/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.facets;

import static org.jboss.forge.addon.dependencies.util.Dependencies.areEquivalent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.manager.maven.MavenContainer;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for {@link MavenDependencyFacet}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class MavenDependencyFacetTest
{

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:resources"),
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven")
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

   private static String previousUserSettings;
   private static String previousLocalRepository;

   @Before
   public void setUp()
   {
      project = projectFactory.createTempProject();
   }

   @BeforeClass
   public static void setRemoteRepository() throws IOException
   {
      previousUserSettings = System.setProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION,
               getAbsolutePath("profiles/settings.xml"));
      previousLocalRepository = System.setProperty(MavenContainer.ALT_LOCAL_REPOSITORY_LOCATION,
               "target/the-other-repository");
   }

   private static String getAbsolutePath(String path) throws FileNotFoundException
   {
      URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
      if (resource == null)
         throw new FileNotFoundException(path);
      return resource.getFile();
   }

   @AfterClass
   public static void clearRemoteRepository()
   {
      if (previousUserSettings == null)
      {
         System.clearProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION);
      }
      else
      {
         System.setProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION, previousUserSettings);
      }
      if (previousLocalRepository == null)
      {
         System.clearProperty(MavenContainer.ALT_LOCAL_REPOSITORY_LOCATION);
      }
      else
      {
         System.setProperty(MavenContainer.ALT_LOCAL_REPOSITORY_LOCATION, previousUserSettings);
      }
   }

   @Test
   public void testAddDirectDependencyOrder() throws Exception
   {
      Assert.assertTrue("DependencyFacet not installed in project", project.hasFacet(DependencyFacet.class));
      final DependencyFacet facet = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependencyOne = DependencyBuilder.create("groupId:artifactId:1.0.0.Final");
      DependencyBuilder dependencyTwo = DependencyBuilder.create("anotherGroupId:anotherArtifactId:1.0.0.Final");
      DependencyBuilder dependencyOneV2 = DependencyBuilder.create("groupId:artifactId:2.0.0.Final");
      facet.addDirectDependency(dependencyOne);
      facet.addDirectDependency(dependencyTwo);
      facet.addDirectDependency(dependencyOneV2);
      List<Dependency> dependencies = facet.getDependencies();
      Assert.assertEquals(2, dependencies.size());
      assertDependencies(dependencyOneV2, dependencies.get(0));
      assertDependencies(dependencyTwo, dependencies.get(1));
   }

   @Test
   public void testAddManagedDependencyOrder() throws Exception
   {
      Assert.assertTrue("DependencyFacet not installed in project", project.hasFacet(DependencyFacet.class));
      final DependencyFacet facet = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependencyOne = DependencyBuilder.create("groupId:artifactId:1.0.0.Final");
      DependencyBuilder dependencyTwo = DependencyBuilder.create("anotherGroupId:anotherArtifactId:1.0.0.Final");
      DependencyBuilder dependencyOneV2 = DependencyBuilder.create("groupId:artifactId:2.0.0.Final");
      facet.addManagedDependency(dependencyOne);
      facet.addManagedDependency(dependencyTwo);
      facet.addManagedDependency(dependencyOneV2);
      List<Dependency> dependencies = facet.getManagedDependencies();
      Assert.assertEquals(2, dependencies.size());
      assertDependencies(dependencyOne, dependencies.get(0));
      assertDependencies(dependencyTwo, dependencies.get(1));
   }

   @Test
   public void testAddDirectManagedDependencyOrder() throws Exception
   {
      Assert.assertTrue("DependencyFacet not installed in project", project.hasFacet(DependencyFacet.class));
      final DependencyFacet facet = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependencyOne = DependencyBuilder.create("groupId:artifactId:1.0.0.Final");
      DependencyBuilder dependencyTwo = DependencyBuilder.create("anotherGroupId:anotherArtifactId:1.0.0.Final");
      DependencyBuilder dependencyOneV2 = DependencyBuilder.create("groupId:artifactId:2.0.0.Final");
      facet.addDirectManagedDependency(dependencyOne);
      facet.addDirectManagedDependency(dependencyTwo);
      facet.addDirectManagedDependency(dependencyOneV2);
      List<Dependency> dependencies = facet.getManagedDependencies();
      Assert.assertEquals(2, dependencies.size());
      assertDependencies(dependencyOneV2, dependencies.get(0));
      assertDependencies(dependencyTwo, dependencies.get(1));
   }

   @Test
   public void testResolveAvailableVersions() throws Exception
   {
      final DependencyFacet facet = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependency = DependencyBuilder.create("test:no_dep:::pom");
      List<Coordinate> versions = facet.resolveAvailableVersions(dependency);
      Assert.assertEquals(6, versions.size());
   }

   @Test
   public void testDifferentDependencyType() throws Exception
   {
      Assert.assertTrue("DependencyFacet not installed in project", project.hasFacet(DependencyFacet.class));
      final DependencyFacet facet = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependencyOne = DependencyBuilder.create("org.jboss.errai:errai-cdi-client")
               .setPackaging("jar").setScopeType("provided");
      DependencyBuilder dependencyTwo = DependencyBuilder.create("org.jboss.errai:errai-cdi-client")
               .setPackaging("test-jar").setScopeType("test");
      facet.addDirectDependency(dependencyOne);
      facet.addDirectDependency(dependencyTwo);
      List<Dependency> dependencies = facet.getDependencies();
      Assert.assertEquals(2, dependencies.size());
      assertDependencies(dependencyOne, dependencies.get(0));
      assertDependencies(dependencyTwo, dependencies.get(1));
   }

   private void assertDependencies(Dependency expected, Dependency actual)
   {
      Assert.assertTrue("Dependencies are not equivalent", areEquivalent(expected, actual));
      Assert.assertEquals("Dependencies version do not match", expected.getCoordinate().getVersion(), actual
               .getCoordinate().getVersion());
   }
}
