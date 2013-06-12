/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.ExportedInstance;
import org.jboss.forge.furnace.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacetFactoryImpl implements FacetFactory
{
   @Inject
   private AddonRegistry registry;

   @Override
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> FACET create(Class<FACET> type)
   {
      Assert.notNull(type, "Facet type must not be null.");
      ExportedInstance<FACET> instance = registry.getExportedInstance(type);
      if (instance == null)
         throw new FacetNotFoundException("Could not find Facet of type [" + type.getName() + "]");
      return instance.get();
   }

   @Override
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> FACET create(Class<FACET> type, E origin)
   {
      FACET instance = create(type);
      if (instance instanceof MutableOrigin)
         ((MutableOrigin<E>) instance).setOrigin(origin);
      else
         throw new IllegalArgumentException("Facet type [" + type.getName() + "] does not support setting an origin.");
      return instance;
   }

   @Override
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> Iterable<FACET> createFacets(Class<FACET> type)
   {
      Assert.notNull(type, "Facet type must not be null.");
      Set<ExportedInstance<FACET>> instances = registry.getExportedInstances(type);
      Set<FACET> facets = new HashSet<FACET>(instances.size());
      for (ExportedInstance<FACET> instance : instances)
      {
         facets.add(instance.get());
      }
      return facets;
   }

   @Override
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> Iterable<FACET> createFacets(
            Class<FACET> type, E origin)
   {
      Iterable<FACET> facets = createFacets(type);
      for (FACET facet : facets)
      {
         if (facet instanceof MutableOrigin)
            ((MutableOrigin<E>) facet).setOrigin(origin);
         else
            throw new IllegalArgumentException("Facet type [" + type.getName()
                     + "] does not support setting an origin.");
      }
      return facets;
   }

   @Override
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> FACET install(Class<FACET> type, E origin)
            throws FacetNotFoundException
   {
      FACET facet = create(type, origin);
      if (!install(facet, origin))
      {
         throw new IllegalStateException("Facet type [" + type.getName()
                  + "] could not be installed completely into [" + origin
                  + "] of type [" + origin.getClass().getName()
                  + "]. You may wish to check for inconsistent origin state.");
      }
      return facet;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> boolean install(FACET facet, E origin)
   {
      Assert.notNull(origin, "Facet instance must not be null.");
      Assert.notNull(origin, "Origin instance must not be null.");

      Faceted<FACET> faceted = (Faceted<FACET>) origin;
      Assert.isTrue(faceted instanceof MutableFaceted, "The given origin [" + origin + "] is not an instance of ["
               + MutableFaceted.class.getName() + "], and does not support " + Facet.class.getSimpleName()
               + " installation.");

      if (facet.getOrigin() == null && facet instanceof MutableOrigin)
      {
         ((MutableOrigin<E>) facet).setOrigin(origin);
      }

      Assert.isTrue(origin.equals(facet.getOrigin()), "The given origin [" + origin + "] is not an instance of ["
               + MutableFaceted.class.getName() + "], and does not support " + Facet.class.getSimpleName()
               + " installation.");

      boolean result = false;
      if (faceted.hasFacet((Class<? extends FACET>) facet.getClass()))
         result = true;
      else
         result = ((MutableFaceted<FACET>) faceted).install(facet);
      return result;
   }
}
