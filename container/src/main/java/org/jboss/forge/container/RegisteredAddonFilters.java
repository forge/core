package org.jboss.forge.container;

public class RegisteredAddonFilters
{
   public static RegisteredAddonFilter allStarted()
   {
      return new RegisteredAddonFilter()
      {
         @Override
         public boolean accept(RegisteredAddon addon)
         {
            return Status.STARTED.equals(addon.getStatus());
         }
      };
   }

   public static RegisteredAddonFilter allWaiting()
   {
      return new RegisteredAddonFilter()
      {
         @Override
         public boolean accept(RegisteredAddon addon)
         {
            return Status.WAITING.equals(addon.getStatus());
         }
      };
   }

   public static RegisteredAddonFilter allNotStarted()
   {
      return new RegisteredAddonFilter()
      {
         @Override
         public boolean accept(RegisteredAddon addon)
         {
            return !Status.STARTED.equals(addon.getStatus());
         }
      };
   }

}
