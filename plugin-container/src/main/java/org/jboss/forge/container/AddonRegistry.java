package org.jboss.forge.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.modules.Module;

@Typed()
public class AddonRegistry
{
   private Map<Module, ServiceRegistry> services = new ConcurrentHashMap<Module, ServiceRegistry>();

   /**
    * Global Addon registry.
    */
   static AddonRegistry registry = new AddonRegistry();

   @Produces
   @Typed(AddonRegistry.class)
   @Singleton
   public static AddonRegistry produceGlobalAddonRegistry()
   {
      return AddonRegistry.registry;
   }

   public void addServices(Module module, ServiceRegistry registry)
   {
      System.out.println("Added services " + registry + " from module [" + module.getIdentifier() + "]");
      if (!services.containsKey(module))
         services.put(module, registry);

      else
         throw new IllegalStateException("ServiceRegistry already regisered for module [" + module.getIdentifier()
                  + "]");
   }

   public Map<Module, ServiceRegistry> getServices()
   {
      return services;
   }
}
