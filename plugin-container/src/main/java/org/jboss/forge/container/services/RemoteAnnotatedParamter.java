package org.jboss.forge.container.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

public class RemoteAnnotatedParamter<R> implements AnnotatedParameter<R>
{
   private AnnotatedParameter<?> wrapped;

   public RemoteAnnotatedParamter(AnnotatedParameter<?> wrapped)
   {
      this.wrapped = wrapped;
   }

   @Override
   public Type getBaseType()
   {
      return Object.class;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Set<Type> getTypeClosure()
   {
      return new HashSet<Type>(Arrays.asList(Object.class));
   }

   @Override
   public <T extends Annotation> T getAnnotation(Class<T> annotationType)
   {
      return wrapped.getAnnotation(annotationType);
   }

   @Override
   public Set<Annotation> getAnnotations()
   {
      return wrapped.getAnnotations();
   }

   @Override
   public boolean isAnnotationPresent(Class<? extends Annotation> annotationType)
   {
      return wrapped.isAnnotationPresent(annotationType);
   }

   @Override
   public int getPosition()
   {
      return wrapped.getPosition();
   }

   @Override
   @SuppressWarnings("unchecked")
   public AnnotatedCallable<R> getDeclaringCallable()
   {
      return (AnnotatedCallable<R>) wrapped.getDeclaringCallable();
   }

}
