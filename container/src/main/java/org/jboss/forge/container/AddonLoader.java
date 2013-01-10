/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.impl.AddonImpl;
import org.jboss.forge.container.impl.AddonRegistryImpl;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.forge.container.util.Sets;

/**
 * Encapsulates the addon loading process
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public final class AddonLoader
{
   private static final String PROP_CONCURRENT_PLUGINS = "forge.concurrentAddons";
   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, Runtime.getRuntime()
            .availableProcessors());

   private final ExecutorService executor = Executors.newFixedThreadPool(BATCH_SIZE);

   private final Map<Addon, Set<Addon>> waitlist = new HashMap<Addon, Set<Addon>>();
   private final Set<AddonRunnable> runnables = Sets.getConcurrentSet();
   private final Logger logger = Logger.getLogger(getClass().getName());

   private final AddonModuleLoader moduleLoader;

   public AddonLoader(AddonModuleLoader moduleLoader)
   {
      super();
      this.moduleLoader = moduleLoader;
   }

   public void updateAddons()
   {
      // Fetch the loaded addons
      Set<Addon> loadedAddons = new HashSet<Addon>();
      for (AddonRunnable runnable : runnables)
      {
         loadedAddons.add(runnable.getAddon());
      }

      Set<Addon> toStop = new HashSet<Addon>(loadedAddons);
      Set<Addon> updatedSet = loadAddons();
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

      List<Addon> addons = new ArrayList<Addon>(toStart);

      // Ordering by dependencies number
      // TODO: Replace this with a more precise order based on the dependencies
      Collections.sort(addons, new Comparator<Addon>()
      {
         @Override
         public int compare(Addon o1, Addon o2)
         {
            return o1.getDependencies().size() - o2.getDependencies().size();
         }
      });
      for (Addon addon : addons)
      {
         logger.info("Starting addon (" + addon.getId() + ")");
         AddonRunnable runnable = new AddonRunnable(moduleLoader.getRepository().getRepositoryDirectory(),
                  (AddonImpl) addon);
         Future<?> future = executor.submit(runnable);
         try
         {
            future.get();
         }
         catch (Exception e)
         {
            future.cancel(true);
            logger.log(Level.WARNING, "Failed to start addon [" + addon + "]", e);
         }
         started.add(runnable);
      }
      return started;
   }

   synchronized private Set<Addon> loadAddons()
   {
      Set<Addon> result = new HashSet<Addon>();
      AddonRepository repository = moduleLoader.getRepository();
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
         Addon addonToLoad = loadAddon(entry);
         if (Status.STARTING == addonToLoad.getStatus() || Status.STARTED == addonToLoad.getStatus())
         {
            result.add(addonToLoad);
         }
      }

      return result;
   }

   private Addon loadAddon(AddonId addonId)
   {
      AddonRegistryImpl registry = AddonRegistryImpl.INSTANCE;
      AddonRepository repository = moduleLoader.getRepository();
      AddonImpl addonToLoad = (AddonImpl) registry.getRegisteredAddon(addonId);
      if (addonToLoad == null)
      {
         addonToLoad = new AddonImpl(addonId, repository.getAddonDependencies(addonId));
         addonToLoad.setStatus(Status.STARTING);
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
               AddonImpl missingDependency = new AddonImpl(dependencyId);
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
               addonToLoad.setModule(moduleLoader.loadModule(addonId));
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

   public void shutdown()
   {
      for (AddonRunnable runnable : runnables)
      {
         try
         {
            runnable.shutdown();
         }
         catch (Exception e)
         {
            logger.log(Level.FINE, "Error while shutting down", e);
         }
      }
      if (executor != null)
      {
         executor.shutdown();
      }
   }

}
