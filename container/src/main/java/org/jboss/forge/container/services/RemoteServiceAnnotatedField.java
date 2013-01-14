package org.jboss.forge.container.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;

public class RemoteServiceAnnotatedField<R> implements AnnotatedField<R>
{
   private AnnotatedField<?> wrapped;

   public RemoteServiceAnnotatedField(AnnotatedField<?> wrapped)
   {
      this.wrapped = wrapped;
   }

   @Override
   public Type getBaseType()
   {
      return Object.class;
   }

   @Override
   public Set<Type> getTypeClosure()
   {
      return Collections.<Type> singleton(Object.class);
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
   public boolean isStatic()
   {
      return wrapped.isStatic();
   }

   @Override
   @SuppressWarnings("unchecked")
   public AnnotatedType<R> getDeclaringType()
   {
      return (AnnotatedType<R>) wrapped.getDeclaringType();
   }

   @Override
   public Field getJavaMember()
   {
      return wrapped.getJavaMember();
   }

}