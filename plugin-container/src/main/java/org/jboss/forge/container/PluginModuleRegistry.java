package org.jboss.forge.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.forge.container.meta.PluginMetadata;
import org.jboss.modules.Module;

public class PluginModuleRegistry
{
   private Map<Module, Map<String, List<PluginMetadata>>> plugins = new ConcurrentHashMap<Module, Map<String, List<PluginMetadata>>>();

   public void addPlugins(Module module, Map<String, List<PluginMetadata>> plugins)
   {
      this.plugins.put(module, plugins);
   }
}
