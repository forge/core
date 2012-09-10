package org.jboss.forge.container;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
import org.jboss.forge.container.modules.ModularWeld;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.BeanManagerUtils;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.forge.container.util.ClassLoaders.Task;
import org.jboss.modules.Module;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public final class AddonRunnable implements Runnable
{
   private Module module;
   private AddonRegistry globalRegistry;
   private boolean shutdown = false;

   public AddonRunnable(Module module, AddonRegistry registry)
   {
      this.module = module;
      this.globalRegistry = registry;
   }

   public void shutdown()
   {
      this.shutdown = true;
   }

   @Override
   public void run()
   {
      ClassLoaders.executeIn(module.getClassLoader(), new Task()
      {
         @Override
         public void perform() throws Exception
         {
            Weld weld = new ModularWeld(module);
            WeldContainer container = weld.initialize();

            BeanManager manager = container.getBeanManager();
            Assert.notNull(manager, "BeanManager was null");

            manager.fireEvent(new ContainerStartup());

            ServiceRegistry registry = BeanManagerUtils.getContextualInstance(manager, ServiceRegistry.class);
            Assert.notNull(registry, "Service registry was null.");

            globalRegistry.addServices(module.getClassLoader(), registry);

            System.out.println("Services loaded from addon module [" + module.getIdentifier() + "] - "
                     + registry.getServices());

            while (!shutdown)
            {
               Thread.sleep(10);
            }

            globalRegistry.removeServices(module.getClassLoader());
            manager.fireEvent(new ContainerShutdown());
            weld.shutdown();
         }
      });
   }
}
