package org.jboss.forge.furnace.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.furnace.impl.Service;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class LocalServiceInjectionPoint implements InjectionPoint
{
   private InjectionPoint wrapped;
   private Set<Annotation> qualifiers;
   private Class<?> serviceType;

   public LocalServiceInjectionPoint(InjectionPoint wrapped, Class<?> serviceType)
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