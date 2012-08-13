/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.bus;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.bus.cdi.BusManaged;
import org.jboss.forge.bus.cdi.ObserverCaptureExtension;
import org.jboss.forge.bus.event.BusEvent;
import org.jboss.forge.bus.spi.EventBusGroomer;
import org.jboss.forge.bus.util.Annotations;

/**
 * Simple bus for postponing event firing.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
// TODO @CommandScoped, and this should be used by another bean in Shell
@Singleton
public class EventBus
{
   @Inject
   private BeanManager manager;

   @Inject
   private ObserverCaptureExtension extension;

   private final Map<Object, Annotation[]> map = new HashMap<Object, Annotation[]>();
   private List<Object> events = new ArrayList<Object>();

   boolean firing = false;

   private ArrayList<EventBusGroomer> groomers;

   @SuppressWarnings("unused")
   private void observeAll(@Observes @Any final Object event)
   {
      if (handles(event) && !hasQueued(event))
      {
         enqueue(event);
      }
   }

   /**
    * Add the given event to the queue.
    */
   public void enqueue(final Object event)
   {
      if (!firing)
      {
         events.add(event);
         map.put(event, new Annotation[] {});
      }
   }

   /**
    * Add the given event to the queue; this event will be fired with the supplied qualifiers.
    */
   public void enqueue(final Object event, final Annotation[] qualifiers)
   {
      if (!firing)
      {
         events.add(event);
         map.put(event, qualifiers);
      }
   }

   /**
    * Fire all queued events.
    */
   public void fireAll()
   {
      firing = true;
      List<Exception> thrown = new ArrayList<Exception>();

      if (groomers == null)
      {
         groomers = new ArrayList<EventBusGroomer>();
         ServiceLoader<EventBusGroomer> services = ServiceLoader.load(EventBusGroomer.class);
         for (EventBusGroomer groomer : services) {
            groomers.add(groomer);
         }
      }

      for (EventBusGroomer groomer : groomers) {
         events = groomer.groom(events);
      }

      try
      {
         for (Object event : events)
         {
            if (map.containsKey(event))
            {
               try
               {
                  Annotation[] value = map.get(event);
                  fireSingle(event, value);
               }
               catch (Exception e1)
               {
                  thrown.add(e1);
               }
            }
            else
            {
               throw new IllegalStateException("Queued event was not found in event Map");
            }
         }
      }
      finally
      {
         firing = false;
         map.clear();
         events.clear();
      }

      // Squelch these for now
      // if (!thrown.isEmpty())
      // throw new EventBusQueuedException(thrown);
   }

   public boolean hasQueued(final Object event)
   {
      return map.containsKey(event);
   }

   public void fireSingle(final Object event)
   {
      List<BusManaged> qualifiers = extension.getEventQualifiers(event
               .getClass());

      fireSingle(event, qualifiers.toArray(new Annotation[] {}));
   }

   public void fireSingle(final Object event, final Annotation... annotations)
   {
      List<BusManaged> qualifiers = extension.getEventQualifiers(event
               .getClass());

      for (BusManaged managed : qualifiers) {
         List<Annotation> toFire = new ArrayList<Annotation>();
         toFire.addAll(Arrays.asList(annotations));
         toFire.add(managed);
         manager.fireEvent(event, toFire.toArray(new Annotation[] {}));
      }

   }

   public boolean handles(final Object event)
   {
      return Annotations.isAnnotationPresent(event.getClass(), BusEvent.class);
   }
}
