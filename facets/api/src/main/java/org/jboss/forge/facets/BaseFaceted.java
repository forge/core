package org.jboss.forge.facets;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * @param <FACETTYPE> the base {@link Facet} type supported by this {@link Faceted} type.
 */
public abstract class BaseFaceted<FACETTYPE extends Facet<?>> implements Faceted<FACETTYPE>
{
   private Set<FACETTYPE> facets = Collections.newSetFromMap(new ConcurrentHashMap<FACETTYPE, Boolean>());

   @Override
   public boolean hasFacet(Class<? extends FACETTYPE> type)
   {
      try
      {
         getFacet(type);
         return true;
      }
      catch (FacetNotFoundException e)
      {
         return false;
      }
   }

   @Override
   public boolean hasAllFacets(Iterable<Class<? extends FACETTYPE>> iterable)
   {
      for (Class<? extends FACETTYPE> type : iterable)
      {
         if (!hasFacet(type))
            return false;
      }
      return true;
   }

   @Override
   public boolean hasAllFacets(Class<? extends FACETTYPE>... types)
   {
      for (Class<? extends FACETTYPE> type : types)
      {
         if (!hasFacet(type))
            return false;
      }
      return true;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <F extends FACETTYPE> F getFacet(Class<F> type) throws FacetNotFoundException
   {
      for (FACETTYPE facet : facets)
      {
         if (type.isAssignableFrom(facet.getClass()))
            return (F) facet;
      }
      throw new FacetNotFoundException("No Facet of type [" + type + "] is installed.");
   }

   @Override
   public Iterable<FACETTYPE> getFacets()
   {
      return Collections.unmodifiableCollection(facets);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <F extends FACETTYPE> Iterable<F> getFacets(Class<F> type)
   {
      Set<F> result = new HashSet<F>();
      for (FACETTYPE facet : facets)
      {
         if (type.isAssignableFrom(facet.getClass()))
            result.add((F) facet);
      }
      return result;
   }

   @Override
   public boolean install(FACETTYPE facet)
   {
      if (facet.getOrigin() != this)
         throw new IllegalArgumentException("[" + facet + "].getOrigin() was [" + facet.getOrigin()
                  + "] but needed to be [" + this + "].");

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
   public boolean uninstall(FACETTYPE facet)
   {
      if (facet.isInstalled())
         return facet.uninstall();
      return true;
   }

}
