/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

import java.util.Collection;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public interface Faceted
{

   /**
    * Return true if a facet of the given type is present; return false otherwise.
    */
   boolean hasFacet(Class<? extends Facet> type);

   /**
    * Return true if all {@link Facet}s of the given types are present; otherwise, if any of the given facet types
    * is missing, return false.
    */
   boolean hasAllFacets(Collection<Class<? extends Facet>> facetDependencies);

   /**
    * Return true if all {@link Facet}s of the given types are present; otherwise, if any of the given facet types
    * is missing, return false.
    */
   boolean hasAllFacets(Class<? extends Facet>... facetDependencies);

   /**
    * Return the instance of the requested {@link Facet} type, or throw a {@link FacetNotFoundException} if no
    * {@link Facet} of that type is registered.
    */
   <F extends Facet> F getFacet(Class<F> type) throws FacetNotFoundException;

   /**
    * Return a {@link Collection} of the currently installed {@link Facet}s. Return an empty list if no facets of that
    * type were found.
    */
   Collection<Facet> getFacets();

   /**
    * Return a {@link Collection} of the currently installed {@link Facet}s matching the given type.
    */
   <F extends Facet> Collection<F> getFacets(Class<F> type);

   /**
    * Install and register the given {@link Facet}. If the facet is already installed, register it instead (See
    * {@link #registerFacet(Facet)}.
    */
   boolean installFacet(Facet facet);

   /**
    * Add the given {@link Facet} to the internal collection of installed facets.
    */
   boolean registerFacet(Facet facet);

   /**
    * Remove the given {@link Facet} from the internal collection of installed facets.
    */
   boolean unregisterFacet(Facet facet);

   /**
    * Remove the given {@link Facet} from the internal collection of installed facets.
    */
   boolean removeFacet(Facet facet);

   /**
    * Return true if the given {@link Facet} is supported.
    */
   boolean supportsFacet(Facet facet);
}
