/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffoldx.plugins;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.project.services.FacetFactory;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffoldx.ScaffoldProvider;
import org.jboss.forge.scaffoldx.events.ScaffoldGeneratedResources;
import org.jboss.forge.scaffoldx.plugins.shell.ScaffoldProviderCompleter;
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
 * The Scaffold-X plugin.
 */
@Alias("scaffold-x")
@Topic("UI Generation & Scaffolding")
@Help("Scaffolding projects from a provided set of resources")
@RequiresProject
@RequiresFacet(ScaffoldProvider.class)
public class ScaffoldXPlugin implements Plugin
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
            @Option(name = "installTemplates") final boolean installTemplates)
   {
      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      targetDir = selectTargetDir(provider, targetDir);
      List<Resource<?>> generatedResources = provider.setup(targetDir, overwrite, installTemplates);

      // TODO give plugins a chance to react to generated resources, use event bus?
      if (!generatedResources.isEmpty())
      {
         generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
      }
   }

   @Command("from")
   public void generateFromResources(
            @Option(required = false) Resource<?>[] resources,
            @Option(name = "targetDir") String targetDir,
            @Option(name = "scaffoldType", required = false, completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite) throws FileNotFoundException
   {
      // Use the current resource if no resources are provided.
      if (((resources == null) || (resources.length < 1)))
      {
         resources = new Resource<?>[] { currentResource };
      }

      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      targetDir = selectTargetDir(provider, targetDir);

      List<Resource<?>> resourceList = Arrays.asList(resources);
      List<Resource<?>> generatedResources = provider.generateFrom(resourceList, targetDir, overwrite);
      // TODO give plugins a chance to react to generated resources, use event bus?
      if (!generatedResources.isEmpty())
      {
         generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
      }

      ShellMessages.success(writer, "Generated scaffold for [" + resourceList + "]");
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

      /*
       * Find an installed project facet that matches the scaffold type.
       */
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

      /*
       * If no detected (installed) scaffold type matches the requested scaffold type,
       * then use the detected scaffold type if there is only one.
       * If there are more than one scaffold types, then prompt to choose one of them.
       *
       * FIXME: This should probably not be done.
       * Say, 'faces' is installed, and 'angularjs' is requested, then faces will be used.
       */
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
            favicon.setContents(getClass().getResourceAsStream("/org/jboss/forge/scaffoldx/favicon.ico"));
         }
      }

      return project.getFacet(scaffoldImpl.getClass());
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
}
