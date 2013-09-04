/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.facets;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FacetFactoryAmbiguousInstallTest
{

   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:facets"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addClasses(FacetFactoryAmbiguousInstallTest.class,
                        MockFacet.class,
                        MockFaceted.class,
                        MockAmbiguousFacetInterface.class,
                        MockAmbiguousFacet_1.class,
                        MockAmbiguousFacet_2.class,
                        MockAmbiguousDependentFacet.class,
                        MockSpecificDependentFacet.class
               )
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:facets"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
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

   @Test(expected = FacetIsAmbiguousException.class)
   public void testFacetInstallAmbiguousViaDependencyShouldFail() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.install(faceted, MockAmbiguousDependentFacet.class);
      Assert.fail("Should not have been able to install ambiguous Facet type.");
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
