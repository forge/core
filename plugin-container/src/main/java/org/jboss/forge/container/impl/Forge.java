package org.jboss.forge.container.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.impl.exception.ContainerException;
import org.jboss.forge.container.impl.modules.AddonModuleLoader;
import org.jboss.forge.container.impl.util.Sets;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;

public final class Forge
{
   private static final String PROP_CONCURRENT_PLUGINS = "forge.concurrentAddons";
   // private static final String PROP_LOG_ENABLE = "forge.logging";

   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, 4);
   // private static final boolean LOGGING_ENABLED = Boolean.getBoolean(PROP_LOG_ENABLE);

   private volatile boolean alive = false;

   Set<AddonThread> threads = Sets.getConcurrentSet();

   Forge()
   {
      if (!AddonUtil.hasRuntimeAPIVersion())
         System.out.println("Warning! Could not detect Forge runtime version - " +
                  "loading all addons, but failures may occur if versions are not compatible.");

      // Module.setModuleLogger(new StreamModuleLogger(System.err));

      // if (LOGGING_ENABLED)
      // initLogging();
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
      Set<Addon> loadedAddons = new HashSet<Addon>();
      for (AddonThread thread : threads)
      {
         loadedAddons.add(thread.getRunnable().getAddon());
      }

      Set<Addon> toStop = new HashSet<Addon>(loadedAddons);
      Set<Addon> updatedSet = loadAddons(addonLoader);
      toStop.removeAll(updatedSet);

      Set<Addon> toStart = new HashSet<Addon>(updatedSet);
      toStart.removeAll(loadedAddons);

      if (toStop.size() > 0)
      {
         System.out.println("Stopping addon(s) " + toStop);
         Set<AddonThread> stopped = new HashSet<AddonThread>();
         for (Addon addon : toStop)
         {
            for (AddonThread thread : threads)
            {
               if (addon.equals(thread.getRunnable().getAddon()))
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
         for (Addon addon : toStart)
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

            AddonRunnable runnable = new AddonRunnable((AddonImpl) addon, registry);
            Thread thread = new Thread(runnable, addon.getId());
            started.add(new AddonThread(thread, runnable));
            thread.start();

            startedThreads++;
         }

         threads.addAll(started);
      }
   }

   public AddonRegistry getAddonRegistry()
   {
      return AddonRegistry.registry;
   }

   protected static void initLogging()
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

   synchronized private Set<Addon> loadAddons(ModuleLoader addonLoader)
   {
      Set<Addon> result = new HashSet<Addon>();

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
            result.add(new AddonImpl(entry, module));
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
