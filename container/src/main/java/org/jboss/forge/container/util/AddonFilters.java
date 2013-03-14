package org.jboss.forge.container.util;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonFilter;

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
