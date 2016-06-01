/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.addon.facets.factory;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetNotFoundException;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FacetFactoryTest
{

   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addClasses(FacetFactoryTest.class,
                        MockFacet.class,
                        MockExceptionFacet.class,
                        MockFaceted.class,
                        SubMockFacet.class,
                        SubMockFacet2.class,
                        NotFoundMockFacet.class,
                        TestQualifier.class);
      return archive;
   }

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testFacetFactoryNotNull() throws Exception
   {
      Assert.assertNotNull(facetFactory);
   }

   @Test(expected = FacetNotFoundException.class)
   public void testNotFoundFacetCreation() throws Exception
   {
      facetFactory.create(new MockFaceted(), NotFoundMockFacet.class);
   }

   @Test
   public void testFacetOrigin() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = facetFactory.create(faceted, MockFacet.class);
      Assert.assertEquals(faceted, facet.getFaceted());
   }

   @Test
   public void testMultipleFacetOrigin() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      Iterable<MockFacet> facets = facetFactory.createFacets(faceted, MockFacet.class);
      Iterator<MockFacet> it = facets.iterator();
      Assert.assertTrue(it.hasNext());
      MockFacet first = it.next();
      Assert.assertEquals(faceted, first.getFaceted());
      Assert.assertNotNull(first);
      Assert.assertTrue(it.hasNext());
      MockFacet second = it.next();
      Assert.assertNotNull(second);
      Assert.assertEquals(faceted, second.getFaceted());
      Assert.assertNotSame(first, second);
   }

   @Test
   public void testFacetInstall() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = facetFactory.install(faceted, MockFacet.class);
      Assert.assertNotNull(facet);
      Assert.assertTrue(faceted.hasFacet(MockFacet.class));
   }

   @Test
   public void testFacetThrowsExceptionDuringInstallReturnsFalse() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      Assert.assertFalse(facetFactory.install(faceted, facetFactory.create(faceted, MockExceptionFacet.class)));
   }

   @Test
   public void testFacetThrowsExceptionDuringRegisterReturnsFalse() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      Assert.assertFalse(facetFactory.register(faceted, facetFactory.create(faceted, MockExceptionFacet.class)));
   }

   @Test(expected = IllegalStateException.class)
   public void testFacetThrowsExceptionDuringInstallTypeThrowsException() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.install(faceted, MockExceptionFacet.class);
   }

   @Test(expected = IllegalStateException.class)
   public void testFacetThrowsExceptionDuringRegisterTypeThrowsException() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.register(faceted, MockExceptionFacet.class);
   }
}
