package org.jboss.forge.container;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.event.Perform;
import org.jboss.forge.container.events.InitializeServices;
import org.jboss.forge.container.impl.AddonRepositoryProducer;
import org.jboss.forge.container.impl.AddonImpl;
import org.jboss.forge.container.impl.NullServiceRegistry;
import org.jboss.forge.container.modules.ModularURLScanner;
import org.jboss.forge.container.modules.ModularWeld;
import org.jboss.forge.container.modules.ModuleResourceLoader;
import org.jboss.forge.container.modules.ModuleScanResult;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.BeanManagerUtils;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;

public final class AddonRunnable implements Runnable
{
   private Forge forge;
   private AddonImpl addon;
   private boolean shutdown = false;
   private static final Logger LOGGER = Logger.getLogger(AddonRunnable.class.getName());

   private CDIContainer container;

   public AddonRunnable(Forge forge, AddonImpl addon)
   {
      this.forge = forge;
      this.addon = addon;
   }

   public void shutdown()
   {
      LOGGER.info("Stopping container [" + Thread.currentThread().getName() + "]");
      shutdown = true;
   }

   @Override
   public void run()
   {
      LOGGER.info("Starting container [" + Thread.currentThread().getName() + "]");
      container = new CDIContainer();
      ClassLoaders.executeIn(addon.getClassLoader(), container);
   }

   public AddonImpl getAddon()
   {
      return addon;
   }

   public class CDIContainer implements Callable<Object>
   {

      @Override
      public Object call() throws Exception
      {
         try
         {
            addon.setStatus(Status.STARTING);

            ResourceLoader loader = new ModuleResourceLoader(addon.getModule());
            ModularURLScanner scanner = new ModularURLScanner(loader, "META-INF/beans.xml");
            ModuleScanResult scanResult = scanner.scan();

            if (scanResult.getDiscoveredResourceUrls().isEmpty())
            {
               /*
                * This is a classloading only addon and does not require weld, nor provide remote services.
                */
               addon.setServiceRegistry(new NullServiceRegistry());
               addon.setStatus(Status.STARTED);

               while (!shutdown)
               {
                  Thread.sleep(10);
               }

               addon.setStatus(Status.STOPPING);
               addon.setStatus(Status.STOPPED);
            }
            else
            {
               Weld weld = new ModularWeld(addon.getModule(), scanResult);
               WeldContainer container = weld.initialize();

               BeanManager manager = container.getBeanManager();
               Assert.notNull(manager, "BeanManager was null");

               ContainerControl control = BeanManagerUtils.getContextualInstance(manager, ContainerControl.class);
               AddonRepositoryProducer repositoryProducer = BeanManagerUtils.getContextualInstance(manager,
                        AddonRepositoryProducer.class);
               repositoryProducer.setAddonDir(forge.getAddonDir());
               Assert.notNull(control, "Container control was null.");

               ServiceRegistry registry = BeanManagerUtils.getContextualInstance(manager, ServiceRegistry.class);
               Assert.notNull(registry, "Service registry was null.");
               addon.setServiceRegistry(registry);

               manager.fireEvent(new InitializeServices());

               LOGGER.info("Services loaded from addon module [" + Thread.currentThread().getName() + "] -  "
                        + registry.getServices());

               control.start();

               addon.setStatus(Status.STARTED);

               manager.fireEvent(new Perform());

               while (!shutdown && !Status.STOPPED.equals(control.getStatus()))
               {
                  Thread.sleep(10);
               }

               addon.setStatus(Status.STOPPING);
               control.stop();
               weld.shutdown();
               addon.setStatus(Status.STOPPED);
            }
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
