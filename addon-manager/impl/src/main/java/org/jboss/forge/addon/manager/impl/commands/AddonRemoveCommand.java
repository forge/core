package org.jboss.forge.addon.manager.impl.commands;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UISelectMany;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Metadata;

public class AddonRemoveCommand implements UICommand, AddonCommandConstants
{

   @Inject
   private Forge forge;

   @Inject
   private AddonManager manager;

   @Inject
   private UISelectMany<AddonId> addons;

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
      addons.setLabel("Installed addons");
      Set<AddonId> choices = new HashSet<AddonId>();
      for (AddonRepository repository : forge.getRepositories())
      {
         for (AddonId id : repository.listEnabled())
         {
            choices.add(id);
         }
      }
      addons.setValueChoices(choices);
      builder.add(addons);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      Iterator<AddonId> iterator = addons.getValue().iterator();
      StringBuilder builder = new StringBuilder();
      while (iterator.hasNext())
      {
         AddonId addonId = iterator.next();
         builder.append(addonId.toCoordinates());

         manager.disable(addonId);
         manager.remove(addonId);

         if (iterator.hasNext())
            builder.append(", ");
      }
      return Results.success("Removed addons: " + builder.toString());
   }

}
