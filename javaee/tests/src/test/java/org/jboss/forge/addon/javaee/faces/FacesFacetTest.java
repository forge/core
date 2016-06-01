/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetIsAmbiguousException;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.descriptor.api.facesconfig21.WebFacesConfigDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class FacesFacetTest
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
   public void testCannotInstallAmbiguousFacetType() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      facetFactory.install(project, FacesFacet.class);
   }

   @Test
   public void testConfigDescriptorCreation_2_0() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      packaging.setPackagingType("war");
      FacesFacet_2_0 facet = facetFactory.install(project, FacesFacet_2_0.class);
      Assert.assertNotNull(facet);
      FileResource<?> configFile = facet.getConfigFile();
      Assert.assertTrue(configFile.exists());
      org.jboss.shrinkwrap.descriptor.api.facesconfig20.WebFacesConfigDescriptor config = facet.getConfig();
      config.createApplication().defaultRenderKitId("foo");
      Assert.assertTrue(configFile.exists());
      facet.saveConfig(config);
      Assert.assertTrue(configFile.exists());
      Assert.assertEquals("2.0", facet.getSpecVersion().toString());
      Assert.assertTrue(project.hasFacet(FacesFacet.class));
   }

   @Test
   public void testConfigDescriptorCreation_2_1() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      packaging.setPackagingType("war");
      FacesFacet_2_1 facet = facetFactory.install(project, FacesFacet_2_1.class);
      Assert.assertNotNull(facet);
      FileResource<?> configFile = facet.getConfigFile();
      Assert.assertTrue(configFile.exists());
      WebFacesConfigDescriptor config = facet.getConfig();
      config.createApplication().defaultRenderKitId("foo");
      Assert.assertTrue(configFile.exists());
      facet.saveConfig(config);
      Assert.assertTrue(configFile.exists());
      Assert.assertEquals("2.1", facet.getSpecVersion().toString());
      Assert.assertTrue(project.hasFacet(FacesFacet.class));
   }

   @Test
   public void testConfigDescriptorCreation_2_2() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      packaging.setPackagingType("war");
      FacesFacet_2_2 facet = facetFactory.install(project, FacesFacet_2_2.class);
      Assert.assertNotNull(facet);
      FileResource<?> configFile = facet.getConfigFile();
      Assert.assertTrue(configFile.exists());
      org.jboss.shrinkwrap.descriptor.api.facesconfig22.WebFacesConfigDescriptor config = facet.getConfig();
      config.createApplication().defaultRenderKitId("foo");
      Assert.assertTrue(configFile.exists());
      facet.saveConfig(config);
      Assert.assertTrue(configFile.exists());
      Assert.assertEquals("2.2", facet.getSpecVersion().toString());
      Assert.assertTrue(project.hasFacet(FacesFacet.class));
   }

   @Test
   public void testComponentLibraryConfigDescriptorCreation() throws Exception
   {
      // Create a temporary project of type JAR that acts as a component library instead of a web app and verify if
      // faces-config.xml is created on facet installation.

      // Faces 2.0
      Project faces20Project = projectFactory.createTempProject();
      Assert.assertNotNull(faces20Project);
      FacesFacet_2_0 faces20Facet = facetFactory.install(faces20Project, FacesFacet_2_0.class);
      Assert.assertNotNull(faces20Facet);
      FileResource<?> configFile = faces20Facet.getConfigFile();
      Assert.assertFalse(configFile.exists());

      // Faces 2.1
      Project faces21Project = projectFactory.createTempProject();
      Assert.assertNotNull(faces21Project);
      FacesFacet_2_1 faces21Facet = facetFactory.install(faces21Project, FacesFacet_2_1.class);
      Assert.assertNotNull(faces21Facet);
      configFile = faces21Facet.getConfigFile();
      Assert.assertFalse(configFile.exists());

      // Faces 2.2
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      FacesFacet_2_2 facet = facetFactory.install(project, FacesFacet_2_2.class);
      Assert.assertNotNull(facet);
      configFile = facet.getConfigFile();
      Assert.assertFalse(configFile.exists());
   }

   @Test
   public void testFacesFacetInstalledAfterProjectIsEvictedFromCache()
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, FacesFacet_2_2.class);
      Assert.assertTrue("Should have FacesFacet", project.hasFacet(FacesFacet.class));
      Assert.assertTrue("Should have FacesFacet_2_2", project.hasFacet(FacesFacet_2_2.class));
      projectFactory.invalidateCaches();
      project = projectFactory.findProject(project.getRoot());
      Assert.assertTrue("Should have FacesFacet", project.hasFacet(FacesFacet.class));
      Assert.assertTrue("Should have FacesFacet_2_2", project.hasFacet(FacesFacet_2_2.class));

   }

}
