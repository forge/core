/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;


/**
 * A {@link Facet} is an access point to common functionality, file manipulations, descriptors that extend a
 * {@link Faceted} instance. When implementing this interface, consider extending {@link AbstractFacet} for convenience.
 * 
 * @param <FACETED> The {@link Faceted} type to which this {@link Facet} may attach.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 * 
 * @see {@link AbstractFacet}
 */
public interface Facet<FACETEDTYPE extends Faceted<?>>
{
   /**
    * Return the {@link Faceted} instance on which this {@link Facet} operates. Each {@link Facet} belongs to a
    * {@link Faceted} object (an object that has facets). Once this {@link Facet} has been installed, the
    * {@link Faceted} instance cannot be changed.
    * <p>
    * Typically, the {@link Faceted} instance is provided at {@link Facet} creation time, when the facet is constructed
    * via the {@link FacetFactory}.
    */
   FACETEDTYPE getFaceted();

   /**
    * Perform necessary setup for this {@link Facet} to be considered installed in the given {@link Faceted} instance.
    * This method should NOT register the facet; facet registration is handled by the {@link Faceted} instance if
    * installation is successful.
    * 
    * @return true if installation was successful, false if not.
    */
   boolean install();

   /**
    * Return true if the {@link Facet} is available for the given {@link Faceted} instance, false if otherwise.
    */
   boolean isInstalled();

   /**
    * Remove this {@link Facet} from its {@link Faceted} instance, and perform any necessary cleanup.
    */
   boolean uninstall();
}
