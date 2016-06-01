/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
public interface MutableFaceted<FACETTYPE extends Facet<?>> extends Faceted<FACETTYPE>
{
   /**
    * Install and register the given {@link Facet}. If the facet is already installed, register it and return
    * <code>true</code>. If the facet could not be installed, return <code>false</code>;
    */
   boolean install(FACETTYPE facet);

   /**
    * Attempt to add the given {@link Facet} in the internal registry. If {@link Facet#isInstalled()} returns
    * <code>true</code>, add it, otherwise return <code>false</code>. If addition fails, return <code>false</code>.
    */
   boolean register(FACETTYPE facet);

   /**
    * Remove the given {@link Facet} from the internal registry. If {@link Facet#isInstalled()} returns
    * <code>true</code>, do nothing and return <code>false</code>. If removal fails, return <code>false</code>.
    */
   boolean unregister(FACETTYPE facet);

   /**
    * Remove the given {@link Facet} from the internal registry.
    */
   boolean uninstall(FACETTYPE facet);
}
