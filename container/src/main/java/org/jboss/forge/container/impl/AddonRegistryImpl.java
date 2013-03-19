/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl;

import java.util.ArrayList;
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
   private static Logger logger = Logger.getLogger(AddonRegistryImpl.class.getName());
   private static final String PROP_CONCURRENT_PLUGINS = "forge.concurrentAddons";
   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, Runtime.getRuntime()
            .availableProcessors());

   private Forge forge;
   private AddonTree addons;

   private final ExecutorService executor = Executors.newFixedThreadPool(BATCH_SIZE);
   private Map<AddonRepository, AddonModuleLoader> loaders = new ConcurrentHashMap<AddonRepository, AddonModuleLoader>();
   private LockManager lock;

   public AddonRegistryImpl(Forge forge, LockManager lock)
   {
      Assert.notNull(forge, "Forge instance must not be null.");
      Assert.notNull(lock, "LockManager must not be null.");

      this.forge = forge;
      this.lock = lock;
      this.addons = new AddonTree(lock);

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
            ValuedVisitor<AddonImpl, Addon> visitor = new ValuedVisitor<AddonImpl, Addon>()
            {
               @Override
               public void visit(Addon instance)
               {
                  if (instance.getId().equals(id))
                  {
                     setResult((AddonImpl) instance);
                  }
               }
            };

            addons.depthFirst(visitor);

            if (visitor.hasResult())
               return visitor.getResult();
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
            return lock.performLocked(LockMode.READ, new Callable<Set<Addon>>()
            {
               @Override
               public Set<Addon> call() throws Exception
               {
                  ValuedVisitor<Set<Addon>, Addon> visitor = new ValuedVisitor<Set<Addon>, Addon>()
                  {
                     {
                        setResult(new HashSet<Addon>());
                     }

                     @Override
                     public void visit(Addon instance)
                     {
                        if (filter.accept(instance))
                           getResult().add(instance);
                     }
                  };

                  addons.breadthFirst(visitor);

                  return visitor.getResult();
               }
            });
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
            for (Addon addon : addons)
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
            for (Addon addon : addons)
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
            for (Addon addon : addons)
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
            for (Addon addon : addons)
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
      return addons.toString();
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
            AddonImpl addon = loadAddon(id);

            if (addon.getFuture() != null)
            {
               result = addon.getFuture();
            }
            else if (addon.getStatus().isLoaded())
            {
               result = doStart(addon);
            }
            else
            {
               result = new CompletedFuture<Addon>(addon);
            }

            return result;
         }
      });
   }

   private AddonImpl loadAddon(AddonId addonId)
   {
      AddonImpl addon = null;
      for (AddonRepository repository : forge.getRepositories())
      {
         addon = loadAddonFromRepository(repository, addonId);
         if (addon != null)
            break;
      }

      if (addon == null)
      {
         addon = new AddonImpl(lock, addonId);
         addons.add(addon);
      }
      else
      {
         for (Addon registered : addons)
         {
            for (AddonDependency dep : registered.getDependencies())
            {
               if (dep.getDependency().equals(addon))
               {
                  loadAddon(dep.getDependent().getId());
               }
            }
         }
      }

      return addon;
   }

   private AddonImpl loadAddonFromRepository(AddonRepository repository, final AddonId addonId)
   {
      AddonImpl addon = null;
      if (repository.isEnabled(addonId))
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

         addons.depthFirst(visitor);

         addon = visitor.getResult();

         if (addon == null)
         {
            addon = new AddonImpl(lock, addonId);
            addon.setRepository(repository);
            addons.add(addon);
         }

         Set<AddonDependency> dependencies = fromAddonDependencyEntries(addon,
                  repository.getAddonDependencies(addonId));
         addon.setDependencies(dependencies);
         addons.prune();

         if (addon.getModule() == null)
         {
            Set<AddonDependency> missingRequiredDependencies = new HashSet<AddonDependency>();
            for (AddonDependency dependency : addon.getDependencies())
            {
               AddonId dependencyId = dependency.getDependency().getId();

               boolean loaded = false;
               for (Addon a : addons)
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
   public Set<Addon> stop(final Addon addonToStop)
   {
      Assert.notNull(addonToStop, "Addon must not be null.");
      Assert.isTrue(addons.contains(addonToStop), "Addon to stop must originate this AddonRegistry.");

      return lock.performLocked(LockMode.WRITE, new Callable<Set<Addon>>()
      {
         @Override
         public Set<Addon> call() throws Exception
         {
            List<Addon> result = new ArrayList<Addon>();

            final Queue<Addon> toRestart = new LinkedList<Addon>();

            if (addonToStop.getStatus().isStarted())
            {
               ValuedVisitor<List<Addon>, Addon> visitor = new ValuedVisitor<List<Addon>, Addon>()
               {
                  {
                     setResult(new ArrayList<Addon>());
                  }

                  @Override
                  public void visit(Addon instance)
                  {
                     for (AddonDependency dependency : instance.getDependencies())
                     {
                        if (dependency.getDependency().equals(addonToStop)
                                 || getResult().contains(dependency.getDependency()))
                        {
                           getResult().add(instance);

                           if (dependency.isOptional())
                           {
                              toRestart.add(instance);
                           }
                        }
                     }
                  }
               };

               addons.breadthFirst(visitor);

               result.addAll(visitor.getResult());
               result.add(addonToStop);

               for (Addon addon : result)
               {
                  doStop(addon);
               }

               for (Addon addon : toRestart)
               {
                  doStart((AddonImpl) addon);
               }
            }

            return new HashSet<Addon>(result);
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
            addons.breadthFirst(new Visitor<Addon>()
            {
               @Override
               public void visit(Addon addon)
               {
                  doStop(addon);
               }
            });

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

            ((AddonImpl) addon).reset();
         }
      }
   }

   private Future<Addon> doStart(AddonImpl addon)
   {
      if (executor.isShutdown())
      {
         throw new IllegalStateException("Cannot start additional addons once Shutdown has been initiated.");
      }
      AddonRunnable runnable = new AddonRunnable(forge, addon);
      Future<Addon> result = executor.submit(runnable, (Addon) addon);
      addon.setFuture(result);
      addon.setRunnable(runnable);
      return result;
   }
}
