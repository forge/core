/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

/**
 * A mutable {@link Faceted} type.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * @param <FACETTYPE> the base {@link Facet} type supported by this {@link MutableFaceted} type.
 */
public interface MutableFaceted<FACETEDTYPE extends Faceted<FACETTYPE, FACETEDTYPE>, FACETTYPE extends Facet<FACETEDTYPE, FACETTYPE>>
         extends Faceted<FACETTYPE, FACETEDTYPE>
{
   /**
    * Install and register the given {@link Facet}. If the facet is already installed, return true.
    */
   boolean install(FACETTYPE facet);

   /**
    * Remove the given {@link Facet} from the internal collection of installed facets.
    */
   boolean uninstall(FACETTYPE facet);
}
