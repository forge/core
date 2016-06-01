/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.git.GitUtils;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.building.BuildException;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.URLResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.request.InstallRequest;
import org.jboss.forge.furnace.manager.request.RemoveRequest;
import org.jboss.forge.furnace.util.Addons;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.versions.Versions;

/**
 * Installs the addon via Git (needs the git addon installed)
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonGitBuildAndInstallCommand extends AbstractProjectCommand implements AddonCommandConstants
{
   private UIInput<URLResource> url;
   private UIInputMany<String> coordinate;
   private UIInput<String> ref;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      boolean gui = context.getProvider().isGUI();
      return Metadata
               .from(super.getMetadata(context), getClass())
               .name(gui ? ADDON_BUILD_INSTALL_COMMAND_NAME_FROM_GIT : ADDON_BUILD_INSTALL_COMMAND_NAME_FROM_GIT_NO_GUI)
               .description(ADDON_BUILD_INSTALL_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      url = factory.createInput("url", 'u', URLResource.class).setLabel("GIT Repository URL")
               .setDescription("The git repository location").setRequired(true);
      coordinate = factory.createInputMany("coordinate", 'c', String.class).setLabel("Coordinate")
               .setDescription("The coordinates of this addon if multiple addons are available");
      ref = factory.createInput("ref", 'r', String.class).setLabel("Branch/Tag")
               .setDescription("The branch/tag (ref) to use if different from default");
      builder.add(url).add(ref).add(coordinate);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Furnace furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
      AddonRegistry registry = furnace.getAddonRegistry();
      AddonManager addonManager = registry.getServices(AddonManager.class).get();
      ProjectFactory projectFactory = getProjectFactory();
      ResourceFactory resourceFactory = registry.getServices(ResourceFactory.class).get();
      GitUtils gitUtils = registry.getServices(GitUtils.class).get();
      // TODO: Option to save sources?
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource projectRoot = resourceFactory.create(DirectoryResource.class, tempDir);
      UIProgressMonitor progressMonitor = context.getProgressMonitor();
      UIOutput output = context.getUIContext().getProvider().getOutput();
      progressMonitor.beginTask("Installing Addon from Git",
               3 + Math.max(1, Lists.toList(coordinate.getValue()).size()));

      progressMonitor.subTask("Cloning repository in " + tempDir);

      // Clone repository
      cloneTo(gitUtils, projectRoot);

      progressMonitor.worked(1);
      progressMonitor.subTask("Installing project into local repository");
      // Build project
      Project project = projectFactory.findProject(projectRoot);
      if (project == null)
      {
         return Results.fail("No project found in root " + projectRoot.getFullyQualifiedName());
      }
      Coordinate buildCoordinate = project.getFacet(MetadataFacet.class).getOutputDependency().getCoordinate();
      try
      {
         project.getFacet(PackagingFacet.class)
                  .createBuilder()
                  .addArguments("clean", "install", "-Dversion.furnace=" + furnace.getVersion(),
                           "-Dversion.forge=" + Versions.getImplementationVersionFor(getClass()))
                  .runTests(false)
                  .build(output.out(), output.err());
      }
      catch (BuildException e)
      {
         return Results.fail("Unable to execute project build", e);
      }
      progressMonitor.worked(1);
      List<AddonId> ids = new ArrayList<>();
      try
      {
         if (coordinate.hasValue())
         {
            for (String c : coordinate.getValue())
            {
               try
               {
                  ids.add(AddonId.fromCoordinates(c));
               }
               catch (IllegalArgumentException e)
               {
                  ids.add(AddonId.from(c, buildCoordinate.getVersion()));
               }
            }
         }
         else
         {
            ids.add(AddonId.from(buildCoordinate.getGroupId() + ":" + buildCoordinate.getArtifactId(),
                     buildCoordinate.getVersion()));
         }
         for (AddonId id : ids)
         {
            progressMonitor.subTask("Removing previous addon installation (" + id + ")");
            RemoveRequest removeRequest = addonManager.remove(id);
            removeRequest.perform();
            Addons.waitUntilStopped(registry.getAddon(id));
            progressMonitor.worked(1);

            progressMonitor.subTask("Installing addon (" + id + ")");
            InstallRequest installRequest = addonManager.install(id);
            installRequest.perform();
         }
         // Invalidate project cache
         projectFactory.invalidateCaches();
         progressMonitor.done();
         return Results.success("Addon(s) " + ids + " installed successfully.");
      }
      catch (Throwable t)
      {
         return Results.fail("Addon(s) " + ids + " could not be installed: " + t.getMessage(), t);
      }
   }

   private void cloneTo(GitUtils gitUtils, DirectoryResource projectRoot) throws GitAPIException, IOException
   {
      Git git = null;
      try
      {
         git = gitUtils.clone(projectRoot, url.getValue().getFullyQualifiedName());
         if (ref.hasValue())
         {
            String refName = ref.getValue();
            String currentBranch = git.getRepository().getBranch();
            // No need to checkout if the branch name is the same
            if (!currentBranch.equals(refName))
            {
               git.checkout().setCreateBranch(true).setName(refName).setUpstreamMode(SetupUpstreamMode.TRACK)
                        .setStartPoint("origin/" + refName).call();
            }
         }
      }
      finally
      {
         gitUtils.close(git);
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }
}
