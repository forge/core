package org.jboss.forge.container.services;

import net.sf.cglib.proxy.LazyLoader;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;

public class RemoteServiceCallback implements LazyLoader
{
   private Class<?> serviceType;
   private AddonRegistry registry;

   public RemoteServiceCallback(AddonRegistry registry, Class<?> serviceType)
   {
      this.registry = registry;
      this.serviceType = serviceType;
   }

   @Override
   public Object loadObject() throws Exception
   {
      Object result = null;
      for (Module module : registry.getServices().keySet())
      {
         ModuleClassLoader classLoader = module.getClassLoader();
         if (classLoader.equals(serviceType.getClassLoader()))
         {
            RemoteInstance<?> instance = registry.getServices().get(module).getRemoteInstance(serviceType);
            result = instance.get();
         }
      }

      if (result == null)
         throw new IllegalStateException("Service [" + serviceType.getName() + "] is not registered.");

      return result;

   }
}
