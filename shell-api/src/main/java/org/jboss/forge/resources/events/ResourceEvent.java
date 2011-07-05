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
package org.jboss.forge.resources.events;

import org.jboss.forge.QueuedEvent;
import org.jboss.forge.resources.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@QueuedEvent
public abstract class ResourceEvent
{
   private final Resource<?> resource;

   public ResourceEvent(final Resource<?> resource)
   {
      this.resource = resource;
   }

   public Resource<?> getResource()
   {
      return resource;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = (prime * result)
               + ((resource.getFullyQualifiedName() == null) ? 0 : resource.getFullyQualifiedName().hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ResourceEvent other = (ResourceEvent) obj;
      if (resource == null) {
         if (other.resource != null)
            return false;
      }
      else if (!resource.getFullyQualifiedName().equals(other.resource.getFullyQualifiedName()))
         return false;
      return true;
   }

}
