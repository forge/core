/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

import org.jboss.forge.addon.facets.events.FacetEvent;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Responsible for instantiation of new {@link Facet} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface FacetFactory
{
   /**
    * Create a new instance of the given {@link Facet} type. If it is also an instance of {@link MutableFacet}, then use
    * the given origin instance as the {@link Facet#getFaceted()}.
    * 
    * @throws FacetNotFoundException if no implementation can be found.
    * @throws FacetIsAmbiguousException if the given facet type is an interface or abstract class, and multiple
    *            implementations were found.
    */
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> FACETTYPE create(
            FACETEDTYPE origin, Class<FACETTYPE> type) throws FacetNotFoundException, FacetIsAmbiguousException;

   /**
    * Get all instantiable {@link Facet} instances implementing the given {@link Facet} type. If it is also an instance
    * of {@link MutableFacet}, then use the given origin instance as the {@link Facet#getFaceted()}. Returns an empty
    * {@link Iterable} if no matching implementations can be found.
    */
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> Iterable<FACETTYPE> createFacets(
            FACETEDTYPE origin, Class<FACETTYPE> type);

   /**
    * Create and installs a new instance of the given {@link Facet} type.
    * 
    * @return the new {@link Facet} instance. (Never null.)
    * 
    * @throws FacetNotFoundException if no implementation can be found.
    * @throws FacetIsAmbiguousException if the given facet type is an interface or abstract class, and multiple
    *            implementations were found.
    * @throws IllegalStateException if installation failed
    */
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> FACETTYPE install(
            FACETEDTYPE origin, Class<FACETTYPE> type) throws FacetNotFoundException, IllegalStateException,
            FacetIsAmbiguousException;

   /**
    * Create and installs a new instance of the given {@link Facet} type.
    * 
    * @return the new {@link Facet} instance. (Never null.)
    * 
    * @throws FacetNotFoundException if no implementation can be found.
    * @throws FacetIsAmbiguousException if the given facet type is an interface or abstract class, and multiple
    *            implementations were found.
    * @throws IllegalStateException if installation failed
    */
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> FACETTYPE install(
            FACETEDTYPE origin, Class<FACETTYPE> type, Predicate<FACETTYPE> filter)
            throws FacetNotFoundException, IllegalStateException,
            FacetIsAmbiguousException;

   /**
    * Install a {@link Facet} instance into the given {@link Faceted} origin.
    * 
    * @throws IllegalStateException if installation failed
    * @throws IllegalArgumentException when the given {@link Facet#getFaceted()} is not equal to the specified
    *            {@link Faceted} origin instance, or if the given {@link Faceted} type does not implement
    *            {@link MutableFaceted}.
    * 
    * @return <code>true</code> if installation was successful; <code>false</code> if installation failed.
    */
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean install(
            FACETEDTYPE origin, FACETTYPE facet) throws IllegalArgumentException, IllegalStateException;

   /**
    * Install a {@link Facet} instance into the given {@link Faceted} origin.
    * 
    * @throws IllegalStateException if installation failed
    * @throws IllegalArgumentException when the given {@link Facet#getFaceted()} is not equal to the specified
    *            {@link Faceted} origin instance, or if the given {@link Faceted} type does not implement
    *            {@link MutableFaceted}.
    * 
    * @return <code>true</code> if installation was successful; <code>false</code> if installation failed.
    */
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean install(
            FACETEDTYPE origin, FACETTYPE facet, Predicate<FACETTYPE> filter) throws IllegalArgumentException,
            IllegalStateException;

   /**
    * Register a {@link Facet} type into the given {@link Faceted} origin. (Facets may be registered when their
    * installation requirements have already been met.)
    * 
    * @throws IllegalArgumentException when the given {@link Facet#getFaceted()} is not equal to the specified
    *            {@link Faceted} origin instance, or if the given {@link Faceted} type does not implement
    *            {@link MutableFaceted}.
    * @throws FacetNotFoundException if no implementation can be found.
    * @throws FacetIsAmbiguousException if the given facet type is an interface or abstract class, and multiple
    *            implementations were found.
    * @throws IllegalStateException if installation failed.
    * 
    * @return the new {@link Facet} instance. (Never null.)
    * 
    */
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> FACETTYPE register(
            FACETEDTYPE origin, Class<FACETTYPE> type) throws FacetNotFoundException, IllegalStateException,
            FacetIsAmbiguousException, IllegalArgumentException;

   /**
    * Register a {@link Facet} instance into the given {@link Faceted} origin. (Facets may be registered when their
    * installation requirements have already been met.)
    * 
    * @throws IllegalArgumentException when the given {@link Facet#getFaceted()} is not equal to the specified
    *            {@link Faceted} origin instance, or if the given {@link Faceted} type does not implement
    *            {@link MutableFaceted}.
    * 
    * @return <code>true</code> if registration was successful; <code>false</code> if installation failed.
    */
   public <FACETEDTYPE extends Faceted<?>, FACETTYPE extends Facet<FACETEDTYPE>> boolean register(
            FACETEDTYPE origin, FACETTYPE facet) throws IllegalArgumentException;

   /**
    * Add a {@link FacetListener} to be notified when {@link FacetEvent} events occur.
    * 
    * @see FacetListener
    */
   public ListenerRegistration<FacetListener> addFacetListener(FacetListener listener);
}
