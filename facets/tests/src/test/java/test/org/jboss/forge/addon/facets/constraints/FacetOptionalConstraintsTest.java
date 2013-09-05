package test.org.jboss.forge.addon.facets.constraints;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.addon.facets.factory.MockFacet;
import test.org.jboss.forge.addon.facets.factory.MockFaceted;

@RunWith(Arquillian.class)
public class FacetOptionalConstraintsTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:facets"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addPackages(true, FacetM.class.getPackage())
               .addClasses(MockFaceted.class, MockFacet.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:facets"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );
      return archive;
   }

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testOptionalDependenciesAreNotInstalled() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.install(faceted, FacetM.class);

      Assert.assertTrue(faceted.hasFacet(FacetM.class));
      Assert.assertFalse(faceted.hasFacet(FacetO.class));
      Assert.assertFalse(faceted.hasFacet(FacetP.class));
   }

   @Test
   public void testTransitiveRequiredFacetsOfOptionalDependenciesAreNotInstalled() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.install(faceted, FacetN.class);

      Assert.assertTrue(faceted.hasFacet(FacetN.class));
      Assert.assertTrue(faceted.hasFacet(FacetP.class));
      Assert.assertFalse(faceted.hasFacet(FacetO.class));
   }

   @Test
   public void testTransitiveDependenciesAreRegistered() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.register(faceted, FacetT.class);

      Assert.assertTrue(faceted.hasFacet(FacetT.class));
      Assert.assertTrue(faceted.hasFacet(FacetU.class));
      Assert.assertTrue(faceted.hasFacet(FacetV.class));
   }

   @Test
   public void testOptionalDependenciesAreRegisteredDuringInstallation() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.install(faceted, FacetT2.class);

      Assert.assertTrue(faceted.hasFacet(FacetT2.class));
      Assert.assertTrue(faceted.hasFacet(FacetU.class));
      Assert.assertTrue(faceted.hasFacet(FacetV.class));
   }

}
