/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl;

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
import org.jboss.forge.container.services.Remote;
import org.jboss.forge.container.services.RemoteAnnotatedType;
import org.jboss.forge.container.services.RemoteInjectionPoint;
import org.jboss.forge.container.services.RemoteProxyBeanProducer;
import org.jboss.forge.container.services.Service;

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
   public void processRemotes(@Observes ProcessAnnotatedType<?> event) throws InstantiationException, IllegalAccessException
   {
      Class<?> type = event.getAnnotatedType().getJavaClass();
      if (type.isAnnotationPresent(Remote.class))
      {
         event.setAnnotatedType(new RemoteAnnotatedType(event.getAnnotatedType()));
         if (type.getClassLoader().equals(Thread.currentThread().getContextClassLoader()))
         {
            services.add(event.getAnnotatedType().getJavaClass());
         }
      }
   }

   public void processRemoteInjectionPoint(@Observes ProcessInjectionPoint<?, ?> event)
   {
      Annotated annotated = event.getInjectionPoint().getAnnotated();
      if (annotated.isAnnotationPresent(Service.class))
         event.setInjectionPoint(new RemoteInjectionPoint(event.getInjectionPoint()));
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
