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

import org.jboss.forge.resources.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ResourceRenamed extends ResourceModified
{
   private final String originalLocation;
   private final String newLocation;

   public ResourceRenamed(final Resource<?> resource, final String originalLocation, final String newLocation)
   {
      super(resource);
      this.originalLocation = originalLocation;
      this.newLocation = newLocation;
   }

   public String getOriginalLocation()
   {
      return originalLocation;
   }

   public String getNewLocation()
   {
      return newLocation;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = (prime * result) + ((newLocation == null) ? 0 : newLocation.hashCode());
      result = (prime * result) + ((originalLocation == null) ? 0 : originalLocation.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      ResourceRenamed other = (ResourceRenamed) obj;
      if (newLocation == null) {
         if (other.newLocation != null)
            return false;
      }
      else if (!newLocation.equals(other.newLocation))
         return false;
      if (originalLocation == null) {
         if (other.originalLocation != null)
            return false;
      }
      else if (!originalLocation.equals(other.originalLocation))
         return false;
      return true;
   }
}
