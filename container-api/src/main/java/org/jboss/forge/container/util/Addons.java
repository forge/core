/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.util;

import java.util.concurrent.TimeUnit;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.exception.ContainerException;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Addons
{

   public static void waitUntilStarted(Addon addon)
   {
      try
      {
         while (!addon.getStatus().isStarted())
         {
            Thread.sleep(10);
         }
      }
      catch (Exception e)
      {
         throw new ContainerException("Addon [" + addon + "]  was not started.", e);
      }
   }

   public static void waitUntilStopped(Addon addon)
   {
      try
      {
         while (addon.getStatus().isStarted())
         {
            Thread.sleep(10);
         }
      }
      catch (Exception e)
      {
         throw new ContainerException("Addon [" + addon + "] was not stopped.", e);
      }
   }

   public static void waitUntilStarted(Addon addon, int quantity, TimeUnit unit)
   {
      try
      {
         long start = System.currentTimeMillis();
         while (addon.getStatus().isStarted()
                  && System.currentTimeMillis() < (start + TimeUnit.MILLISECONDS.convert(quantity, unit)))
         {
            Thread.sleep(10);
         }
      }
      catch (Exception e)
      {
         throw new ContainerException("Addon [" + addon + "] was not stopped.", e);
      }
   }

}
