package org.jboss.forge.addon.facets;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseFaceted implements Faceted
{
   private Set<Facet<?>> facets = Collections.newSetFromMap(new ConcurrentHashMap<Facet<?>, Boolean>());

   @Override
   public boolean hasFacet(Class<? extends Facet<?>> type)
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
   public boolean hasAllFacets(Iterable<Class<? extends Facet<?>>> iterable)
   {
      for (Class<? extends Facet<?>> type : iterable)
      {
         if (!hasFacet(type))
            return false;
      }
      return true;
   }

   @Override
   public boolean hasAllFacets(Class<? extends Facet<?>>... types)
   {
      for (Class<? extends Facet<?>> type : types)
      {
         if (!hasFacet(type))
            return false;
      }
      return true;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <F extends Facet<?>> F getFacet(Class<F> type) throws FacetNotFoundException
   {
      for (Facet<?> facet : facets)
      {
         if (type.isAssignableFrom(facet.getClass()))
            return (F) facet;
      }
      throw new FacetNotFoundException("No Facet of type [" + type + "] is installed.");
   }

   @Override
   public Iterable<? extends Facet<?>> getFacets()
   {
      return Collections.unmodifiableCollection(facets);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <F extends Facet<?>> Iterable<F> getFacets(Class<F> type)
   {
      Set<F> result = new HashSet<F>();
      for (Facet<?> facet : facets)
      {
         if (type.isAssignableFrom(facet.getClass()))
            result.add((F) facet);
      }
      return result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public boolean install(Facet<?> facet)
   {
      if (facet.getOrigin() != this)
         throw new IllegalArgumentException("Facet.getOrigin() was [" + facet.getOrigin()
                  + "] but needed to be [" + this + "].");

      if (supports((Class<? extends Facet<?>>) facet.getClass()))
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
   public boolean uninstall(Facet<?> facet)
   {
      if (facet.isInstalled())
         return facet.uninstall();
      return true;
   }

}
