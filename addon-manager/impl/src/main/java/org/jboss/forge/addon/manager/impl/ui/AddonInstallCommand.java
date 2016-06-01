/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.spi.AddonDependencyResolver;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.furnace.versions.Version;

public class AddonInstallCommand extends AbstractProjectCommand implements AddonCommandConstants
{
   private UIInput<String> coordinate;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      boolean gui = context.getProvider().isGUI();
      return Metadata.from(super.getMetadata(context), getClass())
               .name(gui ? ADDON_INSTALL_COMMAND_NAME : ADDON_INSTALL_COMMAND_NAME_NO_GUI)
               .description(ADDON_INSTALL_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      coordinate = factory.createInput("coordinate", String.class).setLabel("Coordinate")
               .setDescription("The addon's \"groupId:artifactId,version\" coordinate")
               .setNote("The addon's \"groupId:artifactId,version\" coordinate").setRequired(true);
      ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class)
               .get();
      Project project = Projects.getSelectedProject(projectFactory, builder.getUIContext());
      if (project != null)
      {
         MetadataFacet facet = project.getFacet(MetadataFacet.class);
         Coordinate c = facet.getOutputDependency().getCoordinate();
         coordinate.setDefaultValue(AddonId.from(c.getGroupId() + ":" + c.getArtifactId(), c.getVersion())
                  .toCoordinates());
      }
      List<String> defaultCoords = Arrays.asList(CoordinateUtils.FORGE_ADDON_GROUP_ID);
      coordinate.setCompleter((UIContext context, InputComponent<?, String> input,
               String value) -> {
         Iterable<String> items = Collections.emptySet();
         if (Strings.isNullOrEmpty(value))
         {
            items = defaultCoords;
         }
         return items;
      });

      builder.add(coordinate);

   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      Furnace furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
      AddonManager addonManager = SimpleContainer.getServices(getClass().getClassLoader(), AddonManager.class).get();
      AddonDependencyResolver resolver = SimpleContainer
               .getServices(getClass().getClassLoader(), AddonDependencyResolver.class).get();
      Version version = furnace.getVersion();
      AddonId addonId = CoordinateUtils.resolveCoordinate(coordinate.getValue(), version, resolver);
      try
      {
         addonManager.install(addonId).perform();
         // Invalidate project cache
         getProjectFactory().invalidateCaches();
         return Results.success("Addon " + addonId.toCoordinates() + " was installed successfully.");
      }
      catch (Throwable t)
      {
         return Results.fail(
                  "Addon " + addonId.toCoordinates() + " could not be installed: " + t.getCause().getMessage(), t);
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
