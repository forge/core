/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generator;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.jboss.forge.addon.javaee.rest.generation.RestGenerationContext;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.util.Strings;

/**
 * A utlity class that provides information about the project or the JPA entity. This is to be used in the JAX-RS
 * Resource generators.
 * 
 */
public class ResourceGeneratorUtil
{
   public static String getResourcePath(RestGenerationContext context)
   {
      String packageName = context.getTargetPackageName();
      String entityTable = getEntityTable(context.getEntity());
      Project project = context.getProject();
      String proposedQualifiedClassName = packageName + "." + entityTable + "Endpoint";
      String proposedResourcePath = "/" + context.getInflector().pluralize(entityTable.toLowerCase());
      RestResourceTypeVisitor resourceTypeVisitor = new RestResourceTypeVisitor();
      resourceTypeVisitor.setFound(false);
      resourceTypeVisitor.setProposedPath(proposedResourcePath);
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      while (true)
      {
         facet.visitJavaSources(resourceTypeVisitor);
         if (resourceTypeVisitor.isFound())
         {
            if (proposedQualifiedClassName.equals(resourceTypeVisitor.getQualifiedClassNameForMatch()))
            {
               // The class might be overwritten later, so break out
               break;
            }
            proposedResourcePath = proposedResourcePath.startsWith("/") ? "forge" + proposedResourcePath : "forge/"
                     + proposedResourcePath;
            resourceTypeVisitor.setProposedPath(proposedResourcePath);
            resourceTypeVisitor.setFound(false);
         }
         else
         {
            break;
         }
      }
      return proposedResourcePath;
   }

   public static String resolveIdType(JavaClass entity)
   {
      for (Member<JavaClass, ?> member : entity.getMembers())
      {
         if (member.hasAnnotation(Id.class))
         {
            if (member instanceof Method)
            {
               return ((Method<?>) member).getReturnType();
            }
            if (member instanceof Field)
            {
               return ((Field<?>) member).getType();
            }
         }
      }
      return "Object";
   }

   public static String resolveIdGetterName(JavaClass entity)
   {
      String result = null;

      for (Member<JavaClass, ?> member : entity.getMembers())
      {
         if (member.hasAnnotation(Id.class))
         {
            String name = member.getName();
            String type = null;
            if (member instanceof Method)
            {
               type = ((Method<?>) member).getReturnType();
               if (name.startsWith("get"))
               {
                  name = name.substring(2);
               }
            }
            else if (member instanceof Field)
            {
               type = ((Field<?>) member).getType();
            }

            if (type != null)
            {
               for (Method<JavaClass> method : entity.getMethods())
               {
                  // It's a getter
                  if (method.getParameters().size() == 0 && type.equals(method.getReturnType()))
                  {
                     if (method.getName().toLowerCase().contains(name.toLowerCase()))
                     {
                        result = method.getName() + "()";
                        break;
                     }
                  }
               }
            }

            if (result != null)
            {
               break;
            }
            else if (type != null && member.isPublic())
            {
               String memberName = member.getName();
               // Cheat a little if the member is public
               if (member instanceof Method && memberName.startsWith("get"))
               {
                  memberName = memberName.substring(3);
                  memberName = Strings.uncapitalize(memberName);
               }
               result = memberName;
            }
         }
      }

      if (result == null)
      {
         throw new RuntimeException("Could not determine @Id field and getter method for @Entity ["
                  + entity.getQualifiedName()
                  + "]. Aborting.");
      }

      return result;
   }

   public static String getEntityTable(final JavaClass entity)
   {
      String table = entity.getName();
      if (entity.hasAnnotation(Entity.class))
      {
         Annotation<JavaClass> a = entity.getAnnotation(Entity.class);
         if (!Strings.isNullOrEmpty(a.getStringValue("name")))
         {
            table = a.getStringValue("name");
         }
         else if (!Strings.isNullOrEmpty(a.getStringValue()))
         {
            table = a.getStringValue();
         }
      }
      return table;
   }

   public static String getSelectExpression(JavaClass entity, String entityTable)
   {
      char entityVariable = getJpqlEntityVariable(entityTable);
      StringBuilder expressionBuilder = new StringBuilder();
      expressionBuilder.append("SELECT DISTINCT ");
      expressionBuilder.append(entityVariable);
      expressionBuilder.append(" FROM ");
      expressionBuilder.append(entityTable);
      expressionBuilder.append(" ");
      expressionBuilder.append(entityVariable);

      for (Member<JavaClass, ?> member : entity.getMembers())
      {
         if (member.hasAnnotation(OneToOne.class) || member.hasAnnotation(OneToMany.class)
                  || member.hasAnnotation(ManyToMany.class) || member.hasAnnotation(ManyToOne.class))
         {
            String name = member.getName();
            String associationField = null;
            if (member instanceof Method)
            {
               if (name.startsWith("get"))
               {
                  associationField = Strings.uncapitalize(name.substring(2));
               }
            }
            else if (member instanceof Field)
            {
               associationField = name;
            }

            if (associationField == null)
            {
               throw new RuntimeException("Could not compute the association field for member:" + member.getName()
                        + " in entity" + entity.getName());
            }
            else
            {
               expressionBuilder.append(" LEFT JOIN FETCH ");
               expressionBuilder.append(entityVariable);
               expressionBuilder.append('.');
               expressionBuilder.append(associationField);
            }
         }
      }

      return expressionBuilder.toString();
   }

   public static String getIdClause(JavaClass entity, String entityTable)
   {
      for (Member<JavaClass, ?> member : entity.getMembers())
      {
         if (member.hasAnnotation(Id.class))
         {
            String memberName = member.getName();
            String id = null;
            if (member instanceof Method)
            {
               // Getters are expected to obey JavaBean conventions
               id = Strings.uncapitalize(memberName.substring(2));
            }
            if (member instanceof Field)
            {
               id = memberName;
            }
            char entityVariable = getJpqlEntityVariable(entityTable);
            return "WHERE " + entityVariable + "." + id + " = " + ":entityId";
         }
      }
      return null;
   }

   public static String getOrderClause(JavaClass entity, char entityVariable)
   {
      StringBuilder expressionBuilder = new StringBuilder();

      // Add the ORDER BY clause
      for (Member<JavaClass, ?> member : entity.getMembers())
      {
         if (member.hasAnnotation(Id.class))
         {
            String memberName = member.getName();
            String id = null;
            if (member instanceof Method)
            {
               // Getters are expected to obey JavaBean conventions
               id = Strings.uncapitalize(memberName.substring(2));
            }
            if (member instanceof Field)
            {
               id = memberName;
            }
            expressionBuilder.append("ORDER BY ");
            expressionBuilder.append(entityVariable);
            expressionBuilder.append('.');
            expressionBuilder.append(id);
         }
      }
      return expressionBuilder.toString();
   }

   public static char getJpqlEntityVariable(String entityTable)
   {
      return entityTable.toLowerCase().charAt(0);
   }
}
