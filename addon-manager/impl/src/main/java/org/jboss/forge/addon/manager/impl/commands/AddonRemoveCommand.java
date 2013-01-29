package org.jboss.forge.addon.manager.impl.commands;

import javax.inject.Inject;

import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.Forge;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.Results;
import org.jboss.forge.ui.UIContext;

public class AddonRemoveCommand extends AddonCommand
{

   @Inject 
   private Forge forge;

   @Override
   protected String getName()
   {
      return ADDON_REMOVE_COMMAND_NAME;
   }

   @Override
   protected String getDescription()
   {
      return ADDON_REMOVE_COMMAND_DESCRIPTION;
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      String coordinates = getCoordinates();
      try
      {
         AddonId addon = AddonId.fromCoordinates(coordinates);
         forge.getRepository().disable(addon);
         forge.getRepository().undeploy(addon);
         return Results.success("Addon " + coordinates + " was removed succesfully.");
      }
      catch (Throwable t)
      {
         // TODO it should be possible to add the error to the result as payload
         return Results.fail("Addon " + coordinates + " could not be removed.");
      }
   }

}
