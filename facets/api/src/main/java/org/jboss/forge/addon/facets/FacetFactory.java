/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

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
    *
    * @throws FacetNotFoundException if no implementation can be found.
    */
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> FACET create(Class<FACET> type)
            throws FacetNotFoundException;

   /**
    * Create a new instance of the given {@link Facet} type. If it is also an instance of {@link MutableOrigin}, then
    * use the given origin instance as the {@link Facet#getOrigin()}.
    *
    * @throws FacetNotFoundException if no implementation can be found.
    */
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> FACET create(Class<FACET> type, E origin)
            throws FacetNotFoundException;

   /**
    * Get all instantiable {@link Facet} instances implementing the given {@link Facet} type. Returns an empty
    * {@link Iterable} if no matching implementations can be found.
    */
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> Iterable<FACET> createFacets(Class<FACET> type);

   /**
    * Get all instantiable {@link Facet} instances implementing the given {@link Facet} type. If it is also an instance
    * of {@link MutableOrigin}, then use the given origin instance as the {@link Facet#getOrigin()}. Returns an empty
    * {@link Iterable} if no matching implementations can be found.
    */
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> Iterable<FACET> createFacets(
            Class<FACET> type, E origin);

   /**
    * Create and installs a new instance of the given {@link Facet} type.
    *
    * @throws FacetNotFoundException if no implementation can be found.
    */
   public <FACET extends Facet<E>, E extends Faceted<? extends Facet<?>>> FACET install(Class<FACET> type, E origin)
            throws FacetNotFoundException;
}
