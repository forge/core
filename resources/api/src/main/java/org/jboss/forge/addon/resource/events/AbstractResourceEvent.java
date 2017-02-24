/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource.events;

import org.jboss.forge.addon.resource.Resource;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AbstractResourceEvent implements ResourceEvent
{
   private final Resource<?> resource;

   protected AbstractResourceEvent(final Resource<?> resource)
   {
      this.resource = resource;
   }

   @Override
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
         if (other.getResource() != null)
            return false;
      }
      else if (!resource.getFullyQualifiedName().equals(other.getResource().getFullyQualifiedName()))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return getClass().getSimpleName() + ": " + resource.getFullyQualifiedName();
   }

}
