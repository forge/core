package org.jboss.forge.addon.manager.impl.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;

public class AddonRemoveCommand implements UICommand, AddonCommandConstants
{

   @Inject
   private Furnace forge;

   @Inject
   private AddonManager manager;

   @Inject
   @WithAttributes(label = "Installed addons")
   private UISelectMany<AddonId> addons;

   @Inject
   private ProjectFactory projectFactory;

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name(ADDON_REMOVE_COMMAND_NAME)
               .description(ADDON_REMOVE_COMMAND_DESCRIPTION);
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Set<AddonId> choices = new HashSet<AddonId>();
      for (AddonRepository repository : forge.getRepositories())
      {
         // Avoid immutable repositories
         if (repository instanceof MutableAddonRepository)
         {
            for (AddonId id : repository.listEnabled())
            {
               choices.add(id);
            }
         }
      }
      addons.setValueChoices(choices);
      Project project = getSelectedProject(builder.getUIContext());
      if (project != null)
      {
         MetadataFacet facet = project.getFacet(MetadataFacet.class);
         String name = facet.getTopLevelPackage() + facet.getProjectName();
         AddonId selectedAddonId = AddonId.from(name, facet.getProjectVersion());
         if (choices.contains(selectedAddonId))
         {
            addons.setDefaultValue(Arrays.asList(selectedAddonId));
         }
      }
      builder.add(addons);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
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

   /**
    * Returns the selected project. null if no project is found
    */
   protected Project getSelectedProject(UIContext context)
   {
      Project project = null;
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (initialSelection != null)
      {
         project = projectFactory.findProject(initialSelection.get());
      }
      return project;
   }

}
