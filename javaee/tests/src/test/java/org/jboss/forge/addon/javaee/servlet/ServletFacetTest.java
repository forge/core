/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.servlet;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetIsAmbiguousException;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ServletFacetTest
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

   @Test(expected = FacetIsAmbiguousException.class)
   public void testAmbiguousFacet() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ServletFacet.class);
   }

   @Test
   public void testWebXMLInitialInfo_3_1() throws Exception
   {
      Project project = projectFactory.createTempProject();
      ServletFacet_3_1 facet = facetFactory.install(project, ServletFacet_3_1.class);
      Assert.assertFalse(facet.getConfigFile().exists());
      Assert.assertNotNull(facet);
      Assert.assertTrue(project.hasFacet(ServletFacet.class));
      org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor config = facet.getConfig();
      Assert.assertNotNull(config);
      String projectName = project.getFacet(MetadataFacet.class).getProjectName();
      Assert.assertFalse(config.getAllDisplayName().isEmpty());
      Assert.assertEquals(projectName, config.getAllDisplayName().get(0));
      Assert.assertFalse(facet.getConfigFile().exists());
      Assert.assertTrue(project.hasFacet(ServletFacet.class));
      Assert.assertNotNull(project.getFacet(ServletFacet.class));
   }

   @Test
   public void testWebXMLInitialInfo_3_0() throws Exception
   {
      Project project = projectFactory.createTempProject();
      ServletFacet_3_0 facet = facetFactory.install(project, ServletFacet_3_0.class);
      Assert.assertFalse(facet.getConfigFile().exists());
      Assert.assertNotNull(facet);
      Assert.assertTrue(project.hasFacet(ServletFacet.class));
      WebAppDescriptor config = facet.getConfig();
      Assert.assertNotNull(config);
      String projectName = project.getFacet(MetadataFacet.class).getProjectName();
      Assert.assertFalse(config.getAllDisplayName().isEmpty());
      Assert.assertEquals(projectName, config.getAllDisplayName().get(0));
      Assert.assertFalse(facet.getConfigFile().exists());
      Assert.assertTrue(project.hasFacet(ServletFacet.class));
      Assert.assertNotNull(project.getFacet(ServletFacet.class));
   }

   @Test
   public void testWebXMLInitialInfo_2_5() throws Exception
   {
      Project project = projectFactory.createTempProject();
      ServletFacet_2_5 facet = facetFactory.install(project, ServletFacet_2_5.class);
      Assert.assertTrue(facet.getConfigFile().exists());
      Assert.assertNotNull(facet);
      Assert.assertTrue(project.hasFacet(ServletFacet.class));
      org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor config = facet.getConfig();
      Assert.assertNotNull(config);
      String projectName = project.getFacet(MetadataFacet.class).getProjectName();
      Assert.assertFalse(config.getAllDisplayName().isEmpty());
      Assert.assertEquals(projectName, config.getAllDisplayName().get(0));
      Assert.assertTrue(project.hasFacet(ServletFacet.class));
      Assert.assertNotNull(project.getFacet(ServletFacet.class));
   }

}