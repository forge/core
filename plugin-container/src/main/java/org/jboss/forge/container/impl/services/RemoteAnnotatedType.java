package org.jboss.forge.container.impl.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

public class RemoteAnnotatedType<R> implements AnnotatedType<R>
{
   private AnnotatedType<R> wrapped;

   public RemoteAnnotatedType(AnnotatedType<R> wrapped)
   {
      this.wrapped = wrapped;
   }

   @Override
   public Type getBaseType()
   {
      return wrapped.getBaseType();
   }

   @Override
   public Set<Type> getTypeClosure()
   {
      return wrapped.getTypeClosure();
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
   public Class<R> getJavaClass()
   {
      return (Class<R>) wrapped.getJavaClass();
   }

   @Override
   public Set<AnnotatedConstructor<R>> getConstructors()
   {
      return wrapped.getConstructors();
   }

   @Override
   public Set<AnnotatedMethod<? super R>> getMethods()
   {
      return wrapped.getMethods();
   }

   @Override
   public Set<AnnotatedField<? super R>> getFields()
   {
      return wrapped.getFields();
   }

}
