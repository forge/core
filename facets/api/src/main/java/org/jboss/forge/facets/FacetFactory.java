/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.facets;

import org.jboss.forge.container.services.Exported;

/**
 * Responsible for instantiation of new {@link Facet} instances.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface FacetFactory
{
   /**
    * Create a new instance of the given {@link Facet} type.
    */
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> FACET create(Class<FACET> type);

   /**
    * Create a new instance of the given {@link Facet} type. If it is also an instance of {@link MutableOrigin}, then
    * use the given instance as the {@link Facet#getOrigin()}.
    */
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> FACET create(Class<FACET> type, E origin);

   /**
    * Returns all the facets that implements a certain {@link Facet} type
    */
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> Iterable<FACET> createFacets(Class<FACET> type);

   /**
    * Returns all the facets that implements a certain {@link Facet} type. If it is also an instance of
    * {@link MutableOrigin}, then use the given instance as the {@link Facet#getOrigin()}.
    */
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> Iterable<FACET> createFacets(
            Class<FACET> type, E origin);
}
