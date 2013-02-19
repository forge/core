package org.jboss.forge.container.util;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonFilter;

public class AddonFilters
{
   public static AddonFilter allStarted()
   {
      return new AddonFilter()
      {
         @Override
         public boolean accept(Addon addon)
         {
            return addon.getStatus().isStarted();
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
            return addon.getStatus().isWaiting();
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
            return !addon.getStatus().isStarted();
         }
      };
   }

}
