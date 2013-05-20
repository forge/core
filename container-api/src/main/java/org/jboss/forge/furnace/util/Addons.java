/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.exception.ContainerException;

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
         while (!addon.getStatus().isStarted())
         {
            if (System.currentTimeMillis() > (start + TimeUnit.MILLISECONDS.convert(quantity, unit)))
            {
               throw new TimeoutException("Timeout expired waiting for [" + addon + "] to start.");
            }
            Thread.sleep(10);
         }
      }
      catch (RuntimeException re)
      {
         throw re;
      }
      catch (Exception e)
      {
         throw new ContainerException("Addon [" + addon + "] was not started.", e);
      }
   }

   public static void waitUntilStopped(Addon addon, int quantity, TimeUnit unit)
   {
      try
      {
         long start = System.currentTimeMillis();
         while (addon.getStatus().isStarted())
         {
            if (System.currentTimeMillis() > (start + TimeUnit.MILLISECONDS.convert(quantity, unit)))
            {
               throw new TimeoutException("Timeout expired waiting for [" + addon + "] to stop.");
            }
            Thread.sleep(10);
         }
      }
      catch (RuntimeException re)
      {
         throw re;
      }
      catch (Exception e)
      {
         throw new ContainerException("Addon [" + addon + "] was not stopped.", e);
      }
   }

}
