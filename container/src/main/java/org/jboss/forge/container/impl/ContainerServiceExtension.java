/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessProducer;

import org.jboss.forge.container.events.CrossContainerObserverMethod;
import org.jboss.forge.container.services.Remote;
import org.jboss.forge.container.services.RemoteServiceInjectionPoint;
import org.jboss.forge.container.services.RemoteServiceProxyBeanProducer;
import org.jboss.forge.container.util.Annotations;
import org.jboss.forge.container.util.Types;

/**
 * One classloader/thread/weld container per plugin module. One primary executor container running, fires events to each
 * plugin-container.
 * 
 * Addons may depend on other addons beans, but these beans must be explicitly exposed via the {@link Remote} and
 * {@link Service} API.
 */
public class ContainerServiceExtension implements Extension
{
   private Logger logger = Logger.getLogger(getClass().getName());
   private Set<Class<?>> services = new HashSet<Class<?>>();

   public void wireCrossContainerEvents(@Observes AfterBeanDiscovery event)
   {
      event.addObserverMethod(new CrossContainerObserverMethod());
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void processRemotes(@Observes ProcessAnnotatedType<?> event) throws InstantiationException,
            IllegalAccessException
   {
      Class<?> type = event.getAnnotatedType().getJavaClass();
      if (Annotations.isAnnotationPresent(type, Remote.class))
      {
         event.setAnnotatedType(new RemoteAnnotatedType(event.getAnnotatedType()));
         if (type.getClassLoader().equals(Thread.currentThread().getContextClassLoader()))
         {
            services.add(event.getAnnotatedType().getJavaClass());
         }
      }
   }

   public void processRemoteInjectionPoint(@Observes ProcessInjectionPoint<?, ?> event, BeanManager manager)
   {
      Annotated annotated = event.getInjectionPoint().getAnnotated();

      Remote remote = getRemote(annotated);
      Class<?> injectionPointDeclaringType = Types.toClass(event.getInjectionPoint().getMember().getDeclaringClass());
      Class<?> injectionBeanValueType = Types.toClass(annotated.getBaseType());

      boolean local = isClassLocal(injectionPointDeclaringType, injectionBeanValueType);
      if (!local)
      {
         if (remote == null)
         {
            // event.addDefinitionError(new ContainerException(
            // "ERROR: Illegal attempt to inject [" + injectionBeanValueType.getName()
            // + "], which does not originate from this Addon but is not a @"
            // + Remote.class.getSimpleName() + " bean, at injection point " + annotated + "."));
         }
         else
         {
            event.setInjectionPoint(new RemoteServiceInjectionPoint(event.getInjectionPoint(), new Service()
            {
               @Override
               public Class<? extends Annotation> annotationType()
               {
                  return Service.class;
               }
            }));
         }
      }
      else if (remote != null)
      {
         logger.fine("Not @Remote type " + annotated);
      }
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void processProducerHooks(@Observes ProcessProducer<?, ?> event, BeanManager manager)
   {
      Class<?> type = Types.toClass(event.getAnnotatedMember().getJavaMember());
      ClassLoader classLoader = type.getClassLoader();
      if (type != null && classLoader != null && classLoader.equals(Thread.currentThread().getContextClassLoader()))
      {
         if (Annotations.isAnnotationPresent(type, Remote.class))
         {
            event.setProducer(new RemoteServiceProxyBeanProducer(manager, event.getProducer(), type));
            services.add(type);
         }
      }
   }

   public Set<Class<?>> getServices()
   {
      return services;
   }

   private boolean isClassLocal(Class<?> reference, Class<?> type)
   {
      ClassLoader referenceLoader = reference.getClassLoader();
      ClassLoader typeLoader = type.getClassLoader();
      if (referenceLoader != null && referenceLoader.equals(typeLoader))
         return true;
      return false;
   }

   private Remote getRemote(Annotated annotated)
   {
      Class<?> clazz = Types.toClass(annotated.getBaseType());
      return Annotations.getAnnotation(clazz, Remote.class);
   }

   private class RemoteAnnotatedType<R> implements AnnotatedType<R>
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
}
