package org.jboss.forge.spec.javaee.rest;

import static org.jboss.forge.spec.javaee.RestApplicationFacet.REST_APPLICATIONCLASS_PACKAGE;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.RestApplicationFacet;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;

public class ResourceGeneratorUtil
{
   @Inject
   private Project project;
   
   @Inject
   private Configuration configuration;
   
   @Inject
   private JavaSourceFacet java;
   
   @Inject
   private RestResourceTypeVisitor resourceTypeVisitor;
   
   @Inject
   private ShellPrompt prompt;

   @Inject
   private ShellPrintWriter writer;
   
   public String getPackageName()
   {
      if (project.hasFacet(RestApplicationFacet.class))
      {
         return configuration.getString(REST_APPLICATIONCLASS_PACKAGE);
      }
      else
      {
         return java.getBasePackage() + ".rest";
      }
   }
   
   public String getResourcePath(String entityTable)
   {
      String proposedQualifiedClassName = getPackageName() + "." + entityTable + "Endpoint";
      String proposedResourcePath = "/" + entityTable.toLowerCase() + "s";
      resourceTypeVisitor.setFound(false);
      resourceTypeVisitor.setProposedPath(proposedResourcePath);
      while (true)
      {
         java.visitJavaSources(resourceTypeVisitor);
         if (resourceTypeVisitor.isFound())
         {
            if (proposedQualifiedClassName.equals(resourceTypeVisitor.getQualifiedClassNameForMatch()))
            {
               // The class might be overwritten later, so break out
               break;
            }
            ShellMessages.warn(writer, "The @Path " + proposedResourcePath + " conflicts with an existing @Path.");
            String computedPath = proposedResourcePath.startsWith("/") ? "forge" + proposedResourcePath : "forge/"
                     + proposedResourcePath;
            proposedResourcePath = prompt.prompt("Provide a different URI path value for the generated resource.",
                     computedPath);
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
   
   public String getPersistenceUnitName()
   {
      // This is currently limited to accessing the persistence units of the current project only, and not of
      // dependencies (like an EJB module with a PU that is accessible on the classpath, but not in WEB-INF/lib).
      PersistenceFacet persistence = project.getFacet(PersistenceFacet.class);
      PersistenceDescriptor persistenceDescriptor = persistence.getConfig();
      List<PersistenceUnitDef> units = persistenceDescriptor.listUnits();

      // If there is only one PU, then use it irrespective of whether it excludes unlisted classes or not.
      if (units.size() == 1)
      {
         return units.get(0).getName();
      }
      else
      {
         // Otherwise just prompt the user to choose a PU. It is not wise to choose a PU on behalf of the user using
         // techniques like matching class names in the PU since a class could be present in multiple PUs, including PUs
         // that allow unlisted classes to be managed. In such an event, we may choose the wrong PU, when the user might
         // have wanted the PU with classpath scanning. Letting the user choose the PU to be used for the injected
         // PersistenceContext is safest.
         List<String> unitNames = new ArrayList<String>();
         for (PersistenceUnitDef unitDef : units)
         {
            unitNames.add(unitDef.getName());
         }
         String chosenUnit = prompt
                  .promptChoiceTyped(
                           "Multiple persistence units were detected. Which persistence unit do you want to inject in the REST resources?",
                           unitNames, unitNames.get(0));
         return chosenUnit;
      }
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
