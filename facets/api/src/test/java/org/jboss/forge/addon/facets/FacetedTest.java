/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.facets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacetedTest
{
   @Test
   public void testInstall()
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = new MockFacet(faceted);

      Assert.assertTrue(faceted.install(facet));
      Assert.assertTrue(faceted.hasFacet(MockFacet.class));
      List<Class<? extends MockFacet>> list = new ArrayList<Class<? extends MockFacet>>();
      list.add(MockFacet.class);
      for (Class<? extends MockFacet> type : list)
      {
         Assert.assertTrue(faceted.hasFacet(type));
      }
      Assert.assertEquals(facet, faceted.getFacet(MockFacet.class));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testInstallNullOrigin()
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = new MockFacet(null);

      Assert.assertTrue(faceted.install(facet));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testInstallDifferentOrigin()
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = new MockFacet(new MockFaceted());

      Assert.assertTrue(faceted.install(facet));
   }

   @Test
   public void testInstallUnsupported()
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet2 facet = new MockFacet2(faceted);

      Assert.assertFalse(faceted.install(facet));
   }

   @Test
   public void testInstallIsIdempotent()
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = new MockFacet(faceted);

      Assert.assertTrue(faceted.install(facet));
      Assert.assertTrue(faceted.install(facet));

      Assert.assertTrue(faceted.hasFacet(MockFacet.class));

      Iterator<? extends Facet<?>> iterator = faceted.getFacets().iterator();
      Assert.assertEquals(facet, iterator.next());
      Assert.assertFalse(iterator.hasNext());
   }

   @Test
   public void testHasMultipleFacet()
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = new MockFacet(faceted);
      MockFacet3 facet3 = new MockFacet3(faceted);

      Assert.assertTrue(faceted.install(facet));
      Assert.assertTrue(faceted.install(facet3));

      Assert.assertTrue(faceted.hasAllFacets(MockFacet.class, MockFacet3.class));

      Assert.assertTrue(faceted.hasAllFacets(Arrays.asList(MockFacet.class, MockFacet3.class)));
   }

   @Test
   public void testUnInstall()
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = new MockFacet(faceted);

      Assert.assertTrue(faceted.uninstall(facet));
      Assert.assertTrue(faceted.install(facet));
      Assert.assertTrue(faceted.uninstall(facet));
   }

   @Test
   public void testSupports()
   {
      MockFaceted faceted = new MockFaceted();

      Assert.assertTrue(faceted.supports(new MockFacet(faceted)));
      Assert.assertFalse(faceted.supports(new MockFacet2(faceted)));
   }

   @Test
   public void testOptional()
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = new MockFacet(faceted);
      Assert.assertFalse(faceted.getFacetAsOptional(MockFacet.class).isPresent());
      faceted.install(facet);
      Assert.assertTrue(faceted.getFacetAsOptional(MockFacet.class).isPresent());
   }
}
