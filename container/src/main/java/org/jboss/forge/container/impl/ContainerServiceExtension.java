/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessProducer;

import org.jboss.forge.container.events.CrossContainerObserverMethod;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.services.Remote;
import org.jboss.forge.container.services.RemoteAnnotatedType;
import org.jboss.forge.container.services.RemoteProxyBeanProducer;
import org.jboss.forge.container.services.RemoteServiceInjectionPoint;
import org.jboss.forge.container.services.Service;
import org.jboss.forge.container.util.Annotations;

/**
 * One classloader/thread/weld container per plugin module. One primary executor container running, fires events to each
 * plugin-container.
 * 
 * Addons may depend on other addons beans, but these beans must be explicitly exposed via the {@link Remote} and
 * {@link Service} API.
 */
public class ContainerServiceExtension implements Extension
{
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
      if (remote != null)
      {
         Class<?> targetType = toClass(event.getInjectionPoint().getMember().getDeclaringClass());
         Class<?> injectionType = toClass(annotated.getBaseType());

         boolean local = isClassLocal(targetType, injectionType);
         boolean serviceAnnotationPresent = annotated.isAnnotationPresent(Service.class);
         boolean remoteService = remote.service();

         if (serviceAnnotationPresent && !remoteService)
         {
            event.addDefinitionError(new ContainerException(
                     "ERROR: Illegal attempt to inject an instance when the target type [" + targetType.getName()
                              + "] is not @" + Remote.class.getSimpleName() + "(service=true). Remove @"
                              + Service.class.getSimpleName() + " from injection point " + annotated
                              + ", or ensure that the target type is @" + Remote.class.getSimpleName()));
         }
         else if (serviceAnnotationPresent && local)
         {
            event.addDefinitionError(new ContainerException(
                     "ERROR: Illegal attempt to inject a @" + Remote.class.getSimpleName()
                              + " service when the target type [" + targetType.getName()
                              + "] is local. Remove the @" + Service.class.getSimpleName()
                              + " annotation from injection point " + annotated + "."));
         }
         else if (!serviceAnnotationPresent && remoteService && !local)
         {
            event.addDefinitionError(new ContainerException("ERROR: Illegal attempt to @Inject non-local target type ["
                     + targetType.getName() + "]. You must either use the @" + Service.class.getSimpleName()
                     + " qualifier at " + annotated + ", or the target type must be marked @"
                     + Remote.class.getSimpleName() + "(service=false)"));
         }
         else if (serviceAnnotationPresent && remoteService)
         {
            event.setInjectionPoint(new RemoteServiceInjectionPoint(event.getInjectionPoint()));
         }
         else if (!remoteService)
         {
            event.setInjectionPoint(new RemoteServiceInjectionPoint(event.getInjectionPoint()));
         }
      }
      else
      {
         System.out.println("Not @Remote type " + annotated);
      }
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
      Class<?> clazz = toClass(annotated.getBaseType());
      return Annotations.getAnnotation(clazz, Remote.class);
   }

   private Class<?> toClass(Type baseType)
   {
      Class<?> result = null;
      if (baseType instanceof Class)
      {
         result = (Class<?>) baseType;
      }
      else if (baseType instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) baseType;
         Type rawType = parameterizedType.getRawType();
         if (rawType instanceof Class)
         {
            result = (Class<?>) rawType;
         }
      }
      else if (baseType instanceof GenericArrayType)
      {
         GenericArrayType parameterizedType = (GenericArrayType) baseType;
         Type genericType = parameterizedType.getGenericComponentType();
         if (genericType instanceof Class)
         {
            result = (Class<?>) genericType;
         }
      }
      return result;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void processProducerHooks(@Observes ProcessProducer<?, ?> event, BeanManager manager)
   {
      AnnotatedMember<?> annotatedMember = event.getAnnotatedMember();
      if (annotatedMember.isAnnotationPresent(Remote.class))
         event.setProducer(new RemoteProxyBeanProducer(event.getProducer()));
   }

   public Set<Class<?>> getServices()
   {
      return services;
   }
}
