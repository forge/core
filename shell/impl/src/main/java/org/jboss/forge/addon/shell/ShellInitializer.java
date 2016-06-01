/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.container.cdi.events.Local;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * Starts up the shell if not in the IDE
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class ShellInitializer
{
   private Shell shell;

   @Inject
   private ShellFactory shellFactory;

   @Inject
   private Furnace furnace;

   public void startupDefaultShell(@Observes @Local PostStartup startup) throws Exception
   {
      if (Boolean.getBoolean("forge.shell.evaluate"))
      {
         String command = "";
         String[] args = furnace.getArgs();

         for (int i = 0; i < args.length; i++)
         {
            String arg = args[i];
            if ("-e".equals(arg) || "--evaluate".equals(arg))
            {
               command = args[++i];
               break;
            }
         }

         if (!command.endsWith(OperatingSystemUtils.getLineSeparator()))
         {
            command = command + OperatingSystemUtils.getLineSeparator();
         }
         command = command + "exit" + OperatingSystemUtils.getLineSeparator() + "\0";

         Settings settings = new SettingsBuilder().inputStream(new ByteArrayInputStream(command.getBytes()))
                  .outputStream(System.out).outputStreamError(System.err).ansi(false).create();
         this.shell = shellFactory.createShell(OperatingSystemUtils.getWorkingDir(), settings);
      }
      else if (Boolean.getBoolean("forge.standalone"))
      {
         // Starting the shell in a separate thread
         ForkJoinPool.commonPool().submit(() -> {
            Settings settings = new SettingsBuilder().create();
            ShellInitializer.this.shell = shellFactory.createShell(OperatingSystemUtils.getWorkingDir(), settings);
         });
      }
   }

   public void shutdown(@Observes @Local PreShutdown preShutdown)
   {
      destroyShell();
   }

   @PreDestroy
   public void destroyShell()
   {
      if (this.shell != null)
      {
         try
         {
            this.shell.close();
         }
         catch (Exception ignore)
         {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Error while closing Shell", ignore);
         }
         this.shell = null;
      }
   }
}
