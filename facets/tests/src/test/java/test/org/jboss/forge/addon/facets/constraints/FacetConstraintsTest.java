/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.addon.facets.factory.MockFacet;
import test.org.jboss.forge.addon.facets.factory.MockFaceted;

@RunWith(Arquillian.class)
public class FacetConstraintsTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addPackages(true, FacetA.class.getPackage())
               .addClasses(MockFaceted.class, MockFacet.class)
               .addBeansXML();
      return archive;
   }

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testFacetInstallationInstallsDependencies() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.install(faceted, FacetB.class);

      Assert.assertTrue(faceted.hasFacet(FacetB.class));
      Assert.assertTrue(faceted.hasFacet(FacetC.class));
   }

   @Test
   public void testFacetInstallationInstallsNestedDependencies() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.install(faceted, FacetA.class);

      Assert.assertTrue(faceted.hasFacet(FacetA.class));
      Assert.assertTrue(faceted.hasFacet(FacetB.class));
      Assert.assertTrue(faceted.hasFacet(FacetC.class));
   }

   @Test(expected = IllegalStateException.class)
   public void testFacetsCannotDeclareCircularDependencies() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      facetFactory.install(faceted, FacetD.class);
   }

}
