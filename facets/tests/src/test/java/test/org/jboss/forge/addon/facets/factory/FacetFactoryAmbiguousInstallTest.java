/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.addon.facets.factory;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetIsAmbiguousException;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FacetFactoryAmbiguousInstallTest
{

   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addClasses(FacetFactoryAmbiguousInstallTest.class,
                        MockFacet.class,
                        MockFaceted.class,
                        MockAmbiguousFacetInterface.class,
                        MockAmbiguousFacet_1.class,
                        MockAmbiguousFacet_2.class,
                        MockAmbiguousDependentFacet.class,
                        MockSpecificDependentFacet.class
               );
      return archive;
   }

   @Inject
   private FacetFactory facetFactory;

   @Test(expected = FacetIsAmbiguousException.class)
   public void testFacetInstallAmbiguousInterfaceShouldFail() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.install(faceted, MockAmbiguousFacetInterface.class);
      Assert.fail("Should not have been able to install ambiguous Facet type.");
   }

   public void testFacetInstallAmbiguousPreRegistersDependencies() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.install(faceted, MockAmbiguousDependentFacet.class);
      Assert.assertNotNull(faceted.getFacet(MockAmbiguousFacet_1.class));
      Assert.assertNotNull(faceted.getFacet(MockAmbiguousFacet_2.class));
   }

   @Test
   public void testFacetInstallSpecificFacetShouldSucceed() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      MockAmbiguousFacet_1 facet = facetFactory.install(faceted, MockAmbiguousFacet_1.class);
      Assert.assertNotNull(facet);
   }

   @Test
   public void testFacetInstallSpecificFacetViaDependencyShouldSucceed() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      MockSpecificDependentFacet facet = facetFactory.install(faceted, MockSpecificDependentFacet.class);
      Assert.assertNotNull(facet);
   }
}
