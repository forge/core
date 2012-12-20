package org.jboss.forge.container;

public class AddonFilters
{
   public static AddonFilter allStarted()
   {
      return new AddonFilter()
      {
         @Override
         public boolean accept(Addon addon)
         {
            return Status.STARTED.equals(addon.getStatus());
         }
      };
   }

   public static AddonFilter allWaiting()
   {
      return new AddonFilter()
      {
         @Override
         public boolean accept(Addon addon)
         {
            return Status.WAITING.equals(addon.getStatus());
         }
      };
   }

   public static AddonFilter allNotStarted()
   {
      return new AddonFilter()
      {
         @Override
         public boolean accept(Addon addon)
         {
            return !Status.STARTED.equals(addon.getStatus());
         }
      };
   }

}
