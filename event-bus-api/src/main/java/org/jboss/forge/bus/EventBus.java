/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.bus;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.bus.cdi.BusManaged;
import org.jboss.forge.bus.cdi.ObserverCaptureExtension;
import org.jboss.forge.bus.event.BusEvent;
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

   boolean firing = false;

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
         map.put(event, new Annotation[] {});
   }

   /**
    * Add the given event to the queue; this event will be fired with the supplied qualifiers.
    */
   public void enqueue(final Object event, final Annotation[] qualifiers)
   {
      if (!firing)
         map.put(event, qualifiers);
   }

   /**
    * Fire all queued events.
    */
   public void fireAll()
   {
      firing = true;
      List<Exception> thrown = new ArrayList<Exception>();
      try
      {
         for (Entry<Object, Annotation[]> e : map.entrySet())
         {
            try
            {
               Object key = e.getKey();
               Annotation[] value = e.getValue();

               fireSingle(key, value);
            }
            catch (Exception e1)
            {
               thrown.add(e1);
            }
         }
      }
      finally
      {
         firing = false;
         map.clear();
      }

      if (!thrown.isEmpty())
         throw new EventBusQueuedException(thrown);
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
