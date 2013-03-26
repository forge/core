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

      Assert.assertEquals(faceted, facet.getOrigin());
   }
}
