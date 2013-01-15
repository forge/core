/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessProducer;

import net.sf.cglib.proxy.Enhancer;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.events.CrossContainerObserverMethod;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.services.Exported;
import org.jboss.forge.container.services.ExportedInstanceInjectionPoint;
import org.jboss.forge.container.services.ExportedInstanceProxyBeanCallback;
import org.jboss.forge.container.util.Annotations;
import org.jboss.forge.container.util.BeanManagerUtils;
import org.jboss.forge.container.util.Types;
import org.jboss.forge.container.util.cdi.BeanBuilder;
import org.jboss.forge.container.util.cdi.ContextualLifecycle;

public class ContainerServiceExtension implements Extension
{
   private static Logger logger = Logger.getLogger(ContainerServiceExtension.class.getName());

   private Map<Class<?>, AnnotatedType<?>> services = new HashMap<Class<?>, AnnotatedType<?>>();
   private Map<Class<?>, InjectionPoint> requestedServices = new HashMap<Class<?>, InjectionPoint>();
   private static final ServiceLiteral SERVICE_LITERAL = new ServiceLiteral();

   public void processRemotes(@Observes ProcessAnnotatedType<?> event) throws InstantiationException,
            IllegalAccessException
   {
      Class<?> type = event.getAnnotatedType().getJavaClass();
      if (Annotations.isAnnotationPresent(type, Exported.class))
      {
         if (type.getClassLoader().equals(Thread.currentThread().getContextClassLoader()))
         {
            services.put(event.getAnnotatedType().getJavaClass(), event.getAnnotatedType());
         }
      }
   }

   public void processRemoteInjectionPointConsumer(@Observes ProcessInjectionPoint<?, ?> event, BeanManager manager)
   {
      Annotated annotated = event.getInjectionPoint().getAnnotated();

      Exported remote = getRemote(annotated);
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
            event.setInjectionPoint(new ExportedInstanceInjectionPoint(event.getInjectionPoint(), SERVICE_LITERAL));
            requestedServices.put(injectionBeanValueType, event.getInjectionPoint());
         }
      }
      else if (remote != null)
      {
         logger.fine("Not @Remote type " + annotated);
      }
   }

   public void processProducerHooks(@Observes ProcessProducer<?, ?> event, BeanManager manager)
   {
      Class<?> type = Types.toClass(event.getAnnotatedMember().getJavaMember());
      ClassLoader classLoader = type.getClassLoader();
      if (type != null && classLoader != null && classLoader.equals(Thread.currentThread().getContextClassLoader()))
      {
         if (Annotations.isAnnotationPresent(type, Exported.class))
         {
            services.put(type, manager.createAnnotatedType(type));
         }
      }
   }

   public void wireCrossContainerEvents(@Observes AfterBeanDiscovery event, final BeanManager manager)
   {
      event.addObserverMethod(new CrossContainerObserverMethod());

      // needs to happen in the addon that is requesting the service
      for (final Entry<Class<?>, InjectionPoint> entry : requestedServices.entrySet())
      {
         Bean<?> serviceBean = new BeanBuilder<Object>(manager)
                  .beanClass(entry.getKey())
                  .types(entry.getValue().getAnnotated().getTypeClosure())
                  .beanLifecycle(new ContextualLifecycle<Object>()
                  {
                     @Override
                     public void destroy(Bean<Object> bean, Object instance, CreationalContext<Object> creationalContext)
                     {
                        creationalContext.release();
                     }

                     @Override
                     public Object create(Bean<Object> bean, CreationalContext<Object> creationalContext)
                     {
                        return produceRemoteService(
                                 BeanManagerUtils.getContextualInstance(manager, AddonRegistry.class), entry.getValue());
                     }

                     private Object produceRemoteService(AddonRegistry registry, InjectionPoint ip)
                     {
                        Member member = ip.getMember();
                        Class<?> type = null;
                        if (member instanceof Method)
                        {
                           type = ((Method) member).getReturnType();
                        }
                        else if (member instanceof Field)
                        {
                           type = ((Field) member).getType();
                        }
                        else
                        {
                           throw new ContainerException(
                                    "Cannot handle producer for non-Field and non-Method member type: " + member);
                        }

                        return Enhancer.create((Class<?>) type, new ExportedInstanceProxyBeanCallback(registry, type,
                                 ip));
                     }
                  })
                  .qualifiers(SERVICE_LITERAL)
                  .create();

         event.addBean(serviceBean);
      }
   }

   /*
    * Helpers
    */
   public Set<Class<?>> getServices()
   {
      return services.keySet();
   }

   private boolean isClassLocal(Class<?> reference, Class<?> type)
   {
      ClassLoader referenceLoader = reference.getClassLoader();
      ClassLoader typeLoader = type.getClassLoader();
      if (referenceLoader != null && referenceLoader.equals(typeLoader))
         return true;
      return false;
   }

   private Exported getRemote(Annotated annotated)
   {
      Class<?> clazz = Types.toClass(annotated.getBaseType());
      return Annotations.getAnnotation(clazz, Exported.class);
   }
}
