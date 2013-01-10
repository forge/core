package org.jboss.forge.container.events;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.ObserverMethod;

import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.AddonFilters;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.services.Remote;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.Annotations;

public class CrossContainerObserverMethod implements ObserverMethod<Object>
{

   @Override
   public Class<?> getBeanClass()
   {
      return Object.class;
   }

   @Override
   public Type getObservedType()
   {
      return Object.class;
   }

   @Override
   public Set<Annotation> getObservedQualifiers()
   {
      return new HashSet<Annotation>(Arrays.asList(new Any()
      {
         @Override
         public Class<? extends Annotation> annotationType()
         {
            return Any.class;
         }
      }));
   }

   @Override
   public Reception getReception()
   {
      return Reception.ALWAYS;
   }

   @Override
   public TransactionPhase getTransactionPhase()
   {
      return TransactionPhase.IN_PROGRESS;
   }

   @Override
   @SuppressWarnings("unchecked")
   public void notify(Object event)
   {
      notify(event, Collections.EMPTY_SET);
   }

   @Override
   public void notify(final Object event, final Set<Annotation> qualifiers)
   {
      if (Annotations.isAnnotationPresent(event.getClass(), Remote.class))
      {
         try
         {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader eventClassLoader = event.getClass().getClassLoader();
            if (contextClassLoader.equals(eventClassLoader))
            {
               AddonRegistry addonRegistry = CDI.current().select(AddonRegistry.class).get();
               for (Addon addon : addonRegistry.getRegisteredAddons(AddonFilters.allStarted()))
               {
                  // Events should not be fired back into the container from which they originated
                  ClassLoader addonClassLoader = addon.getClassLoader();
                  if (!(event.getClass().getClassLoader().equals(addonClassLoader)
                           || contextClassLoader.equals(addonClassLoader)
                           || ClassLoader.getSystemClassLoader().equals(eventClassLoader)))
                  {
                     ServiceRegistry addonServiceRegistry = addon.getServiceRegistry();
                     BeanManager manager = addonServiceRegistry.getRemoteInstance(BeanManager.class).get();
                     manager.fireEvent(event, qualifiers.toArray(new Annotation[] {}));
                  }
               }
            }
         }
         catch (Exception e)
         {
            throw new ContainerException("Problems encountered during propagation of event [" + event
                     + "] with qualifiers [" + qualifiers + "]", e);
         }
      }
      else
      {
         // do not propagate non-remote events to other containers.
      }
   }

}
