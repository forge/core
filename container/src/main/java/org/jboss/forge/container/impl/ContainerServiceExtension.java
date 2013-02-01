/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessProducer;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.events.CrossContainerObserverMethod;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.services.Exported;
import org.jboss.forge.container.services.ExportedInstanceInjectionPoint;
import org.jboss.forge.container.services.ExportedInstanceLazyLoader;
import org.jboss.forge.container.util.Annotations;
import org.jboss.forge.container.util.BeanManagerUtils;
import org.jboss.forge.container.util.Types;
import org.jboss.forge.container.util.cdi.BeanBuilder;
import org.jboss.forge.container.util.cdi.ContextualLifecycle;

public class ContainerServiceExtension implements Extension
{
   private static Logger logger = Logger.getLogger(ContainerServiceExtension.class.getName());

   private Map<Class<?>, AnnotatedType<?>> services = new HashMap<Class<?>, AnnotatedType<?>>();
   private Map<InjectionPoint, Class<?>> requestedServices = new HashMap<InjectionPoint, Class<?>>();
   private Map<InjectionPoint, ServiceLiteral> requestedServiceLiterals = new HashMap<InjectionPoint, ServiceLiteral>();

   public void processRemotes(@Observes ProcessAnnotatedType<?> event) throws InstantiationException,
            IllegalAccessException
   {
      Class<?> type = event.getAnnotatedType().getJavaClass();
      if (Annotations.isAnnotationPresent(type, Exported.class)
               && !(Modifier.isAbstract(type.getModifiers())
               || Modifier.isInterface(type.getModifiers())))
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

      Exported exported = getExported(annotated);
      Class<?> injectionPointDeclaringType = Types.toClass(event.getInjectionPoint().getMember().getDeclaringClass());
      Class<?> injectionBeanValueType = Types.toClass(annotated.getBaseType());

      boolean local = isClassLocal(injectionPointDeclaringType, injectionBeanValueType);
      if (!local)
      {
         if (exported != null)
         {
            ServiceLiteral serviceLiteral = new ServiceLiteral();
            event.setInjectionPoint(new ExportedInstanceInjectionPoint(event.getInjectionPoint(), serviceLiteral));
            requestedServices.put(event.getInjectionPoint(), injectionBeanValueType);
            requestedServiceLiterals.put(event.getInjectionPoint(), serviceLiteral);
         }
      }
      else if (exported != null)
      {
         logger.fine("Not @Exported type " + annotated);
      }
   }

   public void processProducerHooks(@Observes ProcessProducer<?, ?> event, BeanManager manager)
   {
      Class<?> type = Types.toClass(event.getAnnotatedMember().getJavaMember());
      ClassLoader classLoader = type.getClassLoader();
      if (classLoader != null && classLoader.equals(Thread.currentThread().getContextClassLoader()))
      {
         if (Annotations.isAnnotationPresent(type, Exported.class))
         {
            services.put(type, manager.createAnnotatedType(type));
         }
      }
   }

   public void wireCrossContainerServicesAndEvents(@Observes AfterBeanDiscovery event, final BeanManager manager)
   {
      event.addObserverMethod(new CrossContainerObserverMethod());

      // needs to happen in the addon that is requesting the service
      for (final Entry<InjectionPoint, Class<?>> entry : requestedServices.entrySet())
      {
         final InjectionPoint injectionPoint = entry.getKey();
         final Annotated annotated = injectionPoint.getAnnotated();
         final Member member = injectionPoint.getMember();

         Set<Type> typeClosure = annotated.getTypeClosure();
         Class<?> beanClass = entry.getValue();

         Bean<?> serviceBean = new BeanBuilder<Object>(manager)
                  .beanClass(beanClass)
                  .types(typeClosure)
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
                        Class<?> serviceType = null;
                        if (member instanceof Method)
                        {
                           serviceType = ((Method) member).getReturnType();
                        }
                        else if (member instanceof Field)
                        {
                           serviceType = ((Field) member).getType();
                        }
                        else if (member instanceof Constructor)
                        {
                           if (annotated instanceof AnnotatedParameter)
                           {
                              serviceType = ((Constructor<?>) member).getParameterTypes()[((AnnotatedParameter<?>) annotated)
                                       .getPosition()];
                           }
                        }
                        else
                        {
                           throw new ContainerException(
                                    "Cannot handle producer for non-Field and non-Method member type: " + member);
                        }

                        return ExportedInstanceLazyLoader.create(
                                 BeanManagerUtils.getContextualInstance(manager, AddonRegistry.class),
                                 injectionPoint,
                                 serviceType
                                 );
                     }
                  })
                  .qualifiers(requestedServiceLiterals.get(injectionPoint))
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

   private Exported getExported(Annotated annotated)
   {
      Class<?> clazz = Types.toClass(annotated.getBaseType());
      return Annotations.getAnnotation(clazz, Exported.class);
   }
}
