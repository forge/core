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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.shell.events.CommandExecuted;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
// TODO @CommandScoped
@Singleton
public class EventBus
{
   @Inject
   private BeanManager manager;

   private final Map<Object, Annotation[]> map = new HashMap<Object, Annotation[]>();

   public void enqueue(final Object event)
   {
      map.put(event, new Annotation[] {});
   }

   public void enqueue(final Object event, final Annotation[] qualifiers)
   {
      map.put(event, qualifiers);
   }

   @SuppressWarnings("unused")
   private void fire(@Observes final CommandExecuted event)
   {
      List<Exception> thrown = new ArrayList<Exception>();
      try
      {
         for (Entry<Object, Annotation[]> e : map.entrySet())
         {
            try
            {
               manager.fireEvent(e.getKey(), e.getValue());
            }
            catch (Exception e1)
            {
               e1.printStackTrace();
            }
         }
      }
      finally
      {
         map.clear();
      }

      if (!thrown.isEmpty())
         throw new EventBusQueuedException(thrown);
   }
}
