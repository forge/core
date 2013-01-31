package org.jboss.forge.addon.manager.impl.commands;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.Results;
import org.jboss.forge.ui.UIContext;

@Singleton
public class AddonInstallCommand extends AddonCommand
{

   @Inject
   private AddonManager addonManager;

   @Override
   protected String getName()
   {
      return ADDON_INSTALL_COMMAND_NAME;
   }

   @Override
   protected String getDescription()
   {
      return ADDON_INSTALL_COMMAND_DESCRIPTION;
   }

   @Override
   public Result execute(UIContext context)
   {
      String coordinates = getCoordinates();
      try
      {
         addonManager.install(AddonId.fromCoordinates(coordinates)).perform();
         return Results.success("Addon " + coordinates + " was installed succesfully.");
      }
      catch (Throwable t)
      {
         // TODO it should be possible to add the error to the result as payload
         return Results.fail("Addon " + coordinates + " could not be installed.");
      }
   }

}
