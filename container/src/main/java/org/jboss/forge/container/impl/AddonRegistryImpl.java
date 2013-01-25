package org.jboss.forge.container.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonFilter;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.container.AddonRunnable;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.Status;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.modules.AddonModuleLoader;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.Sets;
import org.jboss.modules.Module;

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

   private AddonFilter waitingFilter = new AddonFilter()
   {
      @Override
      public boolean accept(Addon addon)
      {
         return addon.getStatus().isWaiting();
      }
   };

   public AddonRegistryImpl(Forge forge)
   {
      this.forge = forge;
   }

   @Override
   public Addon getRegisteredAddon(AddonId id)
   {
      synchronized (addons)
      {
         for (Addon addon : addons)
         {
            if (addon.getId().equals(id))
               return addon;
         }
         return null;
      }
   }

   @Override
   public Set<Addon> getRegisteredAddons()
   {
      synchronized (addons)
      {
         return new HashSet<Addon>(addons);
      }
   }

   @Override
   public Set<Addon> getRegisteredAddons(AddonFilter filter)
   {
      synchronized (addons)
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
   }

   @Override
   public boolean isRegistered(AddonId id)
   {
      return getRegisteredAddon(id) != null;
   }

   @Override
   public Future<Addon> start(Addon addon)
   {
      Assert.notNull(addon, "Addon must not be null.");
      updateAddons();
      AddonImpl addonImpl = (AddonImpl) getRegisteredAddon(addon.getId());

      synchronized (addons)
      {

         if (addonImpl != null)
         {
            Future<Addon> future = addonImpl.getFuture();
            if (addonImpl.getMissingDependencies().isEmpty() && future == null)
            {
               logger.info("Starting addon (" + addon.getId() + ")");
               AddonRunnable runnable = new AddonRunnable(forge, addonImpl);
               future = executor.submit(runnable, addon);
               addonImpl.setFuture(future);
               addonImpl.setRunnable(runnable);
            }
            return addonImpl.getFuture();
         }

         return null;
      }
   }

   @Override
   public void stop(Addon addon)
   {
      Assert.notNull(addon, "Addon must not be null.");
      AddonImpl addonImpl = (AddonImpl) getRegisteredAddon(addon.getId());

      synchronized (addons)
      {

         if (addonImpl != null)
         {
            Future<Addon> future = addonImpl.getFuture();
            try
            {
               if (future != null && addon.getStatus().isStarted())
               {
                  addonImpl.getRunnable().shutdown();
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
            }
         }
      }
   }

   public Set<Future<Addon>> startAll()
   {
      Set<Future<Addon>> result = new LinkedHashSet<Future<Addon>>();
      synchronized (addons)
      {
         updateAddons();
         for (Addon addon : addons)
         {
            if (addon.getStatus().isWaiting())
            {
               result.add(start(addon));
            }
         }
      }
      return result;
   }

   public void stopAll()
   {
      for (AddonImpl addon : addons)
      {
         stop(addon);
      }
      List<Runnable> waiting = executor.shutdownNow();
      if (waiting != null && !waiting.isEmpty())
         logger.info("(" + waiting.size() + ") addons were aborted while loading.");
   }

   public void updateAddons()
   {
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

      Set<Addon> toRemove = new HashSet<Addon>();
      for (Addon addon : addons)
      {
         if (!enabledCompatible.contains(addon.getId()))
         {
            toRemove.add(addon);
         }
      }
      addons.removeAll(toRemove);

      for (AddonId entry : enabledCompatible)
      {
         loadAddon(entry);
      }
   }

   public void clearWaiting(Addon addon)
   {
      Set<Addon> waitingAddons = getRegisteredAddons(waitingFilter);
      for (Addon waiting : waitingAddons)
      {
         Set<AddonDependency> dependencies = ((AddonImpl) waiting).getMissingDependencies();
         for (AddonDependency dependency : dependencies)
         {
            if (addon.getId().equals(dependency.getId()))
            {
               dependencies.remove(dependency);
               break;
            }
         }
      }
   }

   private void loadAddon(AddonId addonId)
   {
      AddonRepository repository = moduleLoader.getRepository();

      AddonImpl addon = null;
      for (AddonImpl a : addons)
      {
         if (addonId.equals(a.getId()))
         {
            addon = a;
         }
      }

      if (addon == null)
      {
         addon = new AddonImpl(addonId, repository.getAddonDependencies(addonId));
      }

      if (addon.getModule() == null)
      {

         Set<AddonDependency> missingDependencies = new HashSet<AddonDependency>();
         for (AddonDependency dependency : repository.getAddonDependencies(addonId))
         {
            AddonId dependencyId = dependency.getId();

            boolean registered = false;
            for (Addon a : addons)
            {
               if (a.getId().equals(dependencyId))
               {
                  registered = true;
               }
            }
            if (!registered && !dependency.isOptional())
            {
               missingDependencies.add(dependency);
            }
         }

         addon.setMissingDependencies(missingDependencies);

         if (!missingDependencies.isEmpty())
         {
            if (!addon.getStatus().isWaiting())
            {
               logger.warning("Addon [" + addon + "] has [" + missingDependencies.size()
                        + "] missing dependencies: "
                        + missingDependencies + " and will be not be loaded until all required"
                        + " dependencies are available.");

               addon.setStatus(Status.WAITING);

               if (!isRegistered(addonId))
                  addons.add(addon);
            }
         }
         else
         {
            try
            {
               Module module = moduleLoader.loadModule(addonId);
               addon.setModule(module);
               addon.setStatus(Status.WAITING);

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

   /*
    * Service Accessors
    */
   @Override
   public Map<Addon, ServiceRegistry> getServiceRegistries()
   {
      Map<Addon, ServiceRegistry> services = new HashMap<Addon, ServiceRegistry>();
      for (Addon addon : addons)
      {
         services.put(addon, addon.getServiceRegistry());
      }
      return services;
   }

   public void removeServices(ClassLoader classLoader) throws IllegalArgumentException
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
   }

   @Override
   public <T> Set<ExportedInstance<T>> getExportedInstances(Class<T> type)
   {
      // TODO This needs to block addon installation/removal;
      Set<ExportedInstance<T>> result = new HashSet<ExportedInstance<T>>();
      for (Addon addon : addons)
      {
         if (Status.STARTED.equals(addon.getStatus()))
         {
            ServiceRegistry serviceRegistry = addon.getServiceRegistry();
            result.addAll((Collection<? extends ExportedInstance<T>>) serviceRegistry.getExportedInstances(type));
         }
      }
      return result;
   }

   @Override
   public <T> Set<ExportedInstance<T>> getExportedInstances(String typeName)
   {
      // TODO This needs to block addon installation/removal;
      Set<ExportedInstance<T>> result = new HashSet<ExportedInstance<T>>();
      for (Addon addon : addons)
      {
         if (addon.getStatus().isStarted())
         {
            ServiceRegistry serviceRegistry = addon.getServiceRegistry();
            Set<ExportedInstance<T>> remoteInstances = serviceRegistry.getExportedInstances(typeName);
            result.addAll(remoteInstances);
         }
      }
      return result;
   }

   @Override
   public <T> ExportedInstance<T> getExportedInstance(Class<T> type)
   {
      // TODO This needs to block addon installation/removal;
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

   @Override
   public <T> ExportedInstance<T> getExportedInstance(String type)
   {
      // TODO This needs to block addon installation/removal;
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

   @Override
   public String toString()
   {
      return addons.toString();
   }

   public void setAddonLoader(AddonModuleLoader moduleLoader)
   {
      this.moduleLoader = moduleLoader;
   }
}
