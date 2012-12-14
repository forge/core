package org.jboss.forge.container;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.events.InitializeServices;
import org.jboss.forge.container.impl.AddonRepositoryProducer;
import org.jboss.forge.container.impl.RegisteredAddonImpl;
import org.jboss.forge.container.modules.ModularWeld;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.BeanManagerUtils;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public final class AddonRunnable implements Runnable
{
   private Forge forge;
   private RegisteredAddonImpl addon;
   private boolean shutdown = false;
   private static final Logger LOGGER = Logger.getLogger(AddonRunnable.class.getName());

   private CDIContainer container;

   public AddonRunnable(Forge forge, RegisteredAddonImpl addon)
   {
      this.forge = forge;
      this.addon = addon;
   }

   public void shutdown()
   {
      LOGGER.info("Stopping container [" + Thread.currentThread().getName() + "]");
      ClassLoaders.executeIn(addon.getClassLoader(), new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            shutdown = true;
            return null;
         }
      });
   }

   @Override
   public void run()
   {
      LOGGER.info("Starting container [" + Thread.currentThread().getName() + "]");
      container = new CDIContainer();
      ClassLoaders.executeIn(addon.getClassLoader(), container);
   }

   public RegisteredAddonImpl getAddon()
   {
      return addon;
   }

   public class CDIContainer implements Callable<Object>
   {
      private Weld weld;
      private BeanManager manager;
      private ContainerControl control;

      @Override
      public Object call() throws Exception
      {
         try
         {
            addon.setStatus(Status.STARTING);

            weld = new ModularWeld(addon.getModule());
            WeldContainer container = weld.initialize();

            manager = container.getBeanManager();
            Assert.notNull(manager, "BeanManager was null");

            control = BeanManagerUtils.getContextualInstance(manager, ContainerControl.class);
            AddonRepositoryProducer repositoryProducer = BeanManagerUtils.getContextualInstance(manager,
                     AddonRepositoryProducer.class);
            repositoryProducer.setAddonDir(forge.getAddonDir());
            Assert.notNull(control, "Container control was null.");

            ServiceRegistry registry = BeanManagerUtils.getContextualInstance(manager, ServiceRegistry.class);
            Assert.notNull(registry, "Service registry was null.");
            addon.setServiceRegistry(registry);

            manager.fireEvent(new InitializeServices());

            LOGGER.info("Services loaded from addon module [" + addon.getModule().getIdentifier() + "] -  "
                     + registry.getServices());

            control.start();

            addon.setStatus(Status.STARTED);

            while (!shutdown)
            {
               Thread.sleep(10);
            }

            addon.setStatus(Status.STOPPING);
            control.stop();
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
   }
}
