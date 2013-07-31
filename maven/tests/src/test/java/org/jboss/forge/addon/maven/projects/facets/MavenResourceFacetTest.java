/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.resources.MavenPomResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
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
public class MavenResourceFacetTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:projects", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.addon:maven", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace:container-cdi", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects", "2.0.0-SNAPSHOT")
               );

      return archive;
   }

   private Project project;

   @Inject
   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
      project = projectFactory.createTempProject();
   }

   @Test
   public void testHasFacet() throws Exception
   {
      Assert.assertTrue("ResourcesFacet not installed in project", project.hasFacet(ResourcesFacet.class));
   }

   @Test
   public void testResourceFolderNotNull() throws Exception
   {
      final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      Assert.assertNotNull("Resource folder is null", facet.getResourceFolder());
   }

   @Test
   public void testDefaultResourceFolder() throws Exception
   {
      MavenResourceFacet facet = project.getFacet(MavenResourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "src" + File.separator + "main" + File.separator + "resources");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getResourceFolder().getFullyQualifiedName());
   }

   @Test
   public void testDefaultTestResourceFolder() throws Exception
   {
      MavenResourceFacet facet = project.getFacet(MavenResourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "src" + File.separator + "test" + File.separator + "resources");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getTestResourceFolder().getFullyQualifiedName());
   }

   @Test
   public void testCustomResourceFolder() throws Exception
   {
      MavenPomResource pom = project.getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);

      pom.setContents("<project><modelVersion>4.0.0.</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><resources><resource><directory>foo</directory></resource></resources></build></project>");

      MavenResourceFacet facet = project.getFacet(MavenResourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "foo");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getResourceFolder().getFullyQualifiedName());
   }

   @Test
   public void testCustomTestSourceFolder() throws Exception
   {
      MavenPomResource pom = project.getProjectRoot().getChild("pom.xml").reify(MavenPomResource.class);

      pom.setContents("<project><modelVersion>4.0.0.</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><testResources><testResource><directory>foo</directory></testResource></testResources></build></project>");

      MavenResourceFacet facet = project.getFacet(MavenResourceFacet.class);
      DirectoryResource expected = project.getProjectRoot().getChildDirectory(
               "foo");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getTestResourceFolder().getFullyQualifiedName());
   }

}
