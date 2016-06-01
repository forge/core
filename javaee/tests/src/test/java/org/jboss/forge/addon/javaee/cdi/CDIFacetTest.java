/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetIsAmbiguousException;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CDIFacetTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectFactory.createTempProject();
   }

   @Test(expected = FacetIsAmbiguousException.class)
   public void testCannotInstallAmbiguousFacetType() throws Exception
   {
      Assert.assertNotNull(project);
      facetFactory.install(project, CDIFacet.class);
      Assert.fail("Should not have been able to install ambiguous Facet.");
   }

   @Test
   public void testBeansXMLCreatedWhenInstalled_1_0() throws Exception
   {
      CDIFacet<?> cdiFacet = facetFactory.install(project, CDIFacet_1_0.class);
      assertNotNull(cdiFacet);
      assertTrue(project.hasFacet(CDIFacet.class));
      assertTrue(project.hasFacet(CDIFacet_1_0.class));
      BeansDescriptor config = (BeansDescriptor) project.getFacet(CDIFacet.class).getConfig();
      assertNotNull(config);
   }

   @Test
   public void testBeansXMLCreatedWhenInstalled_1_1() throws Exception
   {
      CDIFacet<?> cdiFacet = facetFactory.install(project, CDIFacet_1_1.class);
      assertNotNull(cdiFacet);
      assertTrue(project.hasFacet(CDIFacet.class));
      assertTrue(project.hasFacet(CDIFacet_1_1.class));
      org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor config = (org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor) project
               .getFacet(CDIFacet.class).getConfig();
      assertNotNull(config);
   }

   @Test
   public void testCDIFacet1_1OnlyInProject() throws Exception
   {
      facetFactory.install(project, CDIFacet_1_1.class);
      projectFactory.invalidateCaches();
      project = projectFactory.findProject(project.getRoot());
      Assert.assertTrue(project.hasFacet(CDIFacet_1_1.class));
      Assert.assertFalse(project.hasFacet(CDIFacet_1_0.class));
   }

   @Test
   public void testCDIFacet1_0OnlyInProject() throws Exception
   {
      facetFactory.install(project, CDIFacet_1_0.class);
      projectFactory.invalidateCaches();
      project = projectFactory.findProject(project.getRoot());
      Assert.assertTrue(project.hasFacet(CDIFacet_1_0.class));
      Assert.assertFalse(project.hasFacet(CDIFacet_1_1.class));
   }

}