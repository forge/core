package org.jboss.forge.container.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

public class RemoteServiceInjectionPoint implements InjectionPoint
{
   private final InjectionPoint wrapped;
   private final Set<Annotation> qualifiers = new HashSet<Annotation>();

   public RemoteServiceInjectionPoint(final InjectionPoint wrapped, Annotation... qualifiers)
   {
      this.wrapped = wrapped;

      if (qualifiers != null)
         this.qualifiers.addAll(Arrays.asList(qualifiers));
   }

   @Override
   public Type getType()
   {
      return wrapped.getType();
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

   @Override
   public Set<Annotation> getQualifiers()
   {
      return Collections.unmodifiableSet(qualifiers);
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
   public String toString()
   {
      return wrapped.getAnnotated().getBaseType().toString();
   }
}
