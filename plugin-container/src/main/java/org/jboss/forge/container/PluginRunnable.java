package org.jboss.forge.container;

import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
import org.jboss.forge.container.meta.PluginMetadata;
import org.jboss.forge.container.meta.PluginRegistry;
import org.jboss.forge.container.util.BeanManagerUtils;
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
      Thread.currentThread().setContextClassLoader(module.getClassLoader());
      Weld weld = new ModularWeld();
      BeanManager manager = null;

      WeldContainer container = weld.initialize();
      manager = container.getBeanManager();

      manager.fireEvent(new ContainerStartup());
      
      // TODO get list of plugins and register metadata with executor thread
      PluginRegistry registry = BeanManagerUtils.getContextualInstance(manager, PluginRegistry.class);
      Map<String, List<PluginMetadata>> plugins = registry.getPlugins();
      globalRegistry.addPlugins(module, plugins);
      
      manager.fireEvent(new ContainerShutdown());
      weld.shutdown();
   }
}
