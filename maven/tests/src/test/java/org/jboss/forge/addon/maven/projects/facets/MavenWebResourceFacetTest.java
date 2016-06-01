/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;
import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenWebResourceFacetTest
{

   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addAsServiceProvider(Service.class, MavenWebResourceFacetTest.class);

      return archive;
   }

   private Project project;
   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      project = projectFactory
               .createTempProject(Arrays.<Class<? extends ProjectFacet>> asList(WebResourcesFacet.class));
   }

   @Test
   public void testHasFacet() throws Exception
   {
      Assert.assertTrue("WebResourcesFacet not installed in project.", project.hasFacet(WebResourcesFacet.class));
   }

   @Test
   public void testPackagingTypeIsWar() throws Exception
   {
      Assert.assertEquals("WebResourcesFacet not installed in project.", "war", project.getFacet(PackagingFacet.class)
               .getPackagingType());
   }

   @Test
   public void testWebResourceFolderNotNull() throws Exception
   {
      final WebResourcesFacet facet = project.getFacet(WebResourcesFacet.class);
      Assert.assertNotNull("Resource folder is null", facet.getWebRootDirectory());
   }

   @Test
   public void testDefaultWebResourceFolder() throws Exception
   {
      WebResourcesFacet facet = project.getFacet(WebResourcesFacet.class);
      DirectoryResource expected = project.getRoot().reify(DirectoryResource.class).getChildDirectory(
               "src" + File.separator + "main" + File.separator + "webapp");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getWebRootDirectory().getFullyQualifiedName());
   }

   @Test
   public void testCustomWebResourceFolder() throws Exception
   {
      MavenModelResource pom = project.getRoot().reify(DirectoryResource.class).getChild("pom.xml")
               .reify(MavenModelResource.class);

      pom.setContents(
               "<project><modelVersion>4.0.0</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><plugins><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-war-plugin</artifactId><version>2.1-beta-1</version><configuration>"
                        + "<warSourceDirectory>foo</warSourceDirectory>"
                        + "</configuration></plugin></plugins></build></project>");

      WebResourcesFacet facet = project.getFacet(WebResourcesFacet.class);
      DirectoryResource expected = project.getRoot().reify(DirectoryResource.class).getChildDirectory("foo");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getWebRootDirectory().getFullyQualifiedName());
   }
}
