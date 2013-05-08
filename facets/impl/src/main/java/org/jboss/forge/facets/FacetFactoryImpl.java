/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.facets;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.util.Assert;

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
      Assert.notNull(type, "Facet type should not be null");
      ExportedInstance<FACET> instance = registry.getExportedInstance(type);
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
      Assert.notNull(type, "Facet type should not be null");
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
}
