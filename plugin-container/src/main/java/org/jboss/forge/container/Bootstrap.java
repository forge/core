/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.AddonUtil.AddonEntry;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Bootstrap
{
   public static final String PROP_CONCURRENT_PLUGINS = "forge.concurrentAddons";
   public static final String PROP_LOG_ENABLE = "forge.logging";

   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, 4);
   private static final boolean LOGGING_ENABLED = Boolean.getBoolean(PROP_LOG_ENABLE);

   public static void main(final String[] args)
   {
      init();
   }

   private static void init()
   {
      if (LOGGING_ENABLED)
         initLogging();

      try
      {
         ModuleLoader bootLoader = Module.getBootModuleLoader();
         Module forge = bootLoader.loadModule(ModuleIdentifier.fromString("org.jboss.forge:main"));
         ControlRunnable controlRunnable = new ControlRunnable(forge);
         Thread controlThread = new Thread(controlRunnable, forge.getIdentifier().getName() + ":"
                  + forge.getIdentifier().getSlot());
         controlThread.start();

         ModuleLoader addonLoader = new AddonModuleLoader();
         Set<AddonThread> threads = new HashSet<AddonThread>();
         boolean alive;
         do
         {
            updateAddons(threads, addonLoader);

            Thread.sleep(10);
            alive = controlThread.isAlive();
         }
         while (alive == true);
      }
      catch (ModuleLoadException e)
      {
         throw new ContainerException(e);
      }
      catch (InterruptedException e)
      {
         throw new ContainerException(e);
      }
   }

   private static void updateAddons(Set<AddonThread> threads, ModuleLoader addonLoader)
   {
      Set<Module> loadedAddons = new HashSet<Module>();
      for (AddonThread thread : threads)
      {
         loadedAddons.add(thread.getModule());
      }

      Set<Module> toStop = new HashSet<Module>(loadedAddons);
      Set<Module> updatedSet = loadAddonModules(addonLoader);
      toStop.removeAll(updatedSet);

      Set<Module> toStart = new HashSet<Module>(updatedSet);
      toStart.removeAll(loadedAddons);

      if (toStop.size() > 0)
      {
         System.out.println("Stopping addons " + toStop);
         Set<AddonThread> stopped = new HashSet<AddonThread>();
         for (Module module : toStop)
         {
            for (AddonThread thread : threads)
            {
               if (module.equals(thread.getModule()))
               {
                  thread.getRunnable().shutdown();
                  stopped.add(thread);
               }
            }
         }
         threads.removeAll(stopped);
      }

      if (toStart.size() > 0)
      {
         System.out.println("Starting addons " + toStart);
         Set<AddonThread> started = new HashSet<AddonThread>();
         AddonRegistry registry = AddonRegistry.registry;

         int startedThreads = 0;
         int batchSize = Math.min(BATCH_SIZE, toStart.size());
         for (Module module : toStart)
         {
            while (registry.getServices().size() + batchSize <= startedThreads)
            {
               try
               {
                  Thread.sleep(10);
               }
               catch (InterruptedException e)
               {
                  throw new ContainerException("Thread interrupted while waiting for an executor.", e);
               }
            }

            AddonRunnable runnable = new AddonRunnable(module, registry);
            Thread thread = new Thread(runnable, module.getIdentifier().getName() + ":"
                     + module.getIdentifier().getSlot());
            started.add(new AddonThread(module, thread, runnable));
            thread.start();

            startedThreads++;
         }

         threads.addAll(started);
      }
   }

   private static void initLogging()
   {
      String[] loggerNames = new String[] { "", "main", Logger.GLOBAL_LOGGER_NAME };
      for (String loggerName : loggerNames)
      {
         Logger globalLogger = Logger.getLogger(loggerName);
         Handler[] handlers = globalLogger.getHandlers();
         for (Handler handler : handlers)
         {
            handler.setLevel(Level.SEVERE);
            globalLogger.removeHandler(handler);
         }
      }
   }

   synchronized private static Set<Module> loadAddonModules(ModuleLoader addonLoader)
   {
      Set<Module> result = new HashSet<Module>();

      List<AddonEntry> installed = AddonUtil.listByAPICompatibleVersion(AddonUtil
               .getRuntimeAPIVersion());

      List<AddonEntry> incompatible = AddonUtil.list();
      incompatible.removeAll(installed);

      for (AddonEntry entry : incompatible)
      {
         System.out.println("Not loading addon [" + entry.getName()
                  + "] because it references Forge API version [" + entry.getApiVersion()
                  + "] which may not be compatible with my current version [" + Bootstrap.class.getPackage()
                           .getImplementationVersion() + "]. To remove this addon, type 'forge remove-addon "
                  + entry + ". Otherwise, try installing a new version of the addon.");
      }

      for (AddonEntry entry : installed)
      {
         try
         {
            Module module = addonLoader.loadModule(ModuleIdentifier.fromString(entry.toModuleId()));
            result.add(module);
         }
         catch (ModuleLoadException e)
         {
            System.out.print("*");
         }
         catch (Exception e)
         {
            throw new ContainerException("Failed loading module for addon [" + entry + "]", e);
         }
      }

      return result;
   }
}
