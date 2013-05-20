/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.events;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.EventMetadata;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.services.Exported;
import org.jboss.forge.furnace.services.ServiceRegistry;
import org.jboss.forge.furnace.util.AddonFilters;
import org.jboss.forge.furnace.util.Annotations;

public class CrossContainerObserverMethod
{
   public void handleEvent(@Observes @Any Object event, EventMetadata metadata)
   {
      if (Annotations.isAnnotationPresent(event.getClass(), Exported.class))
      {
         Set<Annotation> qualifiers = metadata.getQualifiers();
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
