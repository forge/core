/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets.events;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.furnace.util.Assert;

/**
 * Abstract class for {@link FacetEvent}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractFacetEvent implements FacetEvent
{
   private final Facet<?> facet;

   protected AbstractFacetEvent(Facet<?> facet)
   {
      Assert.notNull(facet, "Facet should not be null");
      this.facet = facet;
   }

   @Override
   public Facet<?> getFacet()
   {
      return facet;
   }
}
