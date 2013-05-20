/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.versions;

import org.jboss.forge.furnace.util.Assert;

/**
 * A single, fixed value {@link Version}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SingleVersion implements Version
{
   private String version;

   public SingleVersion(String version)
   {
      Assert.notNull(version, "Version must not be null.");
      this.version = version;
   }

   @Override
   public String toString()
   {
      return version;
   }

   @Override
   public String getVersionString()
   {
      return toString();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      Version other = (Version) obj;
      if (getVersionString() == null)
      {
         if (other.getVersionString() != null)
            return false;
      }
      else if (!getVersionString().equals(other.getVersionString()))
         return false;
      return true;
   }
}
