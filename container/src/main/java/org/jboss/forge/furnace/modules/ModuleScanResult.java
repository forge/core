package org.jboss.forge.furnace.modules;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.jboss.weld.resources.spi.ResourceLoader;

public class ModuleScanResult
{
   private ResourceLoader loader;
   private List<URL> resourceUrls;
   private Collection<String> classes;

   public ModuleScanResult(ResourceLoader loader, List<URL> discoveredResourceUrls, Collection<String> discoveredClasses)
   {
      this.loader = loader;
      this.resourceUrls = discoveredResourceUrls;
      this.classes = discoveredClasses;
   }

   public Collection<String> getDiscoveredClasses()
   {
      return classes;
   }

   public List<URL> getDiscoveredResourceUrls()
   {
      return resourceUrls;
   }

   public ResourceLoader getResourceLoader()
   {
      return loader;
   }
}
