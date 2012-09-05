package org.jboss.forge.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.forge.container.services.ServiceType;
import org.jboss.modules.Module;

public class AddonRegistry
{
   private Map<Module, List<ServiceType>> services = new ConcurrentHashMap<Module, List<ServiceType>>();

   public void addServices(Module module, List<ServiceType> services)
   {
      System.out.println("Added services " + services + " from module [" + module.getIdentifier() + "]");
      this.services.put(module, services);
   }

   public Map<Module, List<ServiceType>> getServices()
   {
      return services;
   }
}
