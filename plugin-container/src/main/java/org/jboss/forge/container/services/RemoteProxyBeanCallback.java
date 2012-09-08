package org.jboss.forge.container.services;

import net.sf.cglib.proxy.LazyLoader;

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
      for (ClassLoader classLoader : registry.getServices().keySet())
      {
         if (registry.getServices().get(classLoader).hasService(serviceType))
         {
            RemoteInstance<?> instance = registry.getServices().get(classLoader).getRemoteInstance(serviceType);
            result = instance.get();
            break;
         }
      }

      if (result == null)
         throw new IllegalStateException("Service [" + serviceType.getName() + "] is not registered.");

      return result;

   }
}
