package org.jboss.forge.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.modules.ModuleClassLoader;

@Typed()
public class AddonRegistry
{
   private Map<ClassLoader, ServiceRegistry> services = new ConcurrentHashMap<ClassLoader, ServiceRegistry>();

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

   public void addServices(ClassLoader loader, ServiceRegistry registry)
   {
      if (loader == null)
      {
         return;
      }

      services.put(loader, registry);
   }

   public Map<ClassLoader, ServiceRegistry> getServices()
   {
      return services;
   }

   public ServiceRegistry removeServices(ModuleClassLoader classLoader)
   {
      return services.remove(classLoader);
   }
}
