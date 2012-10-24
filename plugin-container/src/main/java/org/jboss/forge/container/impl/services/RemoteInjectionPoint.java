package org.jboss.forge.container.impl.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

public class RemoteInjectionPoint implements InjectionPoint
{

   private InjectionPoint wrapped;
   private Annotated annotated;

   public RemoteInjectionPoint(InjectionPoint wrapped)
   {
      this.wrapped = wrapped;

      Annotated annotated = wrapped.getAnnotated();
      if (annotated instanceof AnnotatedField<?>)
         this.annotated = new RemoteAnnotatedField<Object>((AnnotatedField<?>) annotated);
      else if (annotated instanceof AnnotatedParameter<?>)
         this.annotated = new RemoteAnnotatedParamter<Object>((AnnotatedParameter<?>) annotated);
      else
         throw new IllegalArgumentException("Unsupported Service injection point type ["
                  + wrapped.getMember().getName() + "]");
   }

   @Override
   public Type getType()
   {
      return Object.class;
   }

   @Override
   public Set<Annotation> getQualifiers()
   {
      return wrapped.getQualifiers();
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
      return annotated;
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
