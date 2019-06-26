/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.bootstrap.listener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.spi.ContainerLifecycleListener;
import org.jboss.forge.furnace.versions.Versions;

public class GreetingListener implements ContainerLifecycleListener
{
   private final Logger logger = Logger.getLogger(getClass().getName());
   private volatile boolean showProgress = true;

   @Override
   public void beforeStart(Furnace furnace) throws ContainerException
   {
      if (furnace.isServerMode())
      {
         StringWriter sw = new StringWriter();
         PrintWriter out = new PrintWriter(sw, true);
         out.println();
         out.println("    _____                    ");
         out.println("   |  ___|__  _ __ __ _  ___ ");
         out.println("   | |_ / _ \\| `__/ _` |/ _ \\  \\\\");
         out.println("   |  _| (_) | | | (_| |  __/  //");
         out.println("   |_|  \\___/|_|  \\__, |\\___| ");
         out.println("                   |__/      ");
         out.println("");
         out.print("JBoss Forge, version [ ");
         out.print(Versions.getImplementationVersionFor(getClass()));
         out.print(" ] - JBoss, by Red Hat, Inc. [ https://forge.jboss.org ]");
         out.println();
         out.println("Hit '<TAB>' for a list of available commands and 'man [cmd]' for help on a specific command.");
         out.println();
         out.println("To quit the shell, type 'exit'.");
         logger.info(sw.toString());
         System.out.println(sw.toString());
         shellProgressInformation();
      }
   }

   @Override
   public void afterStart(Furnace furnace) throws ContainerException
   {
      showProgress = false;
   }

   @Override
   public void beforeStop(Furnace furnace) throws ContainerException
   {
      // Do nothing
   }

   @Override
   public void afterStop(Furnace furnace) throws ContainerException
   {
      // Do nothing
   }

   @Override
   public void beforeConfigurationScan(Furnace furnace) throws ContainerException
   {
      // Do nothing
   }

   @Override
   public void afterConfigurationScan(Furnace furnace) throws ContainerException
   {
      // Do nothing
   }

   private void shellProgressInformation()
   {
      ForkJoinPool commonPool = ForkJoinPool.commonPool();
      commonPool.submit(() -> {
         String anim = "|/-\\";
         int x = 0;
         while (showProgress)
         {
            x++;
            String data = "\r" + anim.charAt(x % anim.length());
            try
            {
               System.out.write(data.getBytes());
               Thread.sleep(50);
            }
            catch (Exception e)
            {
               break;
            }
         }
      });
   }
}
