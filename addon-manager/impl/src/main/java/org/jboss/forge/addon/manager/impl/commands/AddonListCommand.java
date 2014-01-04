package org.jboss.forge.addon.manager.impl.commands;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepository;

public class AddonListCommand extends AbstractUICommand implements AddonCommandConstants
{

   @Inject
   private Furnace furnace;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name(ADDON_LIST_COMMAND_NAME)
               .description(ADDON_LIST_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Set<AddonId> choices = new TreeSet<>();
      for (AddonRepository repository : furnace.getRepositories())
      {
         for (AddonId id : repository.listEnabled())
         {
            choices.add(id);
         }
      }

      Iterator<AddonId> iterator = choices.iterator();
      StringBuilder builder = new StringBuilder();
      while (iterator.hasNext())
      {
         AddonId addonId = iterator.next();
         builder.append(addonId.toCoordinates());

         if (iterator.hasNext())
            builder.append("\n");
      }
      return Results.success("Currently installed addons: \n" + builder.toString());
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return !context.getProvider().isGUI();
   }
}
