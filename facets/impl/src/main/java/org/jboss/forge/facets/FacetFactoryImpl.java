/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.facets;

import javax.inject.Inject;

import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;

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
}
