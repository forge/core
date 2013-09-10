/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.events;

import org.jboss.forge.addon.resource.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
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
      if (resource == null)
      {
         if (other.resource != null)
            return false;
      }
      else if (!resource.getFullyQualifiedName().equals(other.resource.getFullyQualifiedName()))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return getClass().getSimpleName() + ": " + resource.getFullyQualifiedName();
   }
}
