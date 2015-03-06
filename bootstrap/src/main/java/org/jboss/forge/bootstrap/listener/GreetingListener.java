/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.bootstrap.listener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.spi.ContainerLifecycleListener;
import org.jboss.forge.furnace.versions.EmptyVersion;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.forge.furnace.versions.Versions;

public class GreetingListener implements ContainerLifecycleListener
{
   private final Logger logger = Logger.getLogger(getClass().getName());

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
         out.print(getForgeVersion());
         out.print(" ] - JBoss, by Red Hat, Inc. [ http://forge.jboss.org ]");
         out.println();
         logger.info(sw.toString());
         System.out.println(sw.toString());
      }
   }

   @Override
   public void afterStart(Furnace furnace) throws ContainerException
   {
      // Do nothing
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

   /**
    * Returns the Implementation version for the given {@link Class}
    * 
    * TODO: Use the {@link Versions} class when Forge is updated to Furnace 2.15.3.Final+
    * 
    * @param type the {@link Class} with the corresponding package
    * @return {@link Version} representation from the {@link Package#getImplementationVersion()} returned from
    *         {@link Class#getPackage()}
    */
   private Version getForgeVersion()
   {
      String version = getClass().getPackage().getImplementationVersion();
      if (version != null)
      {
         return new SingleVersion(version);
      }

      return EmptyVersion.getInstance();
   }
}
