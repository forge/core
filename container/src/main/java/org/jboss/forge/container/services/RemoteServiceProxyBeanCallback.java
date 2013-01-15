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

public class RemoteServiceProxyBeanCallback implements LazyLoader
{

   private final Class<?> serviceType;
   private final AddonRegistry registry;
   private final InjectionPoint injectionPoint;

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
               // FIXME remove the need for this implementation coupling
               result = ((RemoteInstanceImpl<?>) instance).get(new NativeServiceInjectionPoint(injectionPoint,
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
