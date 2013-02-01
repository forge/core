package org.jboss.forge.container.services;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.util.Addons;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.proxy.ForgeProxy;
import org.jboss.forge.proxy.Proxies;

public class ExportedInstanceLazyLoader implements MethodHandler
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
      for (Addon addon : registry.getServiceRegistries().keySet())
      {
         if (serviceType.getClassLoader().equals(addon.getClassLoader()))
         {
            Addons.waitUntilStarted(addon);
            ServiceRegistry serviceRegistry = addon.getServiceRegistry();
            ExportedInstance<?> instance = serviceRegistry.getExportedInstance(serviceType);
            Assert.notNull(instance, "Exported Instance not found in originating ServiceRegistry.");
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

}
