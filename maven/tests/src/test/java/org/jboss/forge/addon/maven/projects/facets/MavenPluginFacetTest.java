/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.addon.maven.profiles.ProfileBuilder;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
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

/**
 * Tests for {@link MavenPluginFacet}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class MavenPluginFacetTest
{
   private static final String REPOSITORY_ID = "repository_id";
   private static final String REPOSITORY_URL = "https://forge.jboss.org";
   private static final org.jboss.forge.addon.maven.profiles.Profile TEST_PROFILE_ID = ProfileBuilder.create().setId(
            "test_profile");

   private static final Coordinate PLUGIN_COORDINATE = CoordinateBuilder.create()
            .setGroupId("org.testplugin")
            .setArtifactId("testplugin")
            .setVersion("1.0.0.Final");

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
               .addAsServiceProvider(Service.class, MavenPluginFacetTest.class);

      return archive;
   }

   private Project project;
   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      project = projectFactory.createTempProject();
   }

   @Test
   public void testHasMavenPluginFacet()
   {
      Assert.assertTrue(project.hasFacet(MavenPluginFacet.class));
   }

   @Test
   public void testAddMavenPlugin()
   {
      MavenPluginFacet facet = project.getFacet(MavenPluginFacet.class);
      MavenPluginBuilder plugin = MavenPluginBuilder
               .create()
               .setCoordinate(PLUGIN_COORDINATE);
      // SUT
      facet.addPlugin(plugin);
      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      Model model = mavenFacet.getModel();
      List<Plugin> plugins = model.getBuild().getPlugins();
      Assert.assertEquals(1, plugins.size());
      assertCoordinateMatch(PLUGIN_COORDINATE, plugins.get(0));
      Assert.assertTrue(model.getProfiles().isEmpty());
   }

   @Test
   public void testAddMavenPluginProfile()
   {
      MavenPluginFacet facet = project.getFacet(MavenPluginFacet.class);
      MavenPluginBuilder plugin = MavenPluginBuilder
               .create()
               .setCoordinate(PLUGIN_COORDINATE);

      // SUT
      facet.addPlugin(plugin, TEST_PROFILE_ID);

      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      Model model = mavenFacet.getModel();
      Assert.assertNull(model.getBuild());
      List<Profile> profiles = model.getProfiles();
      Assert.assertEquals(1, profiles.size());
      Profile profile = profiles.get(0);
      assertProfileMatch(TEST_PROFILE_ID, profile);
      assertCoordinateMatch(PLUGIN_COORDINATE, profile.getBuild().getPlugins().get(0));
   }

   @Test
   public void testAddManagedMavenPlugin()
   {
      MavenPluginFacet facet = project.getFacet(MavenPluginFacet.class);
      MavenPluginBuilder plugin = MavenPluginBuilder
               .create()
               .setCoordinate(PLUGIN_COORDINATE);

      // SUT
      facet.addManagedPlugin(plugin);

      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      Model model = mavenFacet.getModel();
      List<Plugin> plugins = model.getBuild().getPlugins();
      Assert.assertTrue(model.getProfiles().isEmpty());
      Assert.assertEquals(0, plugins.size());
      plugins = model.getBuild().getPluginManagement().getPlugins();
      Assert.assertEquals(1, plugins.size());
      assertCoordinateMatch(PLUGIN_COORDINATE, plugins.get(0));
   }

   @Test
   public void testAddManagedMavenPluginProfile()
   {
      MavenPluginFacet facet = project.getFacet(MavenPluginFacet.class);
      MavenPluginBuilder plugin = MavenPluginBuilder
               .create()
               .setCoordinate(PLUGIN_COORDINATE);
      // SUT
      facet.addManagedPlugin(plugin, TEST_PROFILE_ID);

      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      Model model = mavenFacet.getModel();
      Assert.assertNull(model.getBuild());
      List<Profile> profiles = model.getProfiles();
      Assert.assertEquals(1, profiles.size());
      Profile profile = profiles.get(0);
      assertProfileMatch(TEST_PROFILE_ID, profile);
      Assert.assertTrue(profile.getBuild().getPlugins().isEmpty());
      assertCoordinateMatch(PLUGIN_COORDINATE, profile.getBuild().getPluginManagement().getPlugins().get(0));
   }

   @Test
   public void testAddPluginRepository()
   {
      MavenPluginFacet facet = project.getFacet(MavenPluginFacet.class);
      // SUT
      facet.addPluginRepository(REPOSITORY_ID, REPOSITORY_URL);
      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      Model model = mavenFacet.getModel();
      Assert.assertEquals(1, model.getPluginRepositories().size());
      Assert.assertTrue(model.getProfiles().isEmpty());
      assertRepositoryMatch(REPOSITORY_ID, REPOSITORY_URL, model.getPluginRepositories().get(0));
   }

   @Test
   public void testAddPluginRepositoryProfile()
   {
      MavenPluginFacet facet = project.getFacet(MavenPluginFacet.class);
      // SUT
      facet.addPluginRepository(REPOSITORY_ID, REPOSITORY_URL, TEST_PROFILE_ID);
      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      Model model = mavenFacet.getModel();
      Assert.assertEquals(0, model.getPluginRepositories().size());
      Assert.assertEquals(1, model.getProfiles().size());
      assertRepositoryMatch(REPOSITORY_ID, REPOSITORY_URL, model.getProfiles().get(0).getPluginRepositories().get(0));
   }

   private void assertCoordinateMatch(Coordinate coordinate, Plugin plugin)
   {
      Assert.assertEquals(coordinate.getGroupId(), plugin.getGroupId());
      Assert.assertEquals(coordinate.getArtifactId(), plugin.getArtifactId());
      Assert.assertEquals(coordinate.getVersion(), plugin.getVersion());
   }

   private void assertProfileMatch(org.jboss.forge.addon.maven.profiles.Profile profile1, Profile profile2)
   {
      Assert.assertEquals(profile1.getId(), profile2.getId());
   }

   private void assertRepositoryMatch(String id, String url, Repository repository)
   {
      Assert.assertEquals(id, repository.getId());
      Assert.assertEquals(url, repository.getUrl());

   }

}
