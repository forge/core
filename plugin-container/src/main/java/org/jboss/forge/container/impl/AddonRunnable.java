package org.jboss.forge.container.impl;

import java.util.concurrent.Callable;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.Status;
import org.jboss.forge.container.impl.event.ContainerShutdown;
import org.jboss.forge.container.impl.event.ContainerStartup;
import org.jboss.forge.container.impl.modules.ModularWeld;
import org.jboss.forge.container.impl.modules.SilentTCCLSingletonProvider;
import org.jboss.forge.container.impl.util.Assert;
import org.jboss.forge.container.impl.util.BeanManagerUtils;
import org.jboss.forge.container.impl.util.ClassLoaders;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.modules.Module;
import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public final class AddonRunnable implements Runnable
{
   private AddonImpl addon;
   private AddonRegistry globalRegistry;
   private boolean shutdown = false;

   public AddonRunnable(AddonImpl addon, AddonRegistry registry)
   {
      this.addon = addon;
      this.globalRegistry = registry;
   }

   public void shutdown()
   {
      this.shutdown = true;
   }

   @Override
   public void run()
   {
      final Module module = addon.getModule();

      ClassLoaders.executeIn(module.getClassLoader(), new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            try
            {
               SingletonProvider.reset();
               SingletonProvider.initialize(new SilentTCCLSingletonProvider());

               Weld weld = new ModularWeld(module);
               WeldContainer container = weld.initialize();

               BeanManager manager = container.getBeanManager();
               Assert.notNull(manager, "BeanManager was null");

               globalRegistry.register(addon);
               addon.setStatus(Status.STARTING);

               manager.fireEvent(new ContainerStartup());

               ServiceRegistry registry = BeanManagerUtils.getContextualInstance(manager, ServiceRegistry.class);
               Assert.notNull(registry, "Service registry was null.");
               addon.setServiceRegistry(registry);

               addon.setStatus(Status.STARTED);

               System.out.println("Services loaded from addon module [" + module.getIdentifier() + "] - "
                        + registry.getServices());

               while (!shutdown)
               {
                  Thread.sleep(10);
               }

               addon.setStatus(Status.STOPPING);

               globalRegistry.remove(addon);
               manager.fireEvent(new ContainerShutdown());
               weld.shutdown();

               addon.setStatus(Status.STOPPED);
               return null;
            }
            catch (Exception e)
            {
               addon.setStatus(Status.FAILED);
               throw e;
            }
         }
      });
   }

   public AddonImpl getAddon()
   {
      return addon;
   }
}
