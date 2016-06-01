/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.jboss.forge.roaster.model.Annotation;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Member;
import org.jboss.forge.roaster.model.Method;
import org.jboss.forge.roaster.model.util.Strings;

/**
 * 
 * Utility class for determining use properties of JPA Entities.
 * 
 * @author <a href="salem.elrahal@gmail.com">Salem Elrahal</a>
 */
public class JPAEntityUtil
{

   public static String resolveIdGetterName(JavaClass<?> entity)
   {
      String result = null;

      for (Member<?> member : entity.getMembers())
      {
         if (member.hasAnnotation(Id.class))
         {
            String name = member.getName();
            String type = null;
            if (member instanceof Method)
            {
               type = ((Method<?, ?>) member).getReturnType().getQualifiedName();
               if (name.startsWith("get"))
               {
                  name = name.substring(3);
               }
            }
            else if (member instanceof Field)
            {
               type = ((Field<?>) member).getType().getQualifiedName();
            }

            if (type != null)
            {
               for (Method<?, ?> method : entity.getMethods())
               {
                  // It's a getter
                  if (method.getParameters().size() == 0
                           && (method.getReturnType() != null && type.equals(method.getReturnType().getQualifiedName())))
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

   public static String getEntityTable(final JavaClass<?> entity)
   {
      String table = entity.getName();
      if (entity.hasAnnotation(Entity.class))
      {
         Annotation<?> a = entity.getAnnotation(Entity.class);
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

   public static String getSelectExpression(JavaClass<?> entity, String entityTable)
   {
      char entityVariable = getJpqlEntityVariable(entityTable);
      StringBuilder expressionBuilder = new StringBuilder();
      expressionBuilder.append("SELECT DISTINCT ");
      expressionBuilder.append(entityVariable);
      expressionBuilder.append(" FROM ");
      expressionBuilder.append(entityTable);
      expressionBuilder.append(" ");
      expressionBuilder.append(entityVariable);

      for (Member<?> member : entity.getMembers())
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
                  associationField = Strings.uncapitalize(name.substring(3));
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

   public static String getIdClause(JavaClass<?> entity, String entityTable)
   {
      for (Member<?> member : entity.getMembers())
      {
         if (member.hasAnnotation(Id.class))
         {
            String memberName = member.getName();
            String id = null;
            if (member instanceof Method)
            {
               // Getters are expected to obey JavaBean conventions
               id = Strings.uncapitalize(memberName.substring(3));
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

   public static String getOrderClause(JavaClass<?> entity, char entityVariable)
   {
      StringBuilder expressionBuilder = new StringBuilder();

      // Add the ORDER BY clause
      for (Member<?> member : entity.getMembers())
      {
         if (member.hasAnnotation(Id.class))
         {
            String memberName = member.getName();
            String id = null;
            if (member instanceof Method)
            {
               // Getters are expected to obey JavaBean conventions
               id = Strings.uncapitalize(memberName.substring(3));
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

   public static String resolveIdType(JavaClass<?> entity)
   {
      for (Member<?> member : entity.getMembers())
      {
         if (member.hasAnnotation(Id.class))
         {
            if (member instanceof Method)
            {
               return ((Method<?, ?>) member).getReturnType().getName();
            }
            if (member instanceof Field)
            {
               return ((Field<?>) member).getType().getName();
            }
         }
      }
      return "Object";
   }
}
