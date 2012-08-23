package org.jboss.forge.container;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
import org.jboss.forge.container.weld.ModularWeld;
import org.jboss.modules.Module;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public final class ControlRunnable implements Runnable
{
   private Module module;
   private PluginModuleRegistry globalRegistry;
   private volatile boolean terminated;

   public ControlRunnable(Module module, PluginModuleRegistry registry)
   {
      this.module = module;
      this.globalRegistry = registry;
   }
   
   public void terminate()
   {
      terminated = true;
   }

   @Override
   public void run()
   {
      try
      {
         Thread.currentThread().setContextClassLoader(module.getClassLoader());
         Weld weld = new ModularWeld(module);

         WeldContainer container = weld.initialize();
         BeanManager manager = container.getBeanManager();
         manager.fireEvent(new ContainerStartup());

         while(!terminated)
         {
            Thread.sleep(10);
         }

         manager.fireEvent(new ContainerShutdown());
         weld.shutdown();
      }
      catch (Exception e)
      {
         System.out.println("Caught exception in thread: " + Thread.currentThread().getName());
         e.printStackTrace();
      }
   }
}
