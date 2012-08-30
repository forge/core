package org.jboss.forge.container;

import java.util.Set;
import java.util.concurrent.Callable;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.forge.container.weld.ModularWeld;
import org.jboss.modules.Module;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public final class ControlRunnable implements Runnable
{
   private Module module;
   private AddonModuleRegistry globalRegistry;
   private Set<Module> addons;

   public ControlRunnable(Module module, AddonModuleRegistry registry, Set<Module> addons)
   {
      this.module = module;
      this.globalRegistry = registry;
      this.addons = addons;
   }

   @Override
   public void run()
   {
      ClassLoaders.executeIn(module.getClassLoader(), new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            Weld weld = new ModularWeld(module);
            WeldContainer container = weld.initialize();

            BeanManager manager = container.getBeanManager();
            manager.fireEvent(new ContainerStartup());

            while (globalRegistry.getPlugins().keySet().size() < addons.size())
            {
               Thread.sleep(10);
            }

            System.out.println("Control thread plugin registry " + globalRegistry.getPlugins());

            manager.fireEvent(new ContainerShutdown());
            weld.shutdown();
            return null;
         }
      });
   }
}
