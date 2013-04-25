/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonDependency;
import org.jboss.forge.container.addons.AddonDependencyImpl;
import org.jboss.forge.container.addons.AddonFilter;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.addons.AddonStatus;
import org.jboss.forge.container.addons.AddonTree;
import org.jboss.forge.container.addons.CheckDirtyStatusVisitor;
import org.jboss.forge.container.addons.MarkDisabledLoadedAddonsDirtyVisitor;
import org.jboss.forge.container.addons.MarkLoadedAddonsDirtyVisitor;
import org.jboss.forge.container.addons.StartEnabledAddonsVisitor;
import org.jboss.forge.container.addons.StopAllAddonsVisitor;
import org.jboss.forge.container.addons.StopDirtyAddonsVisitor;
import org.jboss.forge.container.lock.LockManager;
import org.jboss.forge.container.lock.LockMode;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.AddonFilters;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.ValuedVisitor;
import org.jboss.forge.container.versions.SingleVersionRange;
import org.jboss.modules.Module;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonRegistryImpl implements AddonRegistry
{
   private static final Logger logger = Logger.getLogger(AddonRegistryImpl.class.getName());

   private final Forge forge;
   private final LockManager lock;
   private final AddonTree tree;
   private final AtomicInteger starting = new AtomicInteger();

   private final ExecutorService executor = Executors.newCachedThreadPool();

   private AddonModuleLoader loader;

   public AddonRegistryImpl(Forge forge)
   {
      Assert.notNull(forge, "Forge instance must not be null.");
      Assert.notNull(forge.getLockManager(), "LockManager must not be null.");

      this.forge = forge;
      this.lock = forge.getLockManager();
      this.tree = new AddonTree(lock);

      logger.log(Level.FINE, "Instantiated AddonRegistryImpl: " + this);
   }

   /*
    * AddonRegistry methods
    */
   @Override
   public AddonImpl getAddon(final AddonId id)
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
   public Set<Addon> getAddons()
   {
      return getAddons(AddonFilters.all());
   }

   @Override
   public Set<Addon> getAddons(final AddonFilter filter)
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
               if (AddonStatus.STARTED.equals(addon.getStatus()))
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
      StringBuilder builder = new StringBuilder();

      Iterator<Addon> iterator = tree.iterator();
      while (iterator.hasNext())
      {
         Addon addon = iterator.next();
         builder.append(addon.toString());
         if (iterator.hasNext())
            builder.append("\n");
      }

      return builder.toString();
   }

   private AddonImpl loadAddon(AddonId addonId)
   {
      Assert.notNull(addonId, "AddonId to load must not be null.");

      AddonImpl addon = null;
      for (Addon existing : tree)
      {
         if (existing.getId().equals(addonId))
         {
            addon = (AddonImpl) existing;
            break;
         }
      }

      if (addon == null)
      {
         for (AddonRepository repository : forge.getRepositories())
         {
            addon = loadAddonFromRepository(repository, addonId);
            if (addon != null)
               break;
         }
      }
      else if (addon.getStatus().isMissing())
      {
         for (AddonRepository repository : forge.getRepositories())
         {
            Addon loaded = loadAddonFromRepository(repository, addonId);
            if (loaded != null && !loaded.getStatus().isMissing())
               break;
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
                  addon.setStatus(AddonStatus.LOADED);

                  System.out.println("Loaded module " + module);
                  tree.depthFirst(new MarkLoadedAddonsDirtyVisitor(tree, addon));

               }
               catch (Exception e)
               {
                  logger.log(Level.FINE, "Failed to load addon [" + addonId + "]", e);
                  // throw new ContainerException("Failed to load addon [" + addonId + "]", e);
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
                  getAddon(entry.getId()), entry.isExported(), entry.isOptional()));
      }
      return result;
   }

   public void forceUpdate()
   {
      lock.performLocked(LockMode.WRITE, new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            Set<AddonId> enabled = getAllEnabled();

            tree.breadthFirst(new MarkDisabledLoadedAddonsDirtyVisitor(tree, enabled));

            CheckDirtyStatusVisitor dirty;
            do
            {
               dirty = new CheckDirtyStatusVisitor();
               tree.breadthFirst(new StopDirtyAddonsVisitor(tree));
               tree.depthFirst(dirty);
            }
            while (dirty.isDirty());

            for (AddonId addonId : enabled)
            {
               loadAddon(addonId);
            }

            tree.depthFirst(new StartEnabledAddonsVisitor(forge, tree, executor, starting, enabled));
            return null;
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
            tree.breadthFirst(new StopAllAddonsVisitor(tree));

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

      if (loader == null)
      {
         loader = new AddonModuleLoader(forge);
      }
      return loader;
   }

   private Set<AddonId> getAllEnabled()
   {
      Set<AddonId> result = new HashSet<AddonId>();
      for (AddonRepository repository : forge.getRepositories())
      {
         for (AddonId enabled : repository.listEnabled())
         {
            result.add(enabled);
         }
      }
      return result;
   }

   public void finishedStarting(AddonImpl addon)
   {
      starting.decrementAndGet();
   }

   /**
    * Returns <code>true</code> if there are currently any Addons being started.
    */
   public boolean isStartingAddons()
   {
      /*
       * Force a full configuration rescan.
       */
      forceUpdate();
      return starting.get() > 0;
   }
}
