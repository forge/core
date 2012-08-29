package org.jboss.forge.container;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
import org.jboss.forge.container.meta.PluginMetadata;
import org.jboss.forge.container.meta.PluginRegistry;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.BeanManagerUtils;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.forge.container.util.ClassLoaders.Task;
import org.jboss.forge.container.weld.ModularWeld;
import org.jboss.modules.Module;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public final class AddonRunnable implements Runnable
{
   private Module module;
   private AddonModuleRegistry globalRegistry;
   private Set<Module> addons;

   public AddonRunnable(Module module, AddonModuleRegistry registry, Set<Module> addons)
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

            PluginRegistry registry = BeanManagerUtils.getContextualInstance(manager, PluginRegistry.class);
            Assert.notNull(registry, "Plugin registry was null.");

            Map<String, List<PluginMetadata>> plugins = registry.getPlugins();
            Assert.notNull(plugins, "PluginMetadata Map was null.");

            globalRegistry.addPlugins(module, plugins);

            while (globalRegistry.getPlugins().keySet().size() < addons.size())
            {
               Thread.sleep(100);
            }

            manager.fireEvent(new ContainerShutdown());
            weld.shutdown();
         }
      });
   }
}
