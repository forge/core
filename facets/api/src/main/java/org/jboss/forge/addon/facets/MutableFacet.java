/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

/**
 * A {@link Facet} with a mutable {@link #getFaceted()}.
 * 
 * @param <FACETEDTYPE> The {@link Faceted} type to which this {@link Facet} may attach.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface MutableFacet<FACETEDTYPE extends Faceted<?>> extends Facet<FACETEDTYPE>
{
   /**
    * Set the {@link Faceted} origin to which this {@link Facet} belongs. Should only be set once, since each
    * {@link Faceted} instance receives its own unique instance of all compatible {@link Facet} types. This method must
    * be called before invoking any operations on {@code this} instance.
    */
   void setFaceted(FACETEDTYPE origin);
}
