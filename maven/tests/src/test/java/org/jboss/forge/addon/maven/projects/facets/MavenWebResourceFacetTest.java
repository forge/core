/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;
import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.resources.MavenPomResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenWebResourceFacetTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects")
               );

      return archive;
   }

   private Project project;

   @Inject
   private ProjectFactory projectFactory;

   @Before
   @SuppressWarnings("unchecked")
   public void setUp()
   {
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
      WebResourcesFacet facet = project.getFacet(MavenWebResourcesFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "src" + File.separator + "main" + File.separator + "webapp");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getWebRootDirectory().getFullyQualifiedName());
   }

   @Test
   public void testCustomWebResourceFolder() throws Exception
   {
      MavenPomResource pom = project.getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);

      pom.setContents("<project><modelVersion>4.0.0</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><plugins><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-war-plugin</artifactId><version>2.1-beta-1</version><configuration>"
               + "<warSourceDirectory>foo</warSourceDirectory>"
               + "</configuration></plugin></plugins></build></project>");

      WebResourcesFacet facet = project.getFacet(MavenWebResourcesFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory("foo");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getWebRootDirectory().getFullyQualifiedName());
   }
}
