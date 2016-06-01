/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.ui;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.building.BuildException;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.request.InstallRequest;
import org.jboss.forge.furnace.manager.request.RemoveRequest;
import org.jboss.forge.furnace.util.Addons;

public class AddonBuildAndInstallCommand extends AbstractProjectCommand implements AddonCommandConstants
{
   private UIInput<DirectoryResource> projectRoot;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      boolean gui = context.getProvider().isGUI();
      return Metadata.from(super.getMetadata(context), getClass())
               .name(gui ? ADDON_BUILD_INSTALL_COMMAND_NAME : ADDON_BUILD_INSTALL_COMMAND_NAME_NO_GUI)
               .description(ADDON_BUILD_INSTALL_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      projectRoot = factory.createInput("projectRoot", DirectoryResource.class).setLabel("Project directory")
               .setDescription("The location of the project (contains pom.xml)").setRequired(true);

      Project project = getSelectedProject(builder);
      if (project != null)
      {
         projectRoot.setDefaultValue(project.getRoot().reify(DirectoryResource.class));
      }
      builder.add(projectRoot);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      Project project = getProjectFactory().findProject(projectRoot.getValue());
      if (project == null)
      {
         return Results.fail("No project found in root " + projectRoot.getValue().getFullyQualifiedName());
      }
      Coordinate coordinate = project.getFacet(MetadataFacet.class).getOutputDependency().getCoordinate();
      try
      {
         // TODO Project builder should support clean and install in the native API.
         project.getFacet(PackagingFacet.class).createBuilder().runTests(false).addArguments("clean", "install")
                  .build();
      }
      catch (BuildException e)
      {
         return Results.fail("Unable to execute project build", e);
      }

      try
      {
         AddonManager addonManager = SimpleContainer.getServices(getClass().getClassLoader(), AddonManager.class).get();
         AddonId id = AddonId.from(coordinate.getGroupId() + ":" + coordinate.getArtifactId(), coordinate.getVersion());
         RemoveRequest removeRequest = addonManager.remove(id);
         removeRequest.perform();
         AddonRegistry registry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
         Addons.waitUntilStopped(registry.getAddon(id));
         InstallRequest installRequest = addonManager.install(id);
         installRequest.perform();
         // Invalidate project cache
         getProjectFactory().invalidateCaches();
         return Results.success("Addon " + coordinate.toString() + " was installed successfully.");
      }
      catch (Throwable t)
      {
         return Results.fail("Addon " + coordinate.toString() + " could not be installed.", t);
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
