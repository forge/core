/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.services;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessProducer;

/**
 * One classloader/thread/weld container per plugin module. One primary executor container running, fires events to each
 * plugin-container.
 * 
 * Multi-threaded bootstrap. Loads primary container, then attaches individual plugin containers as they come up.
 * 
 * Ideas:
 * 
 * Addons may depend on other addons beans, but these beans must be explicitly exposed via the {@link Remote} and
 * {@link Service} API.
 */
public class ContainerServiceExtension implements Extension
{
   public Set<Class<?>> services = new HashSet<Class<?>>();

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void processRemotes(@Observes ProcessAnnotatedType<?> event)
   {
      if (event.getAnnotatedType().getJavaClass().isAnnotationPresent(Remote.class))
      {
         event.setAnnotatedType(new RemoteAnnotatedType(event.getAnnotatedType()));
         services.add(event.getAnnotatedType().getJavaClass());
      }
   }

   public void processRemoteInjectionPoint(@Observes ProcessInjectionPoint<?, ?> event)
   {
      if (event.getInjectionPoint().getAnnotated().isAnnotationPresent(Service.class))
         event.setInjectionPoint(new RemoteInjectionPoint(event.getInjectionPoint()));
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void processProducerHooks(@Observes ProcessProducer<?, ?> event, BeanManager manager)
   {
      if (event.getAnnotatedMember().isAnnotationPresent(Remote.class))
         event.setProducer(new RemoteProxyBeanProducer(event.getProducer()));
   }
}
