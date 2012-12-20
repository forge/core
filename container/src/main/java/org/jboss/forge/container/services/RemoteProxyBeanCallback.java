package org.jboss.forge.container.services;

import net.sf.cglib.proxy.LazyLoader;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonRegistry;

public class RemoteProxyBeanCallback implements LazyLoader
{
   private Class<?> serviceType;
   private AddonRegistry registry;

   public RemoteProxyBeanCallback(AddonRegistry registry, Class<?> serviceType)
   {
      this.registry = registry;
      this.serviceType = serviceType;
   }

   @Override
   public Object loadObject() throws Exception
   {
      Object result = null;
      for (Addon addon : registry.getServiceRegistries().keySet())
      {
         ServiceRegistry serviceRegistry = addon.getServiceRegistry();
         if (serviceRegistry != null && serviceRegistry.hasService(serviceType))
         {
            RemoteInstance<?> instance = serviceRegistry.getRemoteInstance(serviceType);
            result = instance.get();
            break;
         }
      }

      if (result == null)
         throw new IllegalStateException("Service [" + serviceType.getName() + "] is not registered.");

      return result;

   }
}
