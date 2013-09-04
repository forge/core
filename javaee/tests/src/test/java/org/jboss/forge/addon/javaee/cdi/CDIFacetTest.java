/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CDIFacetTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee")
               );
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Test(expected = AmbiguousResolutionException.class)
   public void testCannotInstallAmbiguousFacetType() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      facetFactory.install(project, CDIFacet.class);
      Assert.fail("Should not have been able to install ambiguous Facet.");
   }

   @Test
   public void testBeansXMLCreatedWhenInstalled_1_0() throws Exception
   {
      Project project = projectFactory.createTempProject();
      CDIFacet cdiFacet = facetFactory.install(project, CDIFacet_1_0.class);
      assertNotNull(cdiFacet);
      assertTrue(project.hasFacet(CDIFacet.class));
      assertTrue(project.hasFacet(CDIFacet_1_0.class));
      BeansDescriptor config = project.getFacet(CDIFacet.class).getConfig();
      assertNotNull(config);
   }

   @Test
   public void testBeansXMLCreatedWhenInstalled_1_1() throws Exception
   {
      Project project = projectFactory.createTempProject();
      CDIFacet cdiFacet = facetFactory.install(project, CDIFacet_1_1.class);
      assertNotNull(cdiFacet);
      assertTrue(project.hasFacet(CDIFacet.class));
      assertTrue(project.hasFacet(CDIFacet_1_1.class));
      BeansDescriptor config = project.getFacet(CDIFacet.class).getConfig();
      assertNotNull(config);
   }

}