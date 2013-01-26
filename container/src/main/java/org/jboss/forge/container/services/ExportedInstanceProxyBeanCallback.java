package org.jboss.forge.container.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import net.sf.cglib.proxy.LazyLoader;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.impl.Service;
import org.jboss.forge.container.util.Addons;
import org.jboss.forge.container.util.Assert;

public class ExportedInstanceProxyBeanCallback implements LazyLoader
{

   private final Class<?> serviceType;
   private final AddonRegistry registry;
   private final InjectionPoint injectionPoint;

   public ExportedInstanceProxyBeanCallback(AddonRegistry registry, Class<?> serviceType, InjectionPoint injectionPoint)
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
         if (serviceType.getClassLoader().equals(addon.getClassLoader()))
         {
            Addons.waitUntilStarted(addon);
            ServiceRegistry serviceRegistry = addon.getServiceRegistry();
            ExportedInstance<?> instance = serviceRegistry.getExportedInstance(serviceType);
            Assert.notNull(instance, "Exported Instance not found in originating ServiceRegistry.");
            if (instance instanceof ExportedInstanceImpl)
               // FIXME remove the need for this implementation coupling
               result = ((ExportedInstanceImpl<?>) instance).get(new NativeServiceInjectionPoint(injectionPoint,
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

   /**
    * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
    * 
    */
   public class NativeServiceInjectionPoint implements InjectionPoint
   {

      private InjectionPoint wrapped;
      private Set<Annotation> qualifiers;
      private Class<?> serviceType;

      public NativeServiceInjectionPoint(InjectionPoint wrapped, Class<?> serviceType)
      {
         this.wrapped = wrapped;
         this.qualifiers = new HashSet<Annotation>(wrapped.getQualifiers());

         for (Annotation a : qualifiers)
         {
            if (a instanceof Service)
            {
               qualifiers.remove(a);
               break;
            }
         }

         this.serviceType = serviceType;
      }

      @Override
      public Type getType()
      {
         return serviceType;
      }

      @Override
      public Set<Annotation> getQualifiers()
      {
         return qualifiers;
      }

      @Override
      public Bean<?> getBean()
      {
         return wrapped.getBean();
      }

      @Override
      public Member getMember()
      {
         return wrapped.getMember();
      }

      @Override
      public Annotated getAnnotated()
      {
         return wrapped.getAnnotated();
      }

      @Override
      public boolean isDelegate()
      {
         return wrapped.isDelegate();
      }

      @Override
      public boolean isTransient()
      {
         return wrapped.isTransient();
      }
   }
}
