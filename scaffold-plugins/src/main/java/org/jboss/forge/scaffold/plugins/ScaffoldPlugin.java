/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.scaffold.plugins;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.Entity;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.project.services.FacetFactory;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.events.ScaffoldGeneratedResources;
import org.jboss.forge.scaffold.plugins.shell.ScaffoldProviderCompleter;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("scaffold")
@Topic("UI Generation & Scaffolding")
@Help("Faces UI scaffolding")
@RequiresProject
@RequiresFacet(ScaffoldProvider.class)
public class ScaffoldPlugin implements Plugin
{
   @Inject
   @Current
   private Resource<?> currentResource;

   @Inject
   private Configuration config;

   @Inject
   private Project project;

   @Inject
   private ShellPrompt prompt;

   @Inject
   private ShellPrintWriter writer;

   @Inject
   private Event<ScaffoldGeneratedResources> generatedEvent;

   @Inject
   private Event<InstallFacets> installFacets;

   @Inject
   private FacetFactory factory;

   @SetupCommand
   public void setup(
            @Option(name = "targetDir") String targetDir,
            @Option(name = "scaffoldType", required = false, completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite,
            @Option(name = "usingTemplate") final Resource<?> template)
   {
      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      targetDir = selectTargetDir(provider, targetDir);
      verifyTemplate(provider, template);
      List<Resource<?>> generatedResources = provider.setup(targetDir, template, overwrite);

      // TODO give plugins a chance to react to generated resources, use event bus?
      if (!generatedResources.isEmpty())
      {
         generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
      }
   }

   @Command("indexes")
   public void generateIndex(
            @Option(name = "targetDir") String targetDir,
            @Option(name = "scaffoldType", required = false, completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite,
            @Option(name = "usingTemplate") final Resource<?> template)
   {
      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      targetDir = selectTargetDir(provider, targetDir);
      verifyTemplate(provider, template);
      List<Resource<?>> generatedResources = provider.generateIndex(targetDir, template, overwrite);

      // TODO give plugins a chance to react to generated resources, use event bus?
      if (!generatedResources.isEmpty())
      {
         generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
      }
   }

   @Command("templates")
   public void generateTemplates(
            @Option(name = "targetDir") String targetDir,
            @Option(name = "scaffoldType", required = false, completer = ScaffoldProviderCompleter.class) final String scaffoldType,

            @Option(flagOnly = true, name = "overwrite") final boolean overwrite)
   {
      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      targetDir = selectTargetDir(provider, targetDir);
      List<Resource<?>> generatedResources = provider.generateTemplates(targetDir, overwrite);

      // TODO give plugins a chance to react to generated resources, use event bus?
      if (!generatedResources.isEmpty())
      {
         generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
      }
   }

   @Command("from-entity")
   public void generateFromEntity(
            @Option(required = false) JavaResource[] targets,
            @Option(name = "targetDir") String targetDir,
            @Option(name = "scaffoldType", required = false, completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite,
            @Option(name = "usingTemplate") final Resource<?> template) throws FileNotFoundException
   {
      if (((targets == null) || (targets.length < 1)) && (currentResource instanceof JavaResource))
      {
         targets = new JavaResource[] { (JavaResource) currentResource };
      }

      List<JavaResource> javaTargets = selectTargets(targets);
      if (javaTargets.isEmpty())
      {
         ShellMessages.error(writer, "Must specify a domain @Entity on which to operate.");
         return;
      }

      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      targetDir = selectTargetDir(provider, targetDir);
      verifyTemplate(provider, template);

      for (JavaResource jr : javaTargets)
      {
         JavaClass entity = (JavaClass) (jr).getJavaSource();
         List<Resource<?>> generatedResources = provider.generateFromEntity(targetDir, template, entity, overwrite);

         // TODO give plugins a chance to react to generated resources, use event bus?
         if (!generatedResources.isEmpty())
         {
            generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
         }

         ShellMessages.success(writer, "Generated UI for [" + entity.getQualifiedName() + "]");
      }

   }

   private String selectTargetDir(ScaffoldProvider provider, String target)
   {
      if (provider == null)
      {
         throw new RuntimeException("Selected scaffold provider was null. Re-run with '--scaffoldType ...'");
      }

      String targetDirKey = getTargetDirConfigKey(provider);

      if (Strings.isNullOrEmpty(target))
      {
         target = config.getString(targetDirKey);
         if (Strings.isNullOrEmpty(target))
         {
            String finalName = project.getFacet(PackagingFacet.class).getFinalName();
            target = prompt.promptCommon(
                     "Create scaffold in which sub-directory of web-root? (e.g. http://localhost:8080/"
                              + finalName
                              + "/DIR)", PromptType.FILE_PATH, "/");
         }
      }

      if (!Strings.isNullOrEmpty(target))
      {
         config.setProperty(targetDirKey, target);
         if (!target.startsWith("/"))
            target = "/" + target;
         if (target.endsWith("/"))
            target = target.substring(0, target.length() - 1);
      }
      return target;
   }

   private String getTargetDirConfigKey(ScaffoldProvider provider)
   {
      return provider.getClass().getName() + "_targetDir";
   }

   @SuppressWarnings("unchecked")
   private ScaffoldProvider getScaffoldType(String scaffoldType)
   {
      ScaffoldProvider scaffoldImpl = null;

      Collection<Facet> facets = project.getFacets();
      List<ScaffoldProvider> detectedScaffolds = new ArrayList<ScaffoldProvider>();
      for (Facet facet : facets)
      {
         if (facet instanceof ScaffoldProvider)
         {
            detectedScaffolds.add((ScaffoldProvider) facet);
            if (ConstraintInspector.getName(facet.getClass()).equals(scaffoldType))
            {
               scaffoldImpl = (ScaffoldProvider) facet;
            }
         }
      }

      if (scaffoldImpl == null)
      {
         List<String> detectedScaffoldNames = new ArrayList<String>();
         for (ScaffoldProvider sp : detectedScaffolds)
         {
            detectedScaffoldNames.add(ConstraintInspector.getName(sp.getClass()));
         }

         if (detectedScaffolds.size() > 1)
         {
            String name = prompt.promptChoiceTyped("Use which previously installed scaffold provider?",
                     detectedScaffoldNames, detectedScaffoldNames.get(detectedScaffoldNames.size() - 1));

            for (ScaffoldProvider sp : detectedScaffolds)
            {
               if (name.equals(ConstraintInspector.getName(sp.getClass())))
               {
                  scaffoldImpl = sp;
                  break;
               }
            }
         }
         else if (!detectedScaffolds.isEmpty())
         {
            scaffoldImpl = detectedScaffolds.get(0);
            ShellMessages.info(
                     writer,
                     "Using currently installed scaffold ["
                              + ConstraintInspector.getName(scaffoldImpl.getClass())
                              + "]");
         }
      }

      Set<Class<? extends ScaffoldProvider>> providers = new HashSet<Class<? extends ScaffoldProvider>>();
      for (Class<? extends Facet> type : factory.getFacetTypes())
      {
         if (ScaffoldProvider.class.isAssignableFrom(type))
            providers.add((Class<? extends ScaffoldProvider>) type);
      }
      /*
       * Resolve scaffoldType
       */
      if ((scaffoldImpl == null && scaffoldType == null)
               && prompt.promptBoolean("No scaffold type was selected, use default [JavaServer Faces]?"))
      {

         scaffoldType = "faces";
         for (Class<? extends ScaffoldProvider> type : providers)
         {
            if (ConstraintInspector.getName(type).equals(scaffoldType))
            {
               scaffoldImpl = factory.getFacet(type);
            }
         }
      }
      else if (scaffoldImpl == null && scaffoldType != null)
      {
         for (Class<? extends ScaffoldProvider> type : providers)
         {
            if (ConstraintInspector.getName(type).equals(scaffoldType))
            {
               scaffoldImpl = factory.getFacet(type);
            }
         }
      }

      if (scaffoldImpl == null)
      {
         throw new RuntimeException(
                  "No scaffold installed was detected, and no scaffold type was selected; re-run with '--scaffoldType ...' ");
      }

      /*
       * Perform installation
       */
      if (!project.hasFacet(scaffoldImpl.getClass())
               && prompt.promptBoolean("Scaffold provider [" + scaffoldType + "] is not installed. Install it?"))
      {
         installFacets.fire(new InstallFacets(scaffoldImpl.getClass()));
      }
      else if (!project.hasFacet(scaffoldImpl.getClass()))
      {
         throw new RuntimeException("Aborted.");
      }

      if (project.hasFacet(WebResourceFacet.class))
      {
         FileResource<?> favicon = project.getFacet(WebResourceFacet.class).getWebResource("/favicon.ico");
         if (!favicon.exists())
         {
            favicon.setContents(getClass().getResourceAsStream("/org/jboss/forge/scaffold/favicon.ico"));
         }
      }

      return project.getFacet(scaffoldImpl.getClass());
   }

   private List<JavaResource> selectTargets(Resource<?>[] targets) throws FileNotFoundException
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
                  displaySkippingResourceMsg(entity);
               }
            }
            else
            {
               displaySkippingResourceMsg(entity);
            }
         }
      }
      return results;
   }

   private void displaySkippingResourceMsg(final JavaSource<?> entity)
   {
      ShellMessages.info(writer, "Skipped non-@Entity Java resource [" + entity.getQualifiedName() + "]");
   }

   private List<Resource<?>> prepareResources(final List<Resource<?>> generatedResources)
   {
      List<Integer> nullIndexes = new ArrayList<Integer>();
      for (int i = 0; i < generatedResources.size(); i++)
      {
         Resource<?> r = generatedResources.get(i);
         if (r == null)
         {
            nullIndexes.add(i);
         }
      }

      for (Integer index : nullIndexes)
      {
         generatedResources.remove(index);
      }
      return generatedResources;
   }

   private void verifyTemplate(final ScaffoldProvider provider, final Resource<?> template)
   {
      if (template != null)
      {
         if (!template.exists())
         {
            throw new IllegalArgumentException("Template [" + template.getName()
                     + "] does not exist. You must select a template that exists, or use "
                     + "the default template (do not specify a template.)");
         }

         if (!provider.getTemplateStrategy().compatibleWith(template))
         {
            throw new IllegalStateException("Template [" + template.getName()
                     + "] is not compatible with provider ["
                     + ConstraintInspector.getName(provider.getClass()) + "]");
         }
      }
   }
}
