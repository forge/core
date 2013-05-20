/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.repositories;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.util.Assert;

/**
 * Represents an {@link Addon} dependency as specified in its originating {@link AddonRepository}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonDependencyEntry
{
   private AddonId id;
   private boolean exported;
   private boolean optional;

   public AddonId getId()
   {
      return id;
   }

   public boolean isOptional()
   {
      return optional;
   }

   public boolean isExported()
   {
      return exported;
   }

   public static AddonDependencyEntry create(AddonId id)
   {
      return create(id, false, false);
   }

   public static AddonDependencyEntry create(AddonId id, boolean exported, boolean optional)
   {
      Assert.notNull(id, "AddonId must not be null.");

      AddonDependencyEntry entry = new AddonDependencyEntry();
      entry.id = id;
      entry.exported = exported;
      entry.optional = optional;
      return entry;
   }

   @Override
   public String toString()
   {
      return id + ": exported=" + exported + ", optional=" + optional;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AddonDependencyEntry other = (AddonDependencyEntry) obj;
      if (id == null)
      {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      return true;
   }

}
