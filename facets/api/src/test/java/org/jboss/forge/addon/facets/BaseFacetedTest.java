package org.jboss.forge.addon.facets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class BaseFacetedTest
{
   @Test
   public void testInstall()
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = new MockFacet(faceted);

      Assert.assertTrue(faceted.install(facet));
      Assert.assertTrue(faceted.hasFacet(MockFacet.class));
      List<Class<? extends Facet<?>>> list = new ArrayList<Class<? extends Facet<?>>>();
      list.add(MockFacet.class);
      Assert.assertTrue(faceted.hasAllFacets(list));
      Assert.assertEquals(facet, faceted.getFacet(MockFacet.class));
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

      Assert.assertTrue(faceted.supports(MockFacet.class));
      Assert.assertFalse(faceted.supports(MockFacet2.class));
   }
}
