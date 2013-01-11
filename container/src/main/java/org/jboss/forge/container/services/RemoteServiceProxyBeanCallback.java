package org.jboss.forge.container.services;

import javax.enterprise.inject.spi.InjectionPoint;

import net.sf.cglib.proxy.LazyLoader;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.impl.RemoteInstanceImpl;

public class RemoteServiceProxyBeanCallback implements LazyLoader
{

   private Class<?> serviceType;
   private AddonRegistry registry;
   private InjectionPoint injectionPoint;

   public RemoteServiceProxyBeanCallback(AddonRegistry registry, Class<?> serviceType, InjectionPoint injectionPoint)
   {
      this.registry = registry;
      this.serviceType = serviceType;
      this.injectionPoint = injectionPoint;
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
            if (instance instanceof RemoteInstanceImpl)
            {
               result = ((RemoteInstanceImpl<?>) instance).get(injectionPoint);
            }
            result = instance.get();
            break;
         }
      }

      if (result == null)
         throw new IllegalStateException("Remote service [" + serviceType.getName() + "] is not registered.");

      return result;
   }
}
