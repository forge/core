/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.impl;

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

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonFilter;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.addons.AddonStatus;
import org.jboss.forge.furnace.addons.AddonTree;
import org.jboss.forge.furnace.addons.CheckDirtyStatusVisitor;
import org.jboss.forge.furnace.addons.MarkDisabledLoadedAddonsDirtyVisitor;
import org.jboss.forge.furnace.addons.StartEnabledAddonsVisitor;
import org.jboss.forge.furnace.addons.StopAllAddonsVisitor;
import org.jboss.forge.furnace.addons.StopDirtyAddonsVisitor;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.lock.LockMode;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.services.ExportedInstance;
import org.jboss.forge.furnace.services.ServiceRegistry;
import org.jboss.forge.furnace.util.AddonFilters;
import org.jboss.forge.furnace.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonRegistryImpl implements AddonRegistry
{
   private static final Logger logger = Logger.getLogger(AddonRegistryImpl.class.getName());

   private final Furnace forge;
   private final LockManager lock;
   private final AddonTree tree;
   private final AtomicInteger starting = new AtomicInteger(-1);

   private final ExecutorService executor = Executors.newCachedThreadPool();

   private final AddonLoader loader;

   public AddonRegistryImpl(Furnace forge)
   {
      Assert.notNull(forge, "Furnace instance must not be null.");
      Assert.notNull(forge.getLockManager(), "LockManager must not be null.");

      this.forge = forge;
      this.lock = forge.getLockManager();
      this.tree = new AddonTree(lock);
      this.loader = new AddonLoader(forge, tree);

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
            return loader.loadAddon(id);
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

   public void forceUpdate()
   {
      lock.performLocked(LockMode.WRITE, new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            if (starting.get() == -1)
               starting.set(0);

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
               loader.loadAddon(addonId);
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
            starting.set(-1);
            return null;
         }
      });
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
      if (starting.get() == -1)
         return false;

      /*
       * Force a full configuration rescan.
       */
      forceUpdate();
      return starting.get() > 0;
   }

   @Override
   public Set<Class<?>> getExportedTypes()
   {
      return lock.performLocked(LockMode.READ, new Callable<Set<Class<?>>>()
      {
         @Override
         public Set<Class<?>> call() throws Exception
         {
            Set<Class<?>> result = new HashSet<Class<?>>();
            for (Addon addon : tree)
            {
               if (AddonStatus.STARTED.equals(addon.getStatus()))
               {
                  ServiceRegistry serviceRegistry = addon.getServiceRegistry();
                  result.addAll(serviceRegistry.getExportedTypes());
               }
            }
            return result;
         }
      });
   }

   @Override
   public <T> Set<Class<T>> getExportedTypes(final Class<T> type)
   {
      return lock.performLocked(LockMode.READ, new Callable<Set<Class<T>>>()
      {
         @Override
         public Set<Class<T>> call() throws Exception
         {
            Set<Class<T>> result = new HashSet<Class<T>>();
            for (Addon addon : tree)
            {
               if (AddonStatus.STARTED.equals(addon.getStatus()))
               {
                  ServiceRegistry serviceRegistry = addon.getServiceRegistry();
                  result.addAll(serviceRegistry.getExportedTypes(type));
               }
            }
            return result;
         }
      });
   }
}
