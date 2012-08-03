/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.ws.rs.Path;
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
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.project.ProjectScoped;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.spec.javaee.JTAFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.RestActivatorType;
import org.jboss.forge.spec.javaee.RestApplicationFacet;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.template.CompiledTemplateResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("rest")
@RequiresFacet(RestFacet.class)
@RequiresProject
public class RestPlugin implements Plugin
{
   @Inject
   private Project project;

   @Inject
   private Event<InstallFacets> request;

   @Inject
   @Current
   private Resource<?> currentResource;

   @Inject
   private TemplateCompiler compiler;

   @Inject
   private ShellPrompt prompt;

   @Inject
   @ProjectScoped
   Configuration configuration;

   @SetupCommand
   public void setup(@Option(name = "activatorType") RestActivatorType activatorType, final PipeOut out)
   {
      if (!project.hasFacet(RestFacet.class))
      {
         request.fire(new InstallFacets(RestFacet.class));
      }

      String rootpath = prompt.prompt("What root path do you want to use for your resources?", "/rest");
      configuration.addProperty(RestFacet.ROOTPATH, rootpath);

      if (activatorType == null || activatorType == RestActivatorType.WEB_XML
               && !project.hasFacet(RestWebXmlFacetImpl.class))
      {
         request.fire(new InstallFacets(RestWebXmlFacetImpl.class));
      }
      else if (activatorType == RestActivatorType.APP_CLASS && !project.hasFacet(RestApplicationFacet.class))
      {
         String pkg = prompt.promptCommon("In what package do you want to store the Application class?",
                  PromptType.JAVA_PACKAGE);
         String restApplication = prompt.prompt("How do you want to name the Application class?", "RestApplication");
         configuration.addProperty(RestApplicationFacet.REST_APPLICATIONCLASS_PACKAGE, pkg);
         configuration.addProperty(RestApplicationFacet.REST_APPLICATIONCLASS_NAME, restApplication);
         request.fire(new InstallFacets(RestApplicationFacet.class));
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
         ShellMessages.error(out, "Must specify a domain @Entity on which to operate.");
         return;
      }

      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
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

         CompiledTemplateResource template = compiler.compileResource(getClass().getResourceAsStream(
                  "/org/jboss/forge/rest/Endpoint.jv"));

         Map<Object, Object> map = new HashMap<Object, Object>();
         map.put("entity", entity);
         map.put("idType", idType);
         map.put("setIdStatement", idSetterName);
         map.put("contentType", contentType);
         map.put("entityTable", getEntityTable(entity));

         JavaClass endpoint = JavaParser.parse(JavaClass.class, template.render(map));
         endpoint.addImport(entity.getQualifiedName());
         endpoint.setPackage(java.getBasePackage() + ".rest");
         endpoint.getAnnotation(Path.class).setStringValue("/" + getEntityTable(entity).toLowerCase());

         /*
          * Save the sources
          */
         java.saveJavaSource(entity);

         if (!java.getJavaResource(endpoint).exists()
                  || prompt.promptBoolean("Endpoint [" + endpoint.getQualifiedName() + "] already, exists. Overwrite?"))
         {
            java.saveJavaSource(endpoint);
            ShellMessages.success(out, "Generated REST endpoint for [" + entity.getQualifiedName() + "]");
         }
         else
            ShellMessages.info(out, "Aborted endpoint generation for [" + entity.getQualifiedName() + "]");
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
                     Parameter param = method.getParameters().get(0);

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
            else if (type != null && result == null && member.isPublic())
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
}
