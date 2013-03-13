package org.jboss.forge.container.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonFilter;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.addons.Status;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.lock.LockMode;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.Sets;

public class AddonRegistryImpl implements AddonRegistry
{
   private static Logger logger = Logger.getLogger(AddonRegistryImpl.class.getName());
   private static final String PROP_CONCURRENT_PLUGINS = "forge.concurrentAddons";
   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, Runtime.getRuntime()
            .availableProcessors());

   private Forge forge;
   private Set<AddonImpl> addons = Sets.getConcurrentSet();
   private AddonModuleLoader moduleLoader;

   private final ExecutorService executor = Executors.newFixedThreadPool(BATCH_SIZE);

   public AddonRegistryImpl(Forge forge)
   {
      this.forge = forge;
      forge.getAddonRegistries();
   }

   /*
    * LockManager methods
    */
   @Override
   public Lock obtainLock(LockMode mode)
   {
      ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

      if (LockMode.READ.equals(mode))
         return readWriteLock.readLock();
      else
         return readWriteLock.writeLock();
   }

   @Override
   public <T> T performLocked(LockMode mode, Callable<T> task)
   {
      Assert.notNull(mode, "Lock mode must not be null.");
      Assert.notNull(task, "Task to perform must not be null.");

      Lock lock = obtainLock(mode);
      lock.lock();

      T result;
      try
      {
         result = task.call();
      }
      catch (Exception e)
      {
         throw new ContainerException(e);
      }
      finally
      {
         lock.unlock();
      }
      return result;
   }

   /*
    * AddonRegistry methods
    */
   @Override
   public AddonImpl getRegisteredAddon(final AddonId id)
   {
      return performLocked(LockMode.READ, new Callable<AddonImpl>()
      {
         @Override
         public AddonImpl call() throws Exception
         {
            for (AddonImpl addon : addons)
            {
               if (addon.getId().equals(id))
                  return addon;
            }
            return null;
         }
      });
   }

   @Override
   public Set<Addon> getRegisteredAddons()
   {
      return performLocked(LockMode.READ, new Callable<Set<Addon>>()
      {
         @Override
         public Set<Addon> call() throws Exception
         {
            return new HashSet<Addon>(addons);
         }
      });
   }

   @Override
   public Set<Addon> getRegisteredAddons(final AddonFilter filter)
   {
      return performLocked(LockMode.READ, new Callable<Set<Addon>>()
      {
         @Override
         public Set<Addon> call() throws Exception
         {
            Set<Addon> result = new HashSet<Addon>();
            for (Addon registeredAddon : addons)
            {
               if (filter.accept(registeredAddon))
               {
                  result.add(registeredAddon);
               }
            }
            return result;
         }
      });
   }

   @Override
   public boolean isRegistered(final AddonId id)
   {
      return performLocked(LockMode.READ, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return getRegisteredAddon(id) != null;
         }
      });
   }

   @Override
   public Map<Addon, ServiceRegistry> getServiceRegistries()
   {
      return performLocked(LockMode.READ, new Callable<Map<Addon, ServiceRegistry>>()
      {
         @Override
         public Map<Addon, ServiceRegistry> call() throws Exception
         {
            Map<Addon, ServiceRegistry> services = new HashMap<Addon, ServiceRegistry>();
            for (Addon addon : addons)
            {
               services.put(addon, addon.getServiceRegistry());
            }
            return services;
         }
      });
   }

   public void removeServices(final ClassLoader classLoader) throws IllegalArgumentException
   {
      performLocked(LockMode.WRITE, new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            Iterator<AddonImpl> it = addons.iterator();
            while (it.hasNext())
            {
               Addon addon = it.next();
               if (addon.getClassLoader().equals(classLoader))
               {
                  it.remove();
               }
            }
            return null;
         }
      });
   }

   @Override
   public <T> Set<ExportedInstance<T>> getExportedInstances(final Class<T> type)
   {
      return performLocked(LockMode.READ, new Callable<Set<ExportedInstance<T>>>()
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
      return performLocked(LockMode.READ, new Callable<Set<ExportedInstance<T>>>()
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
      return performLocked(LockMode.READ, new Callable<ExportedInstance<T>>()
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
      return performLocked(LockMode.READ, new Callable<ExportedInstance<T>>()
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
   public Future<Status> start(Addon addon)
   {
      return performLocked(LockMode.WRITE, new Callable<Future<Status>>()
      {
         @Override
         public Future<Status> call() throws Exception
         {
            // TODO Auto-generated method stub
            return null;
         }
      });
   }

   @Override
   public void stop(Addon addon)
   {
      performLocked(LockMode.WRITE, new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            return null;
         }
      });
   }
}
