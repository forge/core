package org.jboss.forge.container;

import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
import org.jboss.forge.container.meta.PluginMetadata;
import org.jboss.forge.container.meta.PluginRegistry;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.BeanManagerUtils;
import org.jboss.forge.container.weld.ModularWeld;
import org.jboss.modules.Module;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public final class PluginRunnable implements Runnable
{
   private Module module;
   private PluginModuleRegistry globalRegistry;

   public PluginRunnable(Module module, PluginModuleRegistry registry)
   {
      this.module = module;
      this.globalRegistry = registry;
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
         Assert.notNull(manager, "BeanManager was null");

         manager.fireEvent(new ContainerStartup());

         PluginRegistry registry = BeanManagerUtils.getContextualInstance(manager, PluginRegistry.class);
         Assert.notNull(registry, "Plugin registry was null.");

         Map<String, List<PluginMetadata>> plugins = registry.getPlugins();
         Assert.notNull(plugins, "PluginMetadata Map was null.");

         globalRegistry.addPlugins(module, plugins);

         Class<?> name = Class.forName("org.example.ExamplePlugin", true, module.getClassLoader());
         System.out.println("Class [" + name + "] was loaded.");

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
