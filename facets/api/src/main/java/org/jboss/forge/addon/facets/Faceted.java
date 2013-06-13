/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * @param <FACETTYPE> the base {@link Facet} type supported by this {@link Faceted} type.
 */
public interface Faceted<FACETTYPE extends Facet<?>>
{
   /**
    * Return true if a facet of the given type is present; return false otherwise.
    */
   boolean hasFacet(Class<? extends FACETTYPE> type);

   /**
    * Return true if all {@link Facet}s of the given types are present; otherwise, if any of the given facet types is
    * missing, return false.
    */
   boolean hasAllFacets(Iterable<Class<? extends FACETTYPE>> facetDependencies);

   /**
    * Return true if all {@link Facet}s of the given types are present; otherwise, if any of the given facet types is
    * missing, return false.
    */
   boolean hasAllFacets(Class<? extends FACETTYPE>... facetDependencies);

   /**
    * Return the instance of the requested {@link Facet} type, or throw a {@link FacetNotFoundException} if no
    * {@link Facet} of that type is installed.
    */
   <F extends FACETTYPE> F getFacet(Class<F> type) throws FacetNotFoundException;

   /**
    * Return a {@link Iterable} of the currently installed {@link Facet}s. Return an empty list if no facets of that
    * type were found.
    */
   Iterable<FACETTYPE> getFacets();

   /**
    * Return a {@link Iterable} of the currently installed {@link Facet}s matching the given type.
    */
   <F extends FACETTYPE> Iterable<F> getFacets(Class<F> type);

   /**
    * Return true if the given {@link Facet} is supported.
    */
   <F extends FACETTYPE> boolean supports(F facet);
}
