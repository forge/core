/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
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
      FacesFacet_2_0 facet = facetFactory.install(project, FacesFacet_2_0.class);
      Assert.assertNotNull(facet);
      FileResource<?> configFile = facet.getConfigFile();
      Assert.assertFalse(configFile.exists());
      org.jboss.shrinkwrap.descriptor.api.facesconfig20.WebFacesConfigDescriptor config = facet.getConfig();
      config.createApplication().defaultRenderKitId("foo");
      Assert.assertFalse(configFile.exists());
      facet.saveConfig(config);
      Assert.assertTrue(configFile.exists());
      Assert.assertTrue(project.hasFacet(FacesFacet.class));
   }

   @Test
   public void testConfigDescriptorCreation_2_1() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      FacesFacet_2_1 facet = facetFactory.install(project, FacesFacet_2_1.class);
      Assert.assertNotNull(facet);
      FileResource<?> configFile = facet.getConfigFile();
      Assert.assertFalse(configFile.exists());
      WebFacesConfigDescriptor config = facet.getConfig();
      config.createApplication().defaultRenderKitId("foo");
      Assert.assertFalse(configFile.exists());
      facet.saveConfig(config);
      Assert.assertTrue(configFile.exists());
      Assert.assertTrue(project.hasFacet(FacesFacet.class));
   }

   @Test
   public void testConfigDescriptorCreation_2_2() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertNotNull(project);
      FacesFacet_2_2 facet = facetFactory.install(project, FacesFacet_2_2.class);
      Assert.assertNotNull(facet);
      FileResource<?> configFile = facet.getConfigFile();
      Assert.assertFalse(configFile.exists());
      org.jboss.shrinkwrap.descriptor.api.facesconfig22.WebFacesConfigDescriptor config = facet.getConfig();
      config.createApplication().defaultRenderKitId("foo");
      Assert.assertFalse(configFile.exists());
      facet.saveConfig(config);
      Assert.assertTrue(configFile.exists());
      Assert.assertTrue(project.hasFacet(FacesFacet.class));
   }

}
