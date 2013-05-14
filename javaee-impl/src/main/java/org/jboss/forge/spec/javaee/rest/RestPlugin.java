/*
 * Copyright 2012-2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.java.util.Types;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.project.ProjectScoped;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.spec.javaee.JTAFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.RestActivatorType;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.forge.spec.javaee.events.RestGeneratedResources;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:sso@adorsys.de">Sandro Sonntag</a>
 */
@Alias("rest")
@RequiresProject
public class RestPlugin implements Plugin
{
   @Inject
   private Project project;

   @Inject
   private Event<InstallFacets> request;

   @Inject
   private Event<RestGeneratedResources> generatedEvent;

   @Inject
   @Current
   private Resource<?> currentResource;

   @Inject
   private ShellPrompt prompt;

   @Inject
   @ProjectScoped
   Configuration configuration;

   @SetupCommand
   public void setup(@Option(name = "activatorType", defaultValue = "WEB_XML") RestActivatorType activatorType, final PipeOut out)
   {
      if (!project.hasFacet(RestFacet.class))
      {
         configuration.setProperty(RestFacet.ACTIVATOR_CHOICE, activatorType.toString());
         request.fire(new InstallFacets(RestFacet.class));
      }
      
      if (project.hasFacet(RestFacet.class))
      {
         ShellMessages.success(out, "Rest Web Services (JAX-RS) is installed.");
      }
   }

