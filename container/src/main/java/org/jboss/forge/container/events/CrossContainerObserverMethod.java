package org.jboss.forge.container.events;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.services.Exported;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.AddonFilters;
import org.jboss.forge.container.util.Annotations;

public class CrossContainerObserverMethod
{
   public void handleEvent(@Observes @Any Object event, InjectionPoint ip)
   {
      if (Annotations.isAnnotationPresent(event.getClass(), Exported.class))
      {
         Set<Annotation> qualifiers = ip.getQualifiers();
         try
         {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader eventClassLoader = event.getClass().getClassLoader();
            if (contextClassLoader.equals(eventClassLoader))
            {
               AddonRegistry addonRegistry = CDI.current().select(AddonRegistry.class).get();
               for (Addon addon : addonRegistry.getAddons(AddonFilters.allStarted()))
               {
                  // Events should not be fired back into the container from which they originated
                  ClassLoader addonClassLoader = addon.getClassLoader();
                  if (!(event.getClass().getClassLoader().equals(addonClassLoader)
                           || contextClassLoader.equals(addonClassLoader)
                           || ClassLoader.getSystemClassLoader().equals(eventClassLoader)))
                  {
                     ServiceRegistry addonServiceRegistry = addon.getServiceRegistry();
                     BeanManager manager = addonServiceRegistry.getExportedInstance(BeanManager.class).get();
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
