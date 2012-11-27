package org.jboss.forge.container.impl;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.event.Startup;
import org.jboss.forge.container.services.ServiceRegistry;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ServiceRegistryInitializer
{
   public static void registerServices(@Observes Startup event, BeanManager manager,
            ContainerServiceExtension extension, ServiceRegistry registry, AddonRegistry global)
   {
      for (Class<?> serviceType : extension.getServices())
      {
         registry.addService(serviceType);
      }
   }
}
