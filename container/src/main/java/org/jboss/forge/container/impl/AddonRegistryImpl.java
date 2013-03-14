package org.jboss.forge.container.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
import org.jboss.forge.container.addons.Status;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.lock.LockManager;
import org.jboss.forge.container.lock.LockMode;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Sets;
import org.jboss.forge.container.versions.SingleVersionRange;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleLoader;

public class AddonRegistryImpl implements AddonRegistry
{
   private static Logger logger = Logger.getLogger(AddonRegistryImpl.class.getName());
   private static final String PROP_CONCURRENT_PLUGINS = "forge.concurrentAddons";
   private static final int BATCH_SIZE = Integer.getInteger(PROP_CONCURRENT_PLUGINS, Runtime.getRuntime()
            .availableProcessors());

   private Forge forge;
   private Set<AddonImpl> addons = Sets.getConcurrentSet();

   private final ExecutorService executor = Executors.newFixedThreadPool(BATCH_SIZE);
   private Map<AddonRepository, AddonModuleLoader> loaders = new ConcurrentHashMap<AddonRepository, AddonModuleLoader>();
   private LockManager lock;

   public AddonRegistryImpl(Forge forge, LockManager lock)
   {
      logger.log(Level.FINE, "Instantiated AddonRegistryImpl: " + this);
      this.forge = forge;
      this.lock = lock;
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
            for (AddonImpl addon : addons)
            {
               if (addon.getId().equals(id))
                  return addon;
            }

            return loadAddon(id);
         }
      });
   }

   @Override
   public Set<Addon> getRegisteredAddons()
   {
      return lock.performLocked(LockMode.READ, new Callable<Set<Addon>>()
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
      return lock.performLocked(LockMode.READ, new Callable<Set<Addon>>()
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
   public Map<Addon, ServiceRegistry> getServiceRegistries()
   {
      return lock.performLocked(LockMode.READ, new Callable<Map<Addon, ServiceRegistry>>()
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
      lock.performLocked(LockMode.WRITE, new Callable<Void>()
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
            for (AddonRepository repository : forge.getRepositories())
            {
               if (repository.isEnabled(id))
               {
               }
            }
            // TODO Auto-generated method stub
            return null;
         }
      });
   }

   private AddonImpl loadAddon(AddonId addonId)
   {
      AddonImpl addon = null;
      for (AddonRepository repository : forge.getRepositories())
      {
         if (repository.isEnabled(addonId))
         {
            for (AddonImpl a : addons)
            {
               if (addonId.equals(a.getId()))
               {
                  addon = a;
               }
            }

            Set<AddonDependency> addonDependencies =
                     fromAddonDependencyEntries(addon, repository.getAddonDependencies(addonId));

            if (addon == null)
            {
               addon = new AddonImpl(lock, addonId, repository, addonDependencies);
            }

            if (addon.getModule() == null)
            {
               Set<AddonDependency> missingRequiredDependencies = new HashSet<AddonDependency>();
               for (AddonDependency dependency : addonDependencies)
               {
                  AddonId dependencyId = dependency.getDependency().getId();

                  boolean available = false;
                  for (Addon a : addons)
                  {
                     if (a.getId().equals(dependencyId) && !a.getStatus().isMissing())
                     {
                        available = true;
                     }
                  }
                  if (!available && !dependency.isOptional())
                  {
                     missingRequiredDependencies.add(dependency);
                  }
               }

               addon.setMissingDependencies(missingRequiredDependencies);

               if (!missingRequiredDependencies.isEmpty())
               {
                  if (!addon.getStatus().isMissing())
                  {
                     logger.warning("Addon [" + addon + "] has [" + missingRequiredDependencies.size()
                              + "] missing dependencies: "
                              + missingRequiredDependencies + " and will be not be loaded until all required"
                              + " dependencies are available.");

                     if (!isRegistered(addonId))
                        addons.add(addon);
                  }
               }
               else
               {
                  try
                  {
                     AddonModuleLoader moduleLoader = getAddonModuleLoader(repository);
                     Module module = moduleLoader.loadModule(addonId);
                     addon.setModule(module);

                     if (!isRegistered(addonId))
                        addons.add(addon);
                  }
                  catch (Exception e)
                  {
                     throw new ContainerException("Failed to load addon [" + addonId + "]", e);
                  }
               }
            }
         }
      }

      if (addon == null)
      {
         addon = new AddonImpl(lock, addonId, null, new HashSet<AddonDependency>());
         addons.add(addon);
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
   public Future<Set<Addon>> stop(Addon addon)
   {
      return lock.performLocked(LockMode.WRITE, new Callable<Future<Set<Addon>>>()
      {
         @Override
         public Future<Set<Addon>> call() throws Exception
         {
            // TODO Auto-generated method stub
            return null;
         }
      });
   }

   public Collection<? extends Future<Addon>> startAll()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public void stopAll()
   {
      // TODO Auto-generated method stub
   }

   private AddonModuleLoader getAddonModuleLoader(AddonRepository repository)
   {
      if (!loaders.containsKey(repository))
         loaders.put(repository, new AddonModuleLoader(repository, forge.getRuntimeClassLoader()));
      return loaders.get(repository);
   }
}
