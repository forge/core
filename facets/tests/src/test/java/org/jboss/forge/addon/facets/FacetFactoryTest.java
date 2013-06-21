/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.facets;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetNotFoundException;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FacetFactoryTest
{

   @Deployment
   @Dependencies(@Addon(name = "org.jboss.forge.addon:facets", version = "2.0.0-SNAPSHOT"))
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addClasses(FacetFactoryTest.class,
                        MockFacet.class,
                        MockFaceted.class,
                        SubMockFacet.class,
                        NotFoundMockFacet.class,
                        TestQualifier.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:facets", "2.0.0-SNAPSHOT")));
      return archive;
   }

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testFacetFactoryNotNull() throws Exception
   {
      Assert.assertNotNull(facetFactory);
   }

   @Test
   public void testNotFoundFacetCreation() throws Exception
   {
      try
      {
         facetFactory.create(NotFoundMockFacet.class, new MockFaceted());
      }
      catch (Throwable e)
      {
         boolean found = false;
         while (e.getCause() != null && e.getCause() != e)
         {
            e = e.getCause();
            if (e instanceof FacetNotFoundException)
            {
               found = true;
            }
         }
         Assert.assertTrue(found);
      }
   }

   @Test
   public void testFacetOrigin() throws Exception
   {
      MockFaceted faceted = new MockFaceted();
      MockFacet facet = facetFactory.create(MockFacet.class, faceted);
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
}
