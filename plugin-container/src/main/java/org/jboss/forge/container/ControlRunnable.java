package org.jboss.forge.container;

import java.util.concurrent.Callable;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
import org.jboss.forge.container.modules.ModularWeld;
import org.jboss.forge.container.modules.SilentTCCLSingletonProvider;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.modules.Module;
import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public final class ControlRunnable implements Runnable
{
   private Module module;
   private boolean shutdown;

   public ControlRunnable(Module module)
   {
      this.module = module;
   }

   public void shutdown()
   {
      this.shutdown = true;
   }

   @Override
   public void run()
   {
      ClassLoaders.executeIn(module.getClassLoader(), new Callable<Object>()
      {

         @Override
         public Object call() throws Exception
         {
            // Make sure Weld uses ThreadSafe singletons.
            SingletonProvider.initialize(new SilentTCCLSingletonProvider());

            Weld weld = new ModularWeld(module);
            WeldContainer container = weld.initialize();

            BeanManager manager = container.getBeanManager();
            manager.fireEvent(new ContainerStartup());

            while (!shutdown)
            {
               Thread.sleep(10);
            }

            System.out.println("Control thread service registry " + AddonRegistry.registry.getServices());

            manager.fireEvent(new ContainerShutdown());
            weld.shutdown();

            return null;
         }
      });
   }
}
