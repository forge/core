/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.events;

import org.jboss.forge.addon.resource.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResourceRenamed extends ResourceModified
{
   private final String originalLocation;

   public ResourceRenamed(final Resource<?> resource, final String originalLocation)
   {
      super(resource);
      this.originalLocation = originalLocation;
   }

   public String getOriginalLocation()
   {
      return originalLocation;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((originalLocation == null) ? 0 : originalLocation.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      ResourceRenamed other = (ResourceRenamed) obj;
      if (originalLocation == null)
      {
         if (other.originalLocation != null)
            return false;
      }
      else if (!originalLocation.equals(other.originalLocation))
         return false;
      return true;
   }

}
