package org.jboss.forge.container.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;

public class RemoteAnnotatedField<R> implements AnnotatedField<R>
{
   private AnnotatedField<?> wrapped;

   public RemoteAnnotatedField(AnnotatedField<?> wrapped)
   {
      this.wrapped = wrapped;
   }

   @Override
   public Type getBaseType()
   {
      return Remote.class;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Set<Type> getTypeClosure()
   {
      return new HashSet<Type>(Arrays.asList(Remote.class));
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