/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.impl;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonDependency;
import org.jboss.forge.furnace.addons.AddonStatus;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.lock.LockMode;
import org.jboss.forge.furnace.modules.AddonResourceLoader;
import org.jboss.forge.furnace.modules.ModularURLScanner;
import org.jboss.forge.furnace.modules.ModularWeld;
import org.jboss.forge.furnace.modules.ModuleScanResult;
import org.jboss.forge.furnace.services.ServiceRegistry;
import org.jboss.forge.furnace.util.Addons;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.BeanManagerUtils;
import org.jboss.forge.furnace.util.ClassLoaders;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * Loads an {@link Addon}
 */
public final class AddonRunnable implements Runnable
{
   private static final Logger logger = Logger.getLogger(AddonRunnable.class.getName());

   private Furnace forge;
   private AddonImpl addon;
   private AddonContainerStartup container;

   private Callable<Object> shutdownCallable = new Callable<Object>()
   {
      @Override
      public Object call() throws Exception
      {
         addon.setStatus(AddonStatus.LOADED);
         return null;
      }
   };

   public AddonRunnable(Furnace forge, AddonImpl addon)
   {
      this.forge = forge;
      this.addon = addon;
   }

   public void shutdown()
   {
      try
      {
         forge.getLockManager().performLocked(LockMode.READ, new Callable<Void>()
         {
            @Override
            public Void call() throws Exception
            {
               logger.info("< Stopping container [" + addon.getId() + "] [" + addon.getRepository().getRootDirectory()
                        + "]");
               long start = System.currentTimeMillis();
               ClassLoaders.executeIn(addon.getClassLoader(), shutdownCallable);
               logger.info("<< Stopped container [" + addon.getId() + "] - "
                        + (System.currentTimeMillis() - start) + "ms");
               return null;
            }
         });
      }
      catch (RuntimeException e)
      {
         logger.log(Level.SEVERE, "Failed to shut down addon " + addon.getId(), e);
         throw e;
      }
   }

   @Override
   public void run()
   {
      Thread currentThread = Thread.currentThread();
      String name = currentThread.getName();
      currentThread.setName(addon.getId().toCoordinates());
      try
      {
         forge.getLockManager().performLocked(LockMode.READ, new Callable<Void>()
         {
            @Override
            public Void call() throws Exception
            {
               logger.info("> Starting container [" + addon.getId() + "] [" + addon.getRepository().getRootDirectory()
                        + "]");
               long start = System.currentTimeMillis();
               container = new AddonContainerStartup();
               shutdownCallable = ClassLoaders.executeIn(addon.getClassLoader(), container);
               logger.info(">> Started container [" + addon.getId() + "] - "
                        + (System.currentTimeMillis() - start) + "ms");
               return null;
            }
         });

         if (container.postStartupTask != null)
            ClassLoaders.executeIn(addon.getClassLoader(), container.postStartupTask);
      }
      catch (Throwable e)
      {
         logger.log(Level.SEVERE, "Failed to start addon [" + addon.getId() + "] with module [" + addon.getModule()
                  + "]", e);
         throw new RuntimeException(e);
      }
      finally
      {
         ((AddonRegistryImpl) forge.getAddonRegistry()).finishedStarting(addon);
         currentThread.setName(name);
      }
   }

   public AddonImpl getAddon()
   {
      return addon;
   }

   public class AddonContainerStartup implements Callable<Callable<Object>>
   {
      private Callable<Void> postStartupTask;

      @Override
      public Callable<Object> call() throws Exception
      {
         try
         {
            ResourceLoader resourceLoader = new AddonResourceLoader(addon);
            ModularURLScanner scanner = new ModularURLScanner(resourceLoader, "META-INF/beans.xml");
            ModuleScanResult scanResult = scanner.scan();

            Callable<Object> shutdownCallback = null;

            if (scanResult.getDiscoveredResourceUrls().isEmpty())
            {
               /*
                * This is an import-only addon and does not require weld, nor provide remote services.
                */
               addon.setServiceRegistry(new NullServiceRegistry());
               addon.setStatus(AddonStatus.STARTED);

               shutdownCallback = new Callable<Object>()
               {
                  @Override
                  public Object call() throws Exception
                  {
                     addon.setStatus(AddonStatus.LOADED);
                     return null;
                  }
               };
            }
            else
            {
               final Weld weld = new ModularWeld(scanResult);
               WeldContainer container;
               container = weld.initialize();

               final BeanManager manager = container.getBeanManager();
               Assert.notNull(manager, "BeanManager was null");

               AddonRepositoryProducer repositoryProducer = BeanManagerUtils.getContextualInstance(manager,
                        AddonRepositoryProducer.class);
               repositoryProducer.setRepository(addon.getRepository());

               FurnaceProducer forgeProducer = BeanManagerUtils.getContextualInstance(manager, FurnaceProducer.class);
               forgeProducer.setForge(forge);

               AddonProducer addonProducer = BeanManagerUtils.getContextualInstance(manager, AddonProducer.class);
               addonProducer.setAddon(addon);

               AddonRegistryProducer addonRegistryProducer = BeanManagerUtils.getContextualInstance(manager,
                        AddonRegistryProducer.class);
               addonRegistryProducer.setRegistry(forge.getAddonRegistry());

               ContainerServiceExtension extension = BeanManagerUtils.getContextualInstance(manager,
                        ContainerServiceExtension.class);
               ServiceRegistryProducer serviceRegistryProducer = BeanManagerUtils.getContextualInstance(manager,
                        ServiceRegistryProducer.class);
               serviceRegistryProducer.setServiceRegistry(new ServiceRegistryImpl(forge.getLockManager(), addon,
                        manager, extension));

               ServiceRegistry registry = BeanManagerUtils.getContextualInstance(manager, ServiceRegistry.class);
               Assert.notNull(registry, "Service registry was null.");
               addon.setServiceRegistry(registry);

               logger.info("Services loaded from addon [" + addon.getId() + "] -  " + registry.getExportedTypes());

               shutdownCallback = new Callable<Object>()
               {
                  @Override
                  public Object call() throws Exception
                  {
                     try
                     {
                        manager.fireEvent(new PreShutdown());
                     }
                     catch (Exception e)
                     {
                        logger.log(Level.SEVERE, "Failed to execute pre-Shutdown event.", e);
                     }
                     finally
                     {
                        addon.setStatus(AddonStatus.LOADED);
                     }

                     weld.shutdown();
                     return null;
                  }
               };

               postStartupTask = new Callable<Void>()
               {
                  @Override
                  public Void call() throws Exception
                  {
                     for (AddonDependency dependency : addon.getDependencies())
                     {
                        if (dependency.getDependency().getStatus().isLoaded())
                           Addons.waitUntilStarted(dependency.getDependency());
                     }

                     addon.setStatus(AddonStatus.STARTED);

                     manager.fireEvent(new PostStartup());
                     return null;
                  }
               };
            }

            return shutdownCallback;
         }
         catch (Exception e)
         {
            addon.setStatus(AddonStatus.FAILED);
            throw e;
         }
      }
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((addon == null) ? 0 : addon.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AddonRunnable other = (AddonRunnable) obj;
      if (addon == null)
      {
         if (other.addon != null)
            return false;
      }
      else if (!addon.equals(other.addon))
         return false;
      return true;
   }
}
