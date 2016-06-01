/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * @param <FACETTYPE> the base {@link Facet} type supported by this {@link Faceted} type.
 */
public abstract class AbstractFaceted<FACETTYPE extends Facet<?>> implements MutableFaceted<FACETTYPE>
{
   private Set<FACETTYPE> facets = Collections.newSetFromMap(new ConcurrentHashMap<FACETTYPE, Boolean>());

   @Override
   public boolean hasFacet(Class<? extends FACETTYPE> type)
   {
      return safeGetFacet(type) != null;
   }

   @Override
   public boolean hasAllFacets(Class<? extends FACETTYPE>... facetDependencies)
   {
      return hasAllFacets(Arrays.asList(facetDependencies));
   }

   @Override
   public boolean hasAllFacets(Iterable<Class<? extends FACETTYPE>> facetDependencies)
   {
      for (Class<? extends FACETTYPE> facetDependency : facetDependencies)
      {
         if (!hasFacet(facetDependency))
         {
            return false;
         }
      }
      return true;
   }

   @Override
   public <F extends FACETTYPE> F getFacet(Class<F> type) throws FacetNotFoundException
   {
      F facet = safeGetFacet(type);
      if (facet == null)
      {
         throw new FacetNotFoundException("No Facet of type [" + type + "] is installed.");
      }
      else
      {
         return facet;
      }
   }

   @Override
   public <F extends FACETTYPE> Optional<F> getFacetAsOptional(Class<F> type)
   {
      return Optional.<F> ofNullable(safeGetFacet(type));
   }

   @Override
   public Iterable<FACETTYPE> getFacets()
   {
      return Collections.unmodifiableCollection(facets);
   }

   @Override
   public boolean install(FACETTYPE facet)
   {
      if (facet.getFaceted() != this)
         throw new IllegalArgumentException("[" + facet + "].getOrigin() was [" + facet.getFaceted()
                  + "] but needed to be [" + this + "]. If your facet type implements "
                  + MutableFacet.class.getSimpleName() + ", " +
                  "ensure that a valid origin was supplied during facet creation.");

      if (supports(facet))
      {
         if (facet.isInstalled() || facet.install())
         {
            facets.add(facet);
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean register(FACETTYPE facet)
   {
      if (facet.getFaceted() != this)
         throw new IllegalArgumentException("[" + facet + "].getOrigin() was [" + facet.getFaceted()
                  + "] but needed to be [" + this + "]. If your facet type implements "
                  + MutableFacet.class.getSimpleName() + ", " +
                  "ensure that a valid origin was supplied during facet creation.");

      if (supports(facet))
      {
         if (facet.isInstalled())
         {
            facets.add(facet);
            return true;
         }
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <F extends FACETTYPE> Iterable<F> getFacets(Class<F> type)
   {
      Set<F> result = new HashSet<F>();
      for (FACETTYPE facet : facets)
      {
         if (type.isInstance(facet))
         {
            result.add((F) facet);
         }
      }
      return result;
   }

   /**
    * Returns the installed facet that is an instance of the provided type argument, null otherwise.
    * 
    * It does not throw any exception
    * 
    * @param type the facet type
    * @return the Facet if found, otherwise, null
    */
   @SuppressWarnings("unchecked")
   private <F extends FACETTYPE> F safeGetFacet(Class<F> type)

   {
      for (FACETTYPE facet : facets)
      {
         if (type.isInstance(facet))
         {
            return (F) facet;
         }
      }
      return null;
   }

   @Override
   public boolean uninstall(FACETTYPE facet)
   {
      return facet.isInstalled() ? (facet.uninstall() && facets.remove(facet))
               : (!facets.contains(facet) || facets.remove(facet));
   }

   @Override
   public boolean unregister(FACETTYPE facet)
   {
      return facet.isInstalled() ? false : facets.remove(facet);
   }

}
