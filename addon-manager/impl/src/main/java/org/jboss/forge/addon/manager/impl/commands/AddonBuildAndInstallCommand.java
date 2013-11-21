package org.jboss.forge.addon.manager.impl.commands;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.building.BuildException;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.request.InstallRequest;
import org.jboss.forge.furnace.manager.request.RemoveRequest;
import org.jboss.forge.furnace.util.Addons;

public class AddonBuildAndInstallCommand extends AbstractUICommand implements AddonCommandConstants
{

   @Inject
   private AddonManager addonManager;

   @Inject
   @WithAttributes(label = "Project directory", description = "The location of the project (contains pom.xml)", required = true)
   private UIInput<DirectoryResource> projectRoot;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private AddonRegistry registry;

   private Project project;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      boolean gui = context.getProvider().isGUI();
      return Metadata.from(super.getMetadata(context), getClass()).name(gui ? ADDON_BUILD_INSTALL_COMMAND_NAME : ADDON_BUILD_INSTALL_COMMAND_NAME_NO_GUI)
               .description(ADDON_BUILD_INSTALL_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return containsProject(context);
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      project = getSelectedProject(builder.getUIContext());
      if (project != null)
      {
         projectRoot.setDefaultValue(project.getProjectRoot());
      }
      builder.add(projectRoot);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      Coordinate coordinate = project.getFacet(MetadataFacet.class).getOutputDependency().getCoordinate();
      try
      {
         // TODO Project builder should support clean and install in the native API.
         project.getFacet(PackagingFacet.class).createBuilder().addArguments("clean", "install").build();
      }
      catch (BuildException e)
      {
         return Results.fail("Unable to execute project build", e);
      }

      try
      {
         AddonId id = AddonId.from(coordinate.getGroupId() + ":" + coordinate.getArtifactId(), coordinate.getVersion());
         RemoveRequest removeRequest = addonManager.remove(id);
         removeRequest.perform();
         Addons.waitUntilStopped(registry.getAddon(id));
         InstallRequest installRequest = addonManager.install(id);
         installRequest.perform();
         return Results.success("Addon " + coordinate.toString() + " was installed succesfully.");
      }
      catch (Throwable t)
      {
         return Results.fail("Addon " + coordinate.toString() + " could not be installed.", t);
      }
   }

   protected boolean containsProject(UIContext context)
   {
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         return projectFactory.containsProject(initialSelection.get());
      }
      return false;

   }

   /**
    * Returns the selected project. null if no project is found
    */
   protected Project getSelectedProject(UIContext context)
   {
      Project project = null;
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         project = projectFactory.findProject(initialSelection.get());
      }
      return project;
   }
}
