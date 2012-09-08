package org.jboss.forge.container;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.container.event.Startup;
import org.jboss.forge.container.services.ContainerServiceExtension;
import org.jboss.forge.container.services.RemoteInstanceImpl;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.modules.Module;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ServiceRegistryManager
{
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void registerServices(@Observes Startup event, BeanManager manager,
            ContainerServiceExtension extension, ServiceRegistry registry, AddonRegistry global)
   {
      for (Class<?> serviceType : extension.services)
      {
         registry.addService(serviceType, new RemoteInstanceImpl(manager, serviceType));
      }

      global.addServices(Module.forClassLoader(Thread.currentThread().getContextClassLoader(), false), registry);
   }
}
