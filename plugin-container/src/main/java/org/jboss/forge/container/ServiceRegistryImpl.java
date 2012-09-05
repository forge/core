package org.jboss.forge.container;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.container.services.ServiceType;
import org.jboss.forge.container.services.ServiceRegistry;

public class ServiceRegistryImpl implements ServiceRegistry
{
   private List<ServiceType> services = new ArrayList<ServiceType>();

   @Override
   public void addService(ServiceType service)
   {
      services.add(service);
   }

   @Override
   public ServiceType getService(Class<?> clazz)
   {
      // TODO implement
      return null;
   }

   @Override
   public List<ServiceType> getServices()
   {
      return services;
   }

}
