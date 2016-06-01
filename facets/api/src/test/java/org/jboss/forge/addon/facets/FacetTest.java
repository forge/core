/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

import org.junit.Assert;
import org.junit.Test;

public class FacetTest
{
   @Test
   public void testGetOriginReturnsOriginFromInstantiation()
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = new MockFacet(faceted);

      Assert.assertEquals(faceted, facet.getFaceted());
   }
}