   @SuppressWarnings("unchecked")
   @Command(value = "endpoint-from-entity", help = "Creates a REST endpoint from an existing domain @Entity object")
   public void endpointFromEntity(
            final PipeOut out,
            @Option(name = "contentType", defaultValue = MediaType.APPLICATION_XML, completer = ContentTypeCompleter.class) String contentType,
            @Option(required = false) JavaResource[] targets)
            throws FileNotFoundException
   {
      /*
       * Make sure we have all the features we need for this to work.
       */
      if (!project.hasAllFacets(Arrays.asList(EJBFacet.class, PersistenceFacet.class)))
      {
         request.fire(new InstallFacets(true, JTAFacet.class, EJBFacet.class, PersistenceFacet.class));
      }

      if (((targets == null) || (targets.length < 1))
               && (currentResource instanceof JavaResource))
      {
         targets = new JavaResource[] { (JavaResource) currentResource };
      }

      List<JavaResource> javaTargets = selectTargets(out, targets);
      if (javaTargets.isEmpty())
      {
         throw new IllegalArgumentException("Must specify a domain @Entity on which to operate.");
      }

      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      List<JavaResource> endpoints = new ArrayList<JavaResource>();  // for RestGeneratedResources event
      List<JavaResource> entities  = new ArrayList<JavaResource>();  // for RestGeneratedResources event
      for (JavaResource jr : javaTargets)
      {
         JavaClass entity = (JavaClass) (jr).getJavaSource();
         if (!entity.hasAnnotation(XmlRootElement.class))
            entity.addAnnotation(XmlRootElement.class);

         String idType = resolveIdType(entity);
         if (!Types.isBasicType(idType))
         {
            ShellMessages.error(out, "Skipped class [" + entity.getQualifiedName() + "] because @Id type [" + idType
                     + "] is not supported by endpoint generation.");
            continue;
         }
         String idSetterName = resolveIdSetterName(entity);
         String idGetterName = resolveIdGetterName(entity);

         freemarker.template.Configuration freemarkerConfig = new freemarker.template.Configuration();
         freemarkerConfig.setClassForTemplateLoading(getClass(), "/");
         freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());

         Map<Object, Object> map = new HashMap<Object, Object>();
         map.put("entity", entity);
         map.put("idType", idType);
         map.put("setIdStatement", idSetterName);
         map.put("getIdStatement", idGetterName);
         map.put("contentType", contentType);
         String persistenceUnitName = getPersistenceUnitName();
         String entityTable = getEntityTable(entity);
         String selectExpression = getSelectExpression(entity, entityTable);
         String idClause = getIdClause(entity, entityTable);
         map.put("persistenceUnitName", persistenceUnitName);
         map.put("entityTable", entityTable);
         map.put("selectExpression", selectExpression);
         map.put("idClause", idClause);
         map.put("resourcePath", entityTable.toLowerCase() + "s");

         Writer output = new StringWriter();
         try
         {
            Template templateFile = freemarkerConfig.getTemplate("org/jboss/forge/rest/Endpoint.jv");
            templateFile.process(map, output);
            output.flush();
         }
         catch (IOException ioEx)
         {
            throw new RuntimeException(ioEx);
         }
         catch (TemplateException templateEx)
         {
            throw new RuntimeException(templateEx);
         }

         JavaClass resource = JavaParser.parse(JavaClass.class, output.toString());
         resource.addImport(entity.getQualifiedName());
         resource.setPackage(java.getBasePackage() + ".rest");

         /*
          * Save the sources
          */
         entities.add(java.saveJavaSource(entity));

         if (!java.getJavaResource(resource).exists()
                  || prompt.promptBoolean("Endpoint [" + resource.getQualifiedName() + "] already, exists. Overwrite?"))
         {
        	 endpoints.add(java.saveJavaSource(resource));
            ShellMessages.success(out, "Generated REST endpoint for [" + entity.getQualifiedName() + "]");

         }
         else
            ShellMessages.info(out, "Aborted REST endpoint generation for [" + entity.getQualifiedName() + "]");
      }
      if (! entities.isEmpty())
      {
         generatedEvent.fire(new RestGeneratedResources(entities, endpoints));
      }
   }

   private String resolveIdType(JavaClass entity)
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

   private String resolveIdSetterName(JavaClass entity)
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
                  // It's a setter
                  if (method.getParameters().size() == 1 && method.getReturnType() == null)
                  {
                     Parameter<JavaClass> param = method.getParameters().get(0);

                     // The type matches ID field's type
                     if (type.equals(param.getType()))
                     {
                        if (method.getName().toLowerCase().contains(name.toLowerCase()))
                        {
                           result = method.getName() + "(id)";
                           break;
                        }
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
               result = memberName + " = id";
            }
         }
      }

      if (result == null)
      {
         throw new RuntimeException("Could not determine @Id field and setter method for @Entity ["
                  + entity.getQualifiedName()
                  + "]. Aborting.");
      }

      return result;
   }

   private String resolveIdGetterName(JavaClass entity)
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

   private String getEntityTable(final JavaClass entity)
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

   private String getSelectExpression(JavaClass entity, String entityTable)
   {
      char entityVariable = entityTable.toLowerCase().charAt(0);
      StringBuilder expressionBuilder = new StringBuilder();
      expressionBuilder.append("SELECT ");
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

   private String getIdClause(JavaClass entity, String entityTable)
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
            char entityVariable = entityTable.toLowerCase().charAt(0);
            return "WHERE " + entityVariable + "." + id + " = " + ":entityId";
         }
      }
      return null;
   }

   private List<JavaResource> selectTargets(final PipeOut out, Resource<?>[] targets)
            throws FileNotFoundException
   {
      List<JavaResource> results = new ArrayList<JavaResource>();
      if (targets == null)
      {
         targets = new Resource<?>[] {};
      }
      for (Resource<?> r : targets)
      {
         if (r instanceof JavaResource)
         {
            JavaSource<?> entity = ((JavaResource) r).getJavaSource();
            if (entity instanceof JavaClass)
            {
               if (entity.hasAnnotation(Entity.class))
               {
                  results.add((JavaResource) r);
               }
               else
               {
                  displaySkippingResourceMsg(out, entity);
               }
            }
            else
            {
               displaySkippingResourceMsg(out, entity);
            }
         }
      }
      return results;
   }

   private void displaySkippingResourceMsg(final PipeOut out, final JavaSource<?> entity)
   {
      if (!out.isPiped())
      {
         ShellMessages.info(out, "Skipped non-@Entity Java resource ["
                  + entity.getQualifiedName() + "]");
      }
   }

   private String getPersistenceUnitName()
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
         for(PersistenceUnitDef unitDef: units)
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
}
