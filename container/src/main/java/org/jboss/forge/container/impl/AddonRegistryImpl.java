/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonDependency;
import org.jboss.forge.container.addons.AddonDependencyImpl;
import org.jboss.forge.container.addons.AddonFilter;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.addons.AddonTree;
import org.jboss.forge.container.addons.Status;
import org.jboss.forge.container.lock.LockManager;
import org.jboss.forge.container.lock.LockMode;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.AddonFilters;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.CompletedFuture;
import org.jboss.forge.container.util.ValuedVisitor;
import org.jboss.forge.container.util.Visitor;
import org.jboss.forge.container.versions.SingleVersionRange;
import org.jboss.modules.Module;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonRegistryImpl implements AddonRegistry
{
   private static final Logger logger = Logger.getLogger(AddonRegistryImpl.class.getName());
   private static final String PROP_CONCURRENT_PLUGINS = "forge.concurrentAddons";
   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, Runtime.getRuntime()
            .availableProcessors());

   private final Forge forge;
   private final LockManager lock;
   private final AddonTree tree;

   private final ExecutorService executor = Executors.newFixedThreadPool(BATCH_SIZE);

   private final Map<AddonRepository, AddonModuleLoader> loaders = new ConcurrentHashMap<AddonRepository, AddonModuleLoader>();

   public AddonRegistryImpl(Forge forge, LockManager lock)
   {
      Assert.notNull(forge, "Forge instance must not be null.");
      Assert.notNull(lock, "LockManager must not be null.");

      this.forge = forge;
      this.lock = lock;
      this.tree = new AddonTree(lock);

      logger.log(Level.FINE, "Instantiated AddonRegistryImpl: " + this);
   }

   /*
    * AddonRegistry methods
    */
   @Override
   public AddonImpl getRegisteredAddon(final AddonId id)
   {
      return lock.performLocked(LockMode.READ, new Callable<AddonImpl>()
      {
         @Override
         public AddonImpl call() throws Exception
         {
            return loadAddon(id);
         }
      });
   }

   @Override
   public Set<Addon> getRegisteredAddons()
   {
      return getRegisteredAddons(AddonFilters.all());
   }

   @Override
   public Set<Addon> getRegisteredAddons(final AddonFilter filter)
   {
      return lock.performLocked(LockMode.READ, new Callable<Set<Addon>>()
      {
         @Override
         public Set<Addon> call() throws Exception
         {
            HashSet<Addon> result = new HashSet<Addon>();

            for (Addon addon : tree)
            {
               if (filter.accept(addon))
                  result.add(addon);
            }

            return result;
         }
      });
   }

   @Override
   public boolean isRegistered(final AddonId id)
   {
      return lock.performLocked(LockMode.READ, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return getRegisteredAddon(id) != null;
         }
      });
   }

   @Override
   public <T> Set<ExportedInstance<T>> getExportedInstances(final Class<T> type)
   {
      return lock.performLocked(LockMode.READ, new Callable<Set<ExportedInstance<T>>>()
      {
         @Override
         public Set<ExportedInstance<T>> call() throws Exception
         {
            Set<ExportedInstance<T>> result = new HashSet<ExportedInstance<T>>();
            for (Addon addon : tree)
            {
               if (Status.STARTED.equals(addon.getStatus()))
               {
                  ServiceRegistry serviceRegistry = addon.getServiceRegistry();
                  result.addAll(serviceRegistry.getExportedInstances(type));
               }
            }
            return result;
         }
      });
   }

   @Override
   public <T> Set<ExportedInstance<T>> getExportedInstances(final String type)
   {
      return lock.performLocked(LockMode.READ, new Callable<Set<ExportedInstance<T>>>()
      {
         @Override
         public Set<ExportedInstance<T>> call() throws Exception
         {
            Set<ExportedInstance<T>> result = new HashSet<ExportedInstance<T>>();
            for (Addon addon : tree)
            {
               if (addon.getStatus().isStarted())
               {
                  ServiceRegistry serviceRegistry = addon.getServiceRegistry();
                  Set<ExportedInstance<T>> remoteInstances = serviceRegistry.getExportedInstances(type);
                  result.addAll(remoteInstances);
               }
            }
            return result;
         }
      });
   }

   @Override
   public <T> ExportedInstance<T> getExportedInstance(final Class<T> type)
   {
      return lock.performLocked(LockMode.READ, new Callable<ExportedInstance<T>>()
      {
         @Override
         public ExportedInstance<T> call() throws Exception
         {
            ExportedInstance<T> result = null;
            for (Addon addon : tree)
            {
               if (addon.getStatus().isStarted())
               {
                  ServiceRegistry serviceRegistry = addon.getServiceRegistry();
                  result = serviceRegistry.getExportedInstance(type);
                  if (result != null)
                  {
                     break;
                  }
               }
            }
            return result;
         }
      });
   }

   @Override
   public <T> ExportedInstance<T> getExportedInstance(final String type)
   {
      return lock.performLocked(LockMode.READ, new Callable<ExportedInstance<T>>()
      {
         @Override
         public ExportedInstance<T> call() throws Exception
         {
            ExportedInstance<T> result = null;
            for (Addon addon : tree)
            {
               if (addon.getStatus().isStarted())
               {
                  ServiceRegistry serviceRegistry = addon.getServiceRegistry();
                  result = serviceRegistry.getExportedInstance(type);
                  if (result != null)
                  {
                     break;
                  }
               }
            }
            return result;
         }
      });
   }

   @Override
   public String toString()
   {
      return tree.toString();
   }

   @Override
   public Future<Addon> start(final AddonId id)
   {
      return lock.performLocked(LockMode.WRITE, new Callable<Future<Addon>>()
      {
         @Override
         public Future<Addon> call() throws Exception
         {
            Future<Addon> result = null;
            AddonImpl addonToStart = getRegisteredAddon(id);

            if (addonToStart.getFuture() != null)
            {
               result = addonToStart.getFuture();
            }
            else if (addonToStart.getStatus().isLoaded())
            {
               List<Addon> toStart = new ArrayList<Addon>();
               calculateAddonsToStart(addonToStart, toStart);

               for (Addon addon : toStart)
               {
                  if (addon.getStatus().isStarted())
                  {
                     doStop(getRegisteredAddon(addon.getId()));
                  }
               }

               result = doStart(addonToStart);
               Collections.reverse(toStart);

               for (Addon addon : toStart)
               {
                  loadAddon(addon.getId());
                  if (addon.getStatus().isLoaded())
                  {
                     doStart((AddonImpl) addon);
                  }
               }

            }
            else
            {
               result = new CompletedFuture<Addon>(addonToStart);
            }

            return result;
         }

         private void calculateAddonsToStart(final Addon addonToStart, final List<Addon> toStart)
         {
            if (!toStart.contains(addonToStart))
            {
               Visitor<Addon> visitor = new Visitor<Addon>()
               {
                  @Override
                  public void visit(Addon instance)
                  {
                     for (AddonDependency dependency : instance.getDependencies())
                     {
                        if (!toStart.contains(instance))
                        {
                           if (dependency.getDependency().equals(addonToStart)
                                    || toStart.contains(dependency.getDependency()))
                           {
                              toStart.add(instance);
                              calculateAddonsToStart(instance, toStart);
                           }
                        }
                     }
                  }
               };

               tree.depthFirst(visitor);
            }
         }
      });
   }

   private AddonImpl loadAddon(AddonId addonId)
   {
      Assert.notNull(addonId, "AddonId to load must not be null.");

      AddonImpl addon = null;
      for (AddonRepository repository : forge.getRepositories())
      {
         addon = loadAddonFromRepository(repository, addonId);
         if (addon != null)
            break;
      }

      if (addon == null)
      {
         for (Addon existing : tree)
         {
            if (existing.getId().equals(addonId))
            {
               addon = (AddonImpl) existing;
               break;
            }
         }
      }

      if (addon == null)
      {
         addon = new AddonImpl(lock, addonId);
         tree.add(addon);
      }

      return addon;
   }

   private AddonImpl loadAddonFromRepository(AddonRepository repository, final AddonId addonId)
   {
      AddonImpl addon = null;
      if (repository.isEnabled(addonId) && repository.isDeployed(addonId))
      {
         ValuedVisitor<AddonImpl, Addon> visitor = new ValuedVisitor<AddonImpl, Addon>()
         {
            @Override
            public void visit(Addon instance)
            {
               if (instance.getId().equals(addonId))
               {
                  setResult((AddonImpl) instance);
               }
            }
         };

         tree.depthFirst(visitor);

         addon = visitor.getResult();

         if (addon == null)
         {
            addon = new AddonImpl(lock, addonId);
            addon.setRepository(repository);
            tree.add(addon);
         }

         Set<AddonDependency> dependencies = fromAddonDependencyEntries(addon,
                  repository.getAddonDependencies(addonId));
         addon.setDependencies(dependencies);
         tree.prune();

         if (addon.getModule() == null)
         {
            Set<AddonDependency> missingRequiredDependencies = new HashSet<AddonDependency>();
            for (AddonDependency dependency : addon.getDependencies())
            {
               AddonId dependencyId = dependency.getDependency().getId();

               boolean loaded = false;
               for (Addon a : tree)
               {
                  if (a.getId().equals(dependencyId) && !a.getStatus().isMissing())
                  {
                     loaded = true;
                  }
               }
               if (!loaded && !dependency.isOptional())
               {
                  missingRequiredDependencies.add(dependency);
               }
            }

            if (!missingRequiredDependencies.isEmpty())
            {
               if (addon.getMissingDependencies().size() != missingRequiredDependencies.size())
               {
                  logger.warning("Addon [" + addon + "] has [" + missingRequiredDependencies.size()
                           + "] missing dependencies: "
                           + missingRequiredDependencies + " and will be not be loaded until all required"
                           + " dependencies are available.");
               }
               addon.setMissingDependencies(missingRequiredDependencies);
            }
            else
            {
               try
               {
                  AddonModuleLoader moduleLoader = getAddonModuleLoader(repository);
                  Module module = moduleLoader.loadModule(addonId);
                  addon.setModuleLoader(moduleLoader);
                  addon.setModule(module);
                  addon.setRepository(repository);
                  addon.setStatus(Status.LOADED);
               }
               catch (Exception e)
               {
                  // logger.log(Level.FINE, "Failed to load addon [" + addonId + "]", e);
                  // // throw new ContainerException("Failed to load addon [" + addonId + "]", e);
               }
            }
         }
      }
      return addon;
   }

   private Set<AddonDependency> fromAddonDependencyEntries(AddonImpl addon, Set<AddonDependencyEntry> entries)
   {
      Set<AddonDependency> result = new HashSet<AddonDependency>();
      for (AddonDependencyEntry entry : entries)
      {
         result.add(new AddonDependencyImpl(lock, addon, new SingleVersionRange(entry.getId().getVersion()),
                  getRegisteredAddon(entry.getId()), entry.isExported(), entry.isOptional()));
      }
      return result;
   }

   @Override
   public void stop(final Addon addonToStop)
   {
      Assert.notNull(addonToStop, "Addon must not be null.");
      Assert.isTrue(tree.contains(addonToStop), "Addon to stop must originate this AddonRegistry.");

      lock.performLocked(LockMode.WRITE, new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            final List<Addon> toStop = new ArrayList<Addon>();
            final Queue<Addon> toRestart = new LinkedList<Addon>();

            if (addonToStop.getStatus().isStarted())
            {
               calculateAddonsToStop(addonToStop, toStop, toRestart);
               toRestart.removeAll(toStop);

               Collections.reverse(toStop);
               for (Addon addon : toStop)
               {
                  doStop(addon);
               }

               for (Addon addon : toRestart)
               {
                  doStop(addon);
               }

               doStop(addonToStop);

               for (Addon addon : toRestart)
               {
                  start(addon.getId());
               }
            }

            return null;
         }

         private void calculateAddonsToStop(final Addon addonToStop, final List<Addon> toStop,
                  final Queue<Addon> toRestart)
         {
            Visitor<Addon> visitor = new Visitor<Addon>()
            {
               @Override
               public void visit(Addon instance)
               {
                  if (instance.getStatus().isStarted())
                  {
                     for (AddonDependency dependency : instance.getDependencies())
                     {
                        if (!(toStop.contains(instance) || toRestart.contains(instance)))
                        {
                           if (dependency.getDependency().equals(addonToStop)
                                    || toStop.contains(dependency.getDependency())
                                    || toRestart.contains(dependency.getDependency()))
                           {
                              if (dependency.isOptional())
                                 toRestart.add(instance);
                              else
                                 toStop.add(instance);

                              calculateAddonsToStop(instance, toStop, toRestart);
                           }
                        }
                     }
                  }
               }
            };

            tree.breadthFirst(visitor);
         }
      });
   }

   public Set<Future<Addon>> startAll()
   {
      return lock.performLocked(LockMode.WRITE, new Callable<Set<Future<Addon>>>()
      {
         @Override
         public Set<Future<Addon>> call() throws Exception
         {
            Set<Future<Addon>> result = new LinkedHashSet<Future<Addon>>();
            for (AddonRepository repository : forge.getRepositories())
            {
               for (AddonId enabled : repository.listEnabled())
               {
                  result.add(start(enabled));
               }
            }
            return result;
         }
      });
   }

   public void stopAll()
   {
      lock.performLocked(LockMode.WRITE, new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            final List<Addon> toStop = new ArrayList<Addon>();
            tree.breadthFirst(new Visitor<Addon>()
            {
               @Override
               public void visit(Addon addon)
               {
                  toStop.add(addon);
               }
            });

            for (Addon addon : toStop)
            {
               doStop(addon);
            }

            List<Runnable> waiting = executor.shutdownNow();
            if (waiting != null && !waiting.isEmpty())
               logger.info("(" + waiting.size() + ") addons were aborted while loading.");

            return null;
         }
      });
   }

   private AddonModuleLoader getAddonModuleLoader(AddonRepository repository)
   {
      Assert.notNull(repository, "Repository must not be null.");

      if (!loaders.containsKey(repository))
         loaders.put(repository, new AddonModuleLoader(repository, forge.getRuntimeClassLoader()));
      return loaders.get(repository);
   }

   private void doStop(Addon addon)
   {
      if (addon != null)
      {
         Future<Addon> future = ((AddonImpl) addon).getFuture();
         try
         {
            if (future != null)
            {
               ((AddonImpl) addon).getRunnable().shutdown();
            }
         }
         catch (Exception e)
         {
            logger.log(Level.WARNING, "Failed to shut down addon " + addon, e);
         }
         finally
         {
            if (future != null && !future.isDone())
               future.cancel(true);

            Set<AddonDependency> dependencies = addon.getDependencies();
            ((AddonImpl) addon).reset();

            for (AddonDependency dependency : dependencies)
            {
               tree.reattach(dependency.getDependency());
            }
         }
      }
   }

   private Future<Addon> doStart(AddonImpl addon)
   {
      if (executor.isShutdown())
      {
         throw new IllegalStateException("Cannot start additional addons once Shutdown has been initiated.");
      }

      if (addon.getFuture() == null)
      {
         AddonRunnable runnable = new AddonRunnable(forge, addon);
         Future<Addon> result = executor.submit(runnable, (Addon) addon);
         addon.setFuture(result);
         addon.setRunnable(runnable);
         return result;
      }
      else
      {
         return addon.getFuture();
      }
   }
}
