/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * Convenience base class for {@link Project} implementations.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class BaseProject implements Project
{
   private final Set<Facet> facets = new HashSet<Facet>();
   private final Map<String, Object> attributes = new HashMap<String, Object>();

   @Override
   public Object getAttribute(final String key)
   {
      return attributes.get(key);
   }

   @Override
   public void setAttribute(final String key, final Object value)
   {
      attributes.put(key, value);
   }

   @Override
   public void removeAttribute(final String key)
   {
      attributes.remove(key);
   }

   @Override
   public boolean hasFacet(final Class<? extends Facet> type)
   {
      Facet result = null;
      for (Facet facet : facets)
      {
         if ((facet != null) && type.isAssignableFrom(facet.getClass()))
         {
            result = facet;
            break;
         }
      }
      if (result != null)
      {
         return true;
      }
      return false;
   }

   @Override
   public boolean hasAllFacets(final List<Class<? extends Facet>> facetDependencies)
   {
      for (Class<? extends Facet> type : facetDependencies)
      {
         if (!hasFacet(type))
         {
            return false;
         }
      }
      return true;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <F extends Facet> F getFacet(final Class<F> type)
   {
      Facet result = null;
      for (Facet facet : facets)
      {
         if ((facet != null) && type.isAssignableFrom(facet.getClass()))
         {
            result = facet;
            break;
         }
      }
      if (result == null)
      {
         throw new FacetNotFoundException("The requested facet of type [" + type.getName()
                  + "] was not found. The facet is not installed.");
      }
      return (F) result;
   }

   @Override
   public List<Facet> getFacets()
   {
      List<Facet> result = new ArrayList<Facet>();
      result.addAll(facets);
      return result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <F extends Facet> List<F> getFacets(final Class<F> type)
   {
      List<F> result = new ArrayList<F>();

      for (Facet facet : facets)
      {
         if ((facet != null) && facet.getClass().isAssignableFrom(type))
         {
            result.add((F) facet);
         }
      }

      return result;
   }

   @Override
   public Project registerFacet(final Facet facet)
   {
      if (facet == null)
      {
         throw new IllegalArgumentException("Attempted to register 'null' as a Facet; Facets cannot be null.");
      }

      List<Class<? extends Facet>> dependencies = ConstraintInspector.getFacetDependencies(facet.getClass());
      for (Class<? extends Facet> type : dependencies)
      {
         if (!hasFacet(type))
         {
            throw new IllegalStateException("Attempting to register a Facet that has missing dependencies: ["
                     + facet.getClass().getSimpleName() + " requires -> " + type.getSimpleName() + "]");
         }
      }

      facet.setProject(this);
      if (facet.isInstalled() && !hasFacet(facet.getClass()))
      {
         facets.add(facet);
      }
      return this;
   }

   @Override
   public Project installFacet(final Facet facet)
   {
      facet.setProject(this);
      if (!facet.isInstalled() && !hasFacet(facet.getClass()))
      {
         performInstallation(facet);
      }
      else if (!hasFacet(facet.getClass()))
      {
         registerFacet(facet);
      }
      else if (!facet.isInstalled())
      {
         performInstallation(facet);
      }
      return this;
   }

   @Override
   public Project removeFacet(final Facet facet)
   {
      if (facet.isInstalled() && hasFacet(facet.getClass()))
      {
         performRemoval(facet);
      }
      else if (facet.isInstalled())
      {
         performRemoval(facet);
      }
      else if (hasFacet(facet.getClass()))
      {
         unregisterFacet(facet);
      }
      return this;
   }

   @Override
   public Project unregisterFacet(final Facet facet)
   {
      if (facet == null)
      {
         throw new IllegalArgumentException("Attempted to deregister 'null' as a Facet; Facets cannot be null.");
      }

      List<Facet> dependents = new ArrayList<Facet>();
      for (Facet f : getFacets())
      {
         if (ConstraintInspector.getFacetDependencies(f.getClass()).contains(facet.getClass()))
         {
            dependents.add(f);
         }
      }

      for (Facet f : dependents)
      {
         if (hasFacet(f.getClass()))
         {
            removeFacet(f);
         }
      }

      if (!facet.isInstalled())
      {
         facets.remove(facet);
      }
      return this;
   }

   private void performRemoval(final Facet facet)
   {
      if (facet.uninstall())
      {
         for (Facet f : facets)
         {
            if (f.getClass().isAssignableFrom(facet.getClass()))
            {
               facets.remove(f);
               break;
            }
         }
      }
      else
      {
         throw new ProjectModelException("Could not remove " +
                  "facet: [" + ConstraintInspector.getName(facet.getClass()) + "]. " +
                        "Removal was unsuccessful.");
      }
   }

   private void performInstallation(final Facet facet)
   {
      if (facet.install())
      {
         facets.add(facet);
      }
      else
      {
         throw new ProjectModelException("Could not complete installation of " +
                  "facet: [" + ConstraintInspector.getName(facet.getClass()) + "]. " +
                        "Installation was aborted by the Facet during installation.");
      }
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
      BaseProject other = (BaseProject) obj;
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
