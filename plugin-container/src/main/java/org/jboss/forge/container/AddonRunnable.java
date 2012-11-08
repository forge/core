package org.jboss.forge.container;

import java.util.concurrent.Callable;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.impl.AddonRegistryImpl;
import org.jboss.forge.container.modules.ModularWeld;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.BeanManagerUtils;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.modules.Module;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public final class AddonRunnable implements Runnable
{
   private AddonImpl addon;
   private AddonRegistryImpl addonRegistry;
   private boolean shutdown = false;

   public AddonRunnable(AddonImpl addon, AddonRegistryImpl registry)
   {
      this.addon = addon;
      this.addonRegistry = registry;
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
               Weld weld = new ModularWeld(module);
               WeldContainer container = weld.initialize();

               BeanManager manager = container.getBeanManager();
               Assert.notNull(manager, "BeanManager was null");

               addonRegistry.register(addon);
               addon.setStatus(Status.STARTING);

               manager.fireEvent(new ContainerStartup());

               ContainerControl control = BeanManagerUtils.getContextualInstance(manager, ContainerControl.class);
               Assert.notNull(control, "Container control was null.");

               if (!Status.STARTED.equals(control.getStatus()))
               {
                  throw new ContainerException("Container failed to start.");
               }

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

               addonRegistry.remove(addon);
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
