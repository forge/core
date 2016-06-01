/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets.events;

import org.jboss.forge.addon.facets.Facet;

/**
 * {@link FacetInstalled} implementation
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class FacetInstalledImpl extends AbstractFacetEvent implements FacetInstalled
{
   public FacetInstalledImpl(Facet<?> facet)
   {
      super(facet);
   }

   @Override
   public String toString()
   {
      return "FacetInstalled [" + getFacet() + "]";
   }
}