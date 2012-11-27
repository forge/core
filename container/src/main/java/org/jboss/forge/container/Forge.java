package org.jboss.forge.container;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.impl.AddonImpl;
import org.jboss.forge.container.impl.AddonRegistryImpl;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.forge.container.util.Sets;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.log.StreamModuleLogger;

public final class Forge
{
   private static final String PROP_CONCURRENT_PLUGINS = "forge.concurrentAddons";

   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, 4);

   private Logger logger = Logger.getLogger(getClass().getName());

   private volatile boolean alive = false;

   Set<AddonThread> threads = Sets.getConcurrentSet();

   private AddonRepository repository = AddonRepositoryImpl.forDefaultAddonDir();

   public Forge()
   {
      if (!AddonRepositoryImpl.hasRuntimeAPIVersion())
         logger.warning("Could not detect Forge runtime version - " +
                  "loading all addons, but failures may occur if versions are not compatible.");

      installMBeanServer();
   }

   private void installMBeanServer()
   {
      try
      {
         Method method = ModuleLoader.class.getDeclaredMethod("installMBeanServer");
         method.setAccessible(true);
         method.invoke(null);
      }
      catch (Exception e)
      {
         throw new ContainerException("Could not install Modules MBean server", e);
      }
   }

   public Forge enableLogging()
   {
      Module.setModuleLogger(new StreamModuleLogger(System.err));
      // if (LOGGING_ENABLED)
      // initLogging();
      return this;
   }

   public Set<AddonThread> getThreads()
   {
      return threads;
   }

   public Forge start()
   {
      if (!alive)
      {
         try
         {
            ModuleLoader addonLoader = new AddonModuleLoader(repository);
            alive = true;
            do
            {
               updateAddons(threads, addonLoader);
               Thread.sleep(100);
            }
            while (alive == true);
         }
         catch (InterruptedException e)
         {
            throw new ContainerException(e);
         }
      }
      return this;
   }

   public Forge stop()
   {
      alive = false;
      return this;
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

      if (!toStop.isEmpty())
      {
         logger.info("Stopping addon(s) " + toStop);
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

      if (!toStart.isEmpty())
      {
         Set<AddonThread> started = startAddons(toStart);

         threads.addAll(started);
      }
   }

   private Set<AddonThread> startAddons(Set<Addon> toStart)
   {
      logger.info("Starting addon(s) " + toStart);
      Set<AddonThread> started = new HashSet<AddonThread>();
      AddonRegistryImpl registry = AddonRegistryImpl.registry;

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

         AddonRunnable runnable = new AddonRunnable(this, (AddonImpl) addon, registry);
         Thread thread = new Thread(runnable, addon.getId());
         started.add(new AddonThread(thread, runnable));
         thread.start();

         startedThreads++;
      }
      return started;
   }

   public AddonRegistry getAddonRegistry()
   {
      return AddonRegistryImpl.registry;
   }

   synchronized private Set<Addon> loadAddons(ModuleLoader addonLoader)
   {
      Set<Addon> result = new HashSet<Addon>();

      String runtimeVersion = AddonRepositoryImpl.getRuntimeAPIVersion();
      List<AddonEntry> enabledCompatible = repository.listEnabledCompatibleWithVersion(runtimeVersion);

      if (AddonRepositoryImpl.hasRuntimeAPIVersion())
      {
         List<AddonEntry> incompatible = repository.listEnabled();
         incompatible.removeAll(enabledCompatible);

         for (AddonEntry entry : incompatible)
         {
            logger.info("Not loading addon [" + entry.getName()
                     + "] because it references Forge API version [" + entry.getApiVersion()
                     + "] which may not be compatible with my current version [" + Bootstrap.class.getPackage()
                              .getImplementationVersion() + "].");
         }
      }

      for (AddonEntry entry : enabledCompatible)
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

   public Forge setAddonDir(File dir)
   {
      this.repository = AddonRepositoryImpl.forAddonDir(dir);
      return this;
   }

   public File getAddonDir()
   {
      return repository.getRepositoryDirectory();
   }

   public AddonRepository getRepository()
   {
      return repository;
   }

   public String getVersion()
   {
      return AddonRepositoryImpl.getRuntimeAPIVersion();
   }

}
