package org.jboss.forge.container.services;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.util.AddonFilters;
import org.jboss.forge.container.util.Addons;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.proxy.ForgeProxy;
import org.jboss.forge.proxy.Proxies;

public class ExportedInstanceLazyLoader implements ForgeProxy
{
   private final Class<?> serviceType;
   private final AddonRegistry registry;
   private final InjectionPoint injectionPoint;
   private Object delegate;

   public ExportedInstanceLazyLoader(AddonRegistry registry, Class<?> serviceType, InjectionPoint injectionPoint)
   {
      this.registry = registry;
      this.serviceType = serviceType;
      this.injectionPoint = injectionPoint;
   }

   public static Object create(AddonRegistry registry, InjectionPoint injectionPoint, Class<?> serviceType)
   {
      ExportedInstanceLazyLoader callback = new ExportedInstanceLazyLoader(registry, serviceType,
               injectionPoint);
      return Proxies.enhance(serviceType, callback);
   }

   @Override
   public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable
   {
      try
      {
         if (thisMethod.getDeclaringClass().getName().equals(ForgeProxy.class.getName()))
         {
            return delegate;
         }
      }
      catch (Exception e)
      {
      }

      if (delegate == null)
         delegate = loadObject();

      return thisMethod.invoke(delegate, args);
   }

   private Object loadObject() throws Exception
   {
      Object result = null;
      for (Addon addon : registry.getRegisteredAddons(AddonFilters.allStarted()))
      {
         Addons.waitUntilStarted(addon);
         ServiceRegistry serviceRegistry = addon.getServiceRegistry();
         if (serviceRegistry.hasService(serviceType))
         {
            ExportedInstance<?> instance = serviceRegistry.getExportedInstance(serviceType);
            Assert.notNull(instance, "Exported Instance of [" + serviceType.getName()
                     + "] not found in originating ServiceRegistry [" + addon.getId() + "].");
            if (instance instanceof ExportedInstanceImpl)
               // FIXME remove the need for this implementation coupling
               result = ((ExportedInstanceImpl<?>) instance).get(new LocalServiceInjectionPoint(injectionPoint,
                        serviceType));
            else
               result = instance.get();
            break;
         }
      }

      if (result == null)
      {
         throw new IllegalStateException("Remote service [" + serviceType.getName() + "] is not registered.");
      }

      return result;
   }

   @Override
   public Object getDelegate()
   {
      return delegate;
   }

}
