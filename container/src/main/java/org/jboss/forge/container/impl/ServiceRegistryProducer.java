package org.jboss.forge.container.impl;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.container.services.ServiceRegistry;

@Singleton
public class ServiceRegistryProducer
{
   @Inject
   private ContainerServiceExtension extension;

   private ServiceRegistry registry;

   @Produces
   @Singleton
   public ServiceRegistry produceGlobalAddonRepository()
   {
      return registry;
   }

   public void setServiceRegistry(ServiceRegistry registry)
   {
      this.registry = registry;
   }
}
