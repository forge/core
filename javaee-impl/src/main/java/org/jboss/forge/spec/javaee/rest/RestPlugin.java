/*
 * Copyright 2012-2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.ws.rs.core.MediaType;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
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
   
   @Inject
   EntityBasedResourceGenerator entityResourceGenerator;
   
   @Inject
   RootAndNestedDTOBasedResourceGenerator dtoResourceGenerator; 

   @SetupCommand
   public void setup(@Option(name = "activatorType", defaultValue = "WEB_XML") RestActivatorType activatorType,
            final PipeOut out)
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
            @Option(required = false) JavaResource[] targets,
            @Option(name = "strategy", defaultValue = "JPA_ENTITY") final ResourceStrategy strategy)
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
      RestGeneratedResources generatedResourcesEvent = new RestGeneratedResources();
      for (JavaResource jr : javaTargets)
      {
         JavaClass entity = (JavaClass) (jr).getJavaSource();

         String idType = resolveIdType(entity);
         if (!Types.isBasicType(idType))
         {
            ShellMessages.error(out, "Skipped class [" + entity.getQualifiedName() + "] because @Id type [" + idType
                     + "] is not supported by endpoint generation.");
            continue;
         }

         JavaClass resource = null;
         if(strategy.equals(ResourceStrategy.JPA_ENTITY))
         {
            resource = entityResourceGenerator.generateFrom(entity, idType, contentType, generatedResourcesEvent);
         }
         else if (strategy.equals(ResourceStrategy.ROOT_AND_NESTED_DTO))
         {
            resource = dtoResourceGenerator.generateFrom(entity, idType, contentType, generatedResourcesEvent);
         }

         generatedResourcesEvent.addToEntities(jr);
         
         if (!java.getJavaResource(resource).exists()
                  || prompt.promptBoolean("Endpoint [" + resource.getQualifiedName() + "] already, exists. Overwrite?"))
         {
            generatedResourcesEvent.addToEndpoints(java.saveJavaSource(resource));
            ShellMessages.success(out, "Generated REST endpoint for [" + entity.getQualifiedName() + "]");

         }
         else
            ShellMessages.info(out, "Aborted REST endpoint generation for [" + entity.getQualifiedName() + "]");
      }
      if (!generatedResourcesEvent.getEntities().isEmpty())
      {
         generatedEvent.fire(generatedResourcesEvent);
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
