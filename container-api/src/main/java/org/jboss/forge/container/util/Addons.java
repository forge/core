package org.jboss.forge.container.util;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.exception.ContainerException;

public class Addons
{

   public static void waitUntilStarted(Addon addon)
   {
      try
      {
         while (!addon.getStatus().isStarted() && !addon.getStatus().isFailed())
         {
            Thread.sleep(10);
         }
      }
      catch (Exception e)
      {
         throw new ContainerException("Addon was not started.", e);
      }
   }

}
