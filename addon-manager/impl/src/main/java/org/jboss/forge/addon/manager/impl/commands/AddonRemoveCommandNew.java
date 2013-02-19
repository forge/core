package org.jboss.forge.addon.manager.impl.commands;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.Forge;
import org.jboss.forge.ui.UIBuilder;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.base.UICommandMetadataBase;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UISelectMany;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;

@Singleton
public class AddonRemoveCommandNew implements UICommand, AddonCommandConstants
{

   @Inject
   private Forge forge;

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
      return new UICommandMetadataBase(
               ADDON_REMOVE_COMMAND_NAME,
               ADDON_REMOVE_COMMAND_DESCRIPTION);
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      addons.setLabel("Installed addons");
      addons.setValueChoices(forge.getRepository().listEnabled());
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
      while (iterator.hasNext()) {
         AddonId addonId = iterator.next();
         forge.getRepository().disable(addonId);
         forge.getRepository().undeploy(addonId);
      }
      return Results.success("hoorray");
   }

}
