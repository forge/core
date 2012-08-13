/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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

      for (Object e : events)
      {
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
      protected List<Resource<?>> doListResources()
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
