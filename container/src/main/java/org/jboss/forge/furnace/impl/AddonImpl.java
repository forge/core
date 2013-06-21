/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.impl;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Future;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonDependency;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonStatus;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.modules.AddonModuleLoader;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.services.ServiceRegistry;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.CompletedFuture;
import org.jboss.forge.furnace.util.Sets;
import org.jboss.modules.Module;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonImpl implements Addon
{
   private static class Memento
   {
      public AddonStatus status = AddonStatus.MISSING;
      public Set<AddonDependency> dependencies = Sets.getConcurrentSet();
      public Set<AddonDependency> missingDependencies = Sets.getConcurrentSet();

      public AddonModuleLoader moduleLoader;
      public Module module;
      public AddonRunnable runnable;
      public Future<Void> future = new CompletedFuture<Void>(null);
      public AddonRepository repository;
      public ServiceRegistry registry;
      public boolean dirty = true;

      @Override
      public String toString()
      {
         return status.toString();
      }

   }

   @SuppressWarnings("unused")
   private final LockManager lock;
   private final AddonId id;
   private Memento state = new Memento();

   public AddonImpl(LockManager lock, AddonId id)
   {
      Assert.notNull(lock, "LockManager must not be null.");
      Assert.notNull(id, "AddonId must not be null.");

      this.id = id;
      this.lock = lock;
   }

   public boolean canBeStarted()
   {
      return getRunnable() == null && getStatus().isLoaded();
   }

   public boolean cancelFuture()
   {
      boolean result = false;
      Future<Void> future = getFuture();
      if (future != null && !future.isDone())
         result = future.cancel(true);
      return result;
   }

   public void reset()
   {
      if (getModuleLoader() != null)
         getModuleLoader().releaseAddonModule(id);
      this.state = new Memento();
   }

   @Override
   public AddonId getId()
   {
      return id;
   }

   @Override
   public Set<AddonDependency> getDependencies()
   {
      return Collections.unmodifiableSet(state.dependencies);
   }

   public Set<AddonDependency> getMutableDependencies()
   {
      return state.dependencies;
   }

   public void setDependencies(Set<AddonDependency> dependencies)
   {
      Assert.notNull(dependencies, "Dependencies must not be null.");

      this.state.dependencies = Sets.getConcurrentSet();
      this.state.dependencies.addAll(dependencies);
   }

   @Override
   public ClassLoader getClassLoader()
   {
      if (state.module != null)
         return state.module.getClassLoader();
      return null;
   }

   public Module getModule()
   {
      return state.module;
   }

   public Addon setModule(Module module)
   {
      this.state.module = module;
      return this;
   }

   public AddonModuleLoader getModuleLoader()
   {
      return state.moduleLoader;
   }

   public void setModuleLoader(AddonModuleLoader moduleLoader)
   {
      this.state.moduleLoader = moduleLoader;
   }

   @Override
   public AddonRepository getRepository()
   {
      return state.repository;
   }

   public void setRepository(AddonRepository repository)
   {
      this.state.repository = repository;
   }

   @Override
   public ServiceRegistry getServiceRegistry()
   {
      return state.registry;
   }

   public Addon setServiceRegistry(ServiceRegistry registry)
   {
      Assert.notNull(registry, "Registry must not be null.");
      this.state.registry = registry;
      return this;
   }

   public void setDirty(boolean dirty)
   {
      this.state.dirty = dirty;
   }

   public boolean isDirty()
   {
      return this.state.dirty;
   }

   @Override
   public AddonStatus getStatus()
   {
      return state.status;
   }

   public Addon setStatus(AddonStatus status)
   {
      Assert.notNull(status, "Status must not be null.");
      this.state.status = status;
      return this;
   }

   public void setMissingDependencies(Set<AddonDependency> missingDependencies)
   {
      Assert.notNull(missingDependencies, "Missing dependencies must not be null.");

      this.state.missingDependencies = Sets.getConcurrentSet();
      this.state.missingDependencies.addAll(missingDependencies);
   }

   public Set<AddonDependency> getMissingDependencies()
   {
      return Collections.unmodifiableSet(state.missingDependencies);
   }

   @Override
   public Future<Void> getFuture()
   {
      return state.future;
   }

   public void setFuture(Future<Void> future)
   {
      Assert.notNull(future, "Future must not be null.");
      this.state.future = future;
   }

   public AddonRunnable getRunnable()
   {
      return state.runnable;
   }

   public void setRunnable(AddonRunnable runnable)
   {
      Assert.notNull(runnable, "Runnable must not be null.");
      this.state.runnable = runnable;
   }

   @Override
   public String toString()
   {
      return getId().toCoordinates() + (state.status == null ? "" : " - " + state.status);
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof Addon))
         return false;
      AddonImpl other = (AddonImpl) obj;
      if (id == null)
      {
         if (other.getId() != null)
            return false;
      }
      else if (!id.equals(other.getId()))
         return false;
      return true;
   }
}
