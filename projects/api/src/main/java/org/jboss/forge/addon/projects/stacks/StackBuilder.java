/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.stacks;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.furnace.util.Assert;

/**
 * Builder to create {@link Stack} objects. This class implements {@link Stack} for easy consumption. (I.e.: Use this
 * class wherever you need to create and use a new {@link Stack})
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class StackBuilder implements Stack
{
   private final String name;
   private Set<Class<? extends ProjectFacet>> facets = new LinkedHashSet<>();

   private StackBuilder(String name)
   {
      Assert.notNull(name, "Name cannot be null");
      this.name = name;
   }

   public static StackBuilder stack(String name)
   {
      return new StackBuilder(name);
   }

   @Override
   public String getName()
   {
      return name;
   }

   public StackBuilder includes(Class<? extends ProjectFacet> facet)
   {
      facets.add(facet);
      return this;
   }

   public StackBuilder includes(Stack stack)
   {
      facets.addAll(stack.getIncludedFacets());
      return this;
   }

   public StackBuilder excludes(Class<? extends ProjectFacet> facet)
   {
      facets.remove(facet);
      return this;
   }

   @Override
   public Set<Class<? extends ProjectFacet>> getIncludedFacets()
   {
      return facets;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((facets == null) ? 0 : facets.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      StackBuilder other = (StackBuilder) obj;
      if (facets == null)
      {
         if (other.facets != null)
            return false;
      }
      else if (!facets.equals(other.facets))
         return false;
      if (name == null)
      {
         if (other.name != null)
            return false;
      }
      else if (!name.equals(other.name))
         return false;
      return true;
   }
}