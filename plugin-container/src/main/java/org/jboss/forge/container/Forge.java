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
import org.jboss.forge.container.util.Sets;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;

public class Forge
{
   private static final String PROP_CONCURRENT_PLUGINS = "forge.concurrentAddons";
   private static final String PROP_LOG_ENABLE = "forge.logging";

   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, 4);
   private static final boolean LOGGING_ENABLED = Boolean.getBoolean(PROP_LOG_ENABLE);

   private volatile boolean alive = false;

   Set<AddonThread> threads = Sets.getConcurrentSet();

   Forge()
   {
      if (!AddonUtil.hasRuntimeAPIVersion())
         System.out.println("Warning! Could not detect Forge runtime version - " +
                  "loading all addons, but failures may occur if versions are not compatible.");

      // Module.setModuleLogger(new StreamModuleLogger(System.err));

      if (LOGGING_ENABLED)
         initLogging();
   }

   public Set<AddonThread> getThreads()
   {
      return threads;
   }

   public void start()
   {
      if (!alive)
      {
         try
         {
            ModuleLoader addonLoader = new AddonModuleLoader();
            alive = true;
            do
            {
               updateAddons(threads, addonLoader);
               Thread.sleep(10);
            }
            while (alive == true);
         }
         catch (InterruptedException e)
         {
            throw new ContainerException(e);
         }
      }
   }

   public void stop()
   {
      alive = false;
   }

   private void updateAddons(Set<AddonThread> threads, ModuleLoader addonLoader)
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
         System.out.println("Stopping addon(s) " + toStop);
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
         System.out.println("Starting addon(s) " + toStart);
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

   synchronized private Set<Module> loadAddonModules(ModuleLoader addonLoader)
   {
      Set<Module> result = new HashSet<Module>();

      String runtimeVersion = AddonUtil.getRuntimeAPIVersion();
      List<AddonEntry> installed = AddonUtil.listByAPICompatibleVersion(runtimeVersion);

      if (AddonUtil.hasRuntimeAPIVersion())
      {
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
            throw new RuntimeException(e);
         }
         catch (Exception e)
         {
            throw new ContainerException("Failed loading module for addon [" + entry + "]", e);
         }
      }

      return result;
   }

}
