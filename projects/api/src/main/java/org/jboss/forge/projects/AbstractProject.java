/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.projects;

import java.util.HashMap;
import java.util.Map;

import org.jboss.forge.facets.AbstractFaceted;

/**
 * Convenience base class for {@link Project} implementations.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class AbstractProject extends AbstractFaceted<ProjectFacet> implements Project
{
   private final Map<Object, Object> attributes = new HashMap<Object, Object>();

   @Override
   public Object getAttribute(final Object key)
   {
      return attributes.get(key);
   }

   @Override
   public void setAttribute(final Object key, final Object value)
   {
      attributes.put(key, value);
   }

   @Override
   public void removeAttribute(final Object key)
   {
      attributes.remove(key);
   }

   /*
    * Project instances are the same if they share a common root directory.
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((getProjectRoot() == null) ? 0 : getProjectRoot().hashCode());
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
      AbstractProject other = (AbstractProject) obj;
      if (getProjectRoot() == null)
      {
         if (other.getProjectRoot() != null)
            return false;
      }
      else if (!getProjectRoot().equals(other.getProjectRoot()))
         return false;
      return true;
   }
}
