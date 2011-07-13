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
package org.jboss.forge.shell.resources;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.bus.spi.EventBusGroomer;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.VirtualResource;
import org.jboss.forge.resources.events.ResourceEvent;

/**
 * Publishes file change events to the shell output stream.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResourceEventGroomer implements EventBusGroomer
{
   @Override
   public List<Object> groom(final List<Object> events)
   {
      List<Resource<?>> seen = new ArrayList<Resource<?>>();
      List<Object> result = new ArrayList<Object>();

      for (Object e : events) {
         if (e instanceof ResourceEvent)
         {
            Resource<?> resource = ((ResourceEvent) e).getResource();
            if (seen.contains(resource))
            {
               result.remove(seen.indexOf(resource));
               seen.remove(seen.indexOf(resource));
            }
            result.add(e);
            seen.add(resource);
         }
         else
         {
            result.add(e);
            seen.add(new Placeholder(null));
         }
      }

      return result;
   }

   private final class Placeholder extends VirtualResource<Object>
   {
      private Placeholder(final Resource<?> parent)
      {
         super(null);
      }

      @Override
      public boolean delete() throws UnsupportedOperationException
      {
         return true;
      }

      @Override
      public boolean delete(final boolean recursive) throws UnsupportedOperationException
      {
         return true;
      }

      @Override
      public String getName()
      {
         return "";
      }

      @Override
      public List<Resource<?>> listResources()
      {
         return new ArrayList<Resource<?>>();
      }

      @Override
      public Object getUnderlyingResourceObject()
      {
         return new Object();
      }
   }
}
