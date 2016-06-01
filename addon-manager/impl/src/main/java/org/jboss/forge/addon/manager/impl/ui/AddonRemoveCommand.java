/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;

public class AddonRemoveCommand extends AbstractUICommand implements AddonCommandConstants
{
   private UISelectMany<AddonId> addons;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      boolean gui = context.getProvider().isGUI();
      return Metadata.from(super.getMetadata(context), getClass())
               .name(gui ? ADDON_REMOVE_COMMAND_NAME : ADDON_REMOVE_COMMAND_NAME_NO_GUI)
               .description(ADDON_REMOVE_COMMAND_DESCRIPTION).category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      addons = factory.createSelectMany("addons", AddonId.class).setLabel("Installed addons")
               .setDescription("The installed addons in mutable addon repositories that may be removed")
               .setRequired(true).setRequiredMessage(
                        "The specified addon coordinates were not found within any configured furnace repository");
      Furnace furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
      ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class)
               .get();
      Set<AddonId> choices = new TreeSet<>();
      for (AddonRepository repository : furnace.getRepositories())
      {
         // Avoid immutable repositories
         if (repository instanceof MutableAddonRepository)
         {
            // allow removing addons that are not compatible with our Furnace version.
            for (AddonId id : repository.listAll())
            {
               choices.add(id);
            }
         }
      }
      addons.setValueChoices(choices);
      Project project = Projects.getSelectedProject(projectFactory, builder.getUIContext());
      if (project != null)
      {
         MetadataFacet facet = project.getFacet(MetadataFacet.class);
         String name = facet.getProjectGroupName() + ":" + facet.getProjectName();
         AddonId selectedAddonId = AddonId.from(name, facet.getProjectVersion());
         if (choices.contains(selectedAddonId))
         {
            addons.setDefaultValue(Collections.singleton(selectedAddonId));
         }
      }
      builder.add(addons);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      AddonManager manager = SimpleContainer.getServices(getClass().getClassLoader(), AddonManager.class).get();
      Iterable<AddonId> value = addons.getValue();
      Iterator<AddonId> iterator = value.iterator();
      StringBuilder builder = new StringBuilder();
      while (iterator.hasNext())
      {
         AddonId addonId = iterator.next();
         builder.append(addonId.toCoordinates());

         manager.disable(addonId).perform();
         manager.remove(addonId).perform();

         if (iterator.hasNext())
            builder.append(", ");
      }
      return Results.success("Removed addons: " + builder.toString());
   }
}
