package org.jboss.forge.container;

import java.util.Set;

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
   private Set<Module> addons;

   public AddonRunnable(Module module, AddonRegistry registry, Set<Module> addons)
   {
      this.module = module;
      this.globalRegistry = registry;
      this.addons = addons;
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

            while (globalRegistry.getServices().size() < addons.size())
            {
               Thread.sleep(10);
            }

            manager.fireEvent(new ContainerShutdown());
            weld.shutdown();
         }
      });
   }
}
