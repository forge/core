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

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetNotFoundException;
import org.jboss.forge.addon.facets.Faceted;
import org.jboss.forge.addon.facets.MutableOrigin;
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
   @SuppressWarnings("unchecked")
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> FACET install(Class<FACET> type, E origin)
            throws FacetNotFoundException
   {
      FACET facet = create(type, origin);
      Faceted<FACET> faceted = (Faceted<FACET>) origin;
      if (!faceted.hasFacet(type))
      {
         faceted.install(facet);
      }
      return facet;
   }
}
