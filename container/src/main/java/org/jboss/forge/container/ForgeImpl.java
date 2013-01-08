package org.jboss.forge.container;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.impl.AddonImpl;
import org.jboss.forge.container.impl.AddonRegistryImpl;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.forge.container.util.Sets;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.log.StreamModuleLogger;

public class ForgeImpl implements Forge
{
   private static final String PROP_CONCURRENT_PLUGINS = "forge.concurrentAddons";

   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, 4);

   private Logger logger = Logger.getLogger(getClass().getName());

   private volatile boolean alive = false;

   private AddonRepository repository = AddonRepositoryImpl.forDefaultDirectory();

   private Map<Addon, Set<Addon>> waitlist = new HashMap<Addon, Set<Addon>>();

   private boolean serverMode = true;

   Set<AddonRunnable> runnables = Sets.getConcurrentSet();

   ExecutorService executor;

   public ForgeImpl()
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
      return this;
   }

   @Override
   public Forge startAsync()
   {
      return startAsync(Thread.currentThread().getContextClassLoader());
   }

   @Override
   public Forge startAsync(final ClassLoader loader)
   {
      new Thread()
      {
         @Override
         public void run()
         {
            ForgeImpl.this.start(loader);
         };
      }.start();

      return this;
   }

   @Override
   public Forge start()
   {
      return start(Thread.currentThread().getContextClassLoader());
   }

   @Override
   public Forge start(ClassLoader loader)
   {
      if (!alive)
      {
         try
         {
            ModuleLoader addonLoader = new AddonModuleLoader(repository, loader);
            alive = true;
            do
            {
               updateAddons(addonLoader);
               Thread.sleep(100);
            }
            while (serverMode && alive == true);

            shutdownThreads();
         }
         catch (InterruptedException e)
         {
            throw new ContainerException(e);
         }
      }
      return this;
   }

   private void shutdownThreads()
   {
      for (AddonRunnable runnable : runnables)
      {
         try
         {
            runnable.shutdown();
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
      if (executor != null)
      {
         executor.shutdown();
      }
   }

   @Override
   public Forge stop()
   {
      alive = false;
      return this;
   }

   private void updateAddons(ModuleLoader addonLoader)
   {
      Set<Addon> loadedAddons = new HashSet<Addon>();
      for (AddonRunnable runnable : runnables)
      {
         loadedAddons.add(runnable.getAddon());
      }

      Set<Addon> toStop = new HashSet<Addon>(loadedAddons);
      Set<Addon> updatedSet = loadAddons(addonLoader);
      toStop.removeAll(updatedSet);

      Set<Addon> toStart = new HashSet<Addon>(updatedSet);
      toStart.removeAll(loadedAddons);

      if (!toStop.isEmpty())
      {
         Set<AddonRunnable> stopped = new HashSet<AddonRunnable>();
         for (Addon addon : toStop)
         {
            // TODO This needs to handle dependencies and ordering.
            ((AddonImpl) addon).setStatus(Status.STOPPING);
            logger.info("Stopping addon (" + addon.getId() + ")");
            for (AddonRunnable runnable : runnables)
            {
               if (addon.equals(runnable.getAddon()))
               {
                  runnable.shutdown();
                  stopped.add(runnable);
                  AddonRegistryImpl.INSTANCE.remove(addon);
               }
            }
         }
         runnables.removeAll(stopped);
      }

      if (!toStart.isEmpty())
      {
         Set<AddonRunnable> started = startAddons(toStart);
         runnables.addAll(started);
      }
   }

   private Set<AddonRunnable> startAddons(Set<Addon> toStart)
   {
      Set<AddonRunnable> started = new HashSet<AddonRunnable>();

      executor = Executors.newFixedThreadPool(BATCH_SIZE);
      for (Addon addon : toStart)
      {
         logger.info("Starting addon (" + addon.getId() + ")");
         AddonRunnable runnable = new AddonRunnable(this, (AddonImpl) addon);
         Future<?> future = executor.submit(runnable);
         try
         {
            future.get();
         }
         catch (Exception e)
         {
            throw new ContainerException("Failed to start addon [" + addon + "]", e);
         }
         started.add(runnable);

      }
      return started;
   }

   synchronized private Set<Addon> loadAddons(ModuleLoader addonLoader)
   {
      Set<Addon> result = new HashSet<Addon>();

      String runtimeVersion = AddonRepositoryImpl.getRuntimeAPIVersion();
      List<AddonId> enabledCompatible = repository.listEnabledCompatibleWithVersion(runtimeVersion);

      if (AddonRepositoryImpl.hasRuntimeAPIVersion())
      {
         List<AddonId> incompatible = repository.listEnabled();
         incompatible.removeAll(enabledCompatible);

         for (AddonId entry : incompatible)
         {
            logger.info("Not loading addon [" + entry.getName()
                     + "] because it references Forge API version [" + entry.getApiVersion()
                     + "] which may not be compatible with my current version ["
                     + AddonRepositoryImpl.getRuntimeAPIVersion() + "].");
         }
      }

      for (AddonId entry : enabledCompatible)
      {
         Addon addonToLoad = loadAddon(addonLoader, entry);
         if (Status.STARTING.equals(addonToLoad.getStatus()) || Status.STARTED.equals(addonToLoad.getStatus()))
            result.add(addonToLoad);
      }

      return result;
   }

   private Addon loadAddon(ModuleLoader addonLoader, AddonId addonId)
   {
      AddonRegistryImpl registry = AddonRegistryImpl.INSTANCE;

      AddonImpl addonToLoad = (AddonImpl) registry.getRegisteredAddon(addonId);
      if (addonToLoad == null)
      {
         addonToLoad = new AddonImpl(addonId, Status.STARTING);
         registry.register(addonToLoad);
      }

      if (!(repository.isDeployed(addonId) && repository.isEnabled(addonId)))
      {
         addonToLoad.setStatus(Status.FAILED);
      }
      else if (!waitlist.containsKey(addonToLoad) && (addonToLoad.getModule() == null))
      {
         Set<Addon> missingDependencies = new HashSet<Addon>();
         for (AddonDependency dependency : repository.getAddonDependencies(addonId))
         {
            AddonId dependencyId = dependency.getId();
            if (!registry.isRegistered(dependencyId) && !dependency.isOptional())
            {
               AddonImpl missingDependency = new AddonImpl(dependencyId, null);
               missingDependencies.add(missingDependency);
            }
         }

         if (!missingDependencies.isEmpty())
         {
            waitlist.put(addonToLoad, missingDependencies);
            addonToLoad.setStatus(Status.WAITING);
            logger.warning("Addon [" + addonToLoad + "] has [" + missingDependencies.size()
                     + "] missing dependencies: "
                     + missingDependencies + " and will be not be loaded until all required"
                     + " dependencies are available.");
         }
         else
         {
            try
            {
               Module module = addonLoader.loadModule(ModuleIdentifier.fromString(addonId.toModuleId()));
               addonToLoad.setModule(module);
               addonToLoad.setStatus(Status.STARTING);

               for (Addon waiting : waitlist.keySet())
               {
                  waitlist.get(waiting).remove(addonToLoad);
                  if (waitlist.get(waiting).isEmpty())
                  {
                     ((AddonImpl) registry.getRegisteredAddon(waiting.getId())).setStatus(Status.STARTING);
                     waitlist.remove(waiting);
                  }
               }
            }
            catch (Exception e)
            {
               addonToLoad.setStatus(Status.FAILED);
               throw new ContainerException("Failed to load addon [" + addonId + "]", e);
            }
         }
      }
      return addonToLoad;
   }

   @Override
   public Forge setAddonDir(File dir)
   {
      this.repository = AddonRepositoryImpl.forDirectory(dir);
      return this;
   }

   @Override
   public Forge setServerMode(boolean server)
   {
      this.serverMode = server;
      return this;
   }

   @Override
   public File getAddonDir()
   {
      return repository.getRepositoryDirectory();
   }

   @Override
   public AddonRegistry getAddonRegistry()
   {
      return AddonRegistryImpl.INSTANCE;
   }

   @Override
   public AddonRepository getRepository()
   {
      return repository;
   }

   @Override
   public String getVersion()
   {
      return AddonRepositoryImpl.getRuntimeAPIVersion();
   }

}
