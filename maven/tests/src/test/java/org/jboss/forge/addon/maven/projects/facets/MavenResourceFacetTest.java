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
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MavenResourceFacetTest
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
               .addAsServiceProvider(Service.class, MavenResourceFacetTest.class);

      return archive;
   }

   private Project project;
   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      project = projectFactory.createTempProject(Arrays.<Class<? extends ProjectFacet>> asList(ResourcesFacet.class));
   }

   @Test
   public void testHasFacet() throws Exception
   {
      Assert.assertTrue("ResourcesFacet not installed in project", project.hasFacet(ResourcesFacet.class));
   }

   @Test
   public void testResourceDirectoryNotNull() throws Exception
   {
      final ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      Assert.assertNotNull("Resource Directory is null", facet.getResourceDirectory());
   }

   @Test
   public void testDefaultResourceDirectory() throws Exception
   {
      ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      DirectoryResource expected = project.getRoot().reify(DirectoryResource.class).getChildDirectory(
               "src" + File.separator + "main" + File.separator + "resources");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getResourceDirectory().getFullyQualifiedName());
   }

   @Test
   public void testDefaultTestResourceDirectory() throws Exception
   {
      ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      DirectoryResource expected = project.getRoot().reify(DirectoryResource.class).getChildDirectory(
               "src" + File.separator + "test" + File.separator + "resources");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getTestResourceDirectory().getFullyQualifiedName());
   }

   @Test
   public void testCustomResourceDirectory() throws Exception
   {
      MavenModelResource pom = project.getRoot().reify(DirectoryResource.class).getChild("pom.xml")
               .reify(MavenModelResource.class);

      pom.setContents(
               "<project><modelVersion>4.0.0</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><resources><resource><directory>foo</directory></resource></resources></build></project>");

      ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      DirectoryResource expected = project.getRoot().reify(DirectoryResource.class).getChildDirectory(
               "foo");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getResourceDirectory().getFullyQualifiedName());
   }

   @Test
   @Ignore("https://issues.jboss.org/browse/FORGE-1218")
   public void testCustomResourceDirectoryWithProperty() throws Exception
   {
      MavenModelResource pom = project.getRoot().reify(DirectoryResource.class).getChild("pom.xml")
               .reify(MavenModelResource.class);

      pom.setContents(
               "<project><modelVersion>4.0.0</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><resources><resource><directory>${project.basedir}"
                        + File.separator + "foo</directory></resource></resources></build></project>");

      ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      DirectoryResource expected = project.getRoot().reify(DirectoryResource.class).getChildDirectory(
               "foo");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getResourceDirectory().getFullyQualifiedName());
   }

   @Test
   public void testCustomTestSourceDirectory() throws Exception
   {
      MavenModelResource pom = project.getRoot().reify(DirectoryResource.class).getChild("pom.xml")
               .reify(MavenModelResource.class);

      pom.setContents(
               "<project><modelVersion>4.0.0</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><testResources><testResource><directory>foo</directory></testResource></testResources></build></project>");

      ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      DirectoryResource expected = project.getRoot().reify(DirectoryResource.class).getChildDirectory(
               "foo");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getTestResourceDirectory().getFullyQualifiedName());
   }

   @Test
   @Ignore("https://issues.jboss.org/browse/FORGE-1218")
   public void testCustomTestSourceDirectoryWithProperty() throws Exception
   {
      MavenModelResource pom = project.getRoot().reify(DirectoryResource.class).getChild("pom.xml")
               .reify(MavenModelResource.class);

      pom.setContents(
               "<project><modelVersion>4.0.0</modelVersion><groupId>com.test</groupId><artifactId>testme</artifactId><version>1.0</version><build><testResources><testResource><directory>${project.basedir}"
                        + File.separator + "foo</directory></testResource></testResources></build></project>");

      ResourcesFacet facet = project.getFacet(ResourcesFacet.class);
      DirectoryResource expected = project.getRoot().reify(DirectoryResource.class).getChildDirectory(
               "foo");
      Assert.assertEquals(expected.getFullyQualifiedName(), facet.getTestResourceDirectory().getFullyQualifiedName());
   }

}
