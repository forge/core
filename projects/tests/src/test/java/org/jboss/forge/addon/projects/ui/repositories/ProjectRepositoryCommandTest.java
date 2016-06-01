/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui.repositories;

import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.maven.projects.MavenBuildSystem;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ProjectRepositoryCommandTest
{

   //
   //
   // @Deployment
   // @AddonDeployments({
   // @AddonDeployment(name = "org.jboss.forge.addon:projects"),
   // @AddonDeployment(name = "org.jboss.forge.addon:maven"),
   // @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness")
   // })
   // public static AddonArchive getDeployment()
   // {
   // AddonArchive archive = ShrinkWrap
   // .create(AddonArchive.class)
   // .addBeansXML()
   // .addAsAddonDependencies(
   // AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
   // AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
   // AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
   // AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
   // );
   //
   // return archive;
   // }

   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addAsServiceProvider(Service.class, ProjectRepositoryCommandTest.class);

      return archive;
   }

   private MavenBuildSystem build;
   private ProjectFactory factory;
   private UITestHarness testHarness;
   private Project project;

   @Before
   public void setUp()
   {
      build = SimpleContainer.getServices(getClass().getClassLoader(), MavenBuildSystem.class).get();
      factory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
      project = factory.createTempProject(build);
   }

   @After
   public void tearDown()
   {
      project.getRoot().delete(true);
      project = null;
   }

   @Test
   public void testAddRepository() throws Exception
   {
      try (CommandController c = testHarness
               .createCommandController(AddRepositoryCommand.class, project.getRoot()))
      {
         c.initialize();
         Assert.assertFalse(c.isValid());
         c.setValueFor("named", "my-repo");
         c.setValueFor("url", "http://my-url.com");
         Assert.assertTrue(c.isValid());
         Result result = c.execute();
         Assert.assertNotNull(result);
      }
      List<DependencyRepository> repositories = project.getFacet(DependencyFacet.class).getRepositories();
      Assert.assertEquals(1, repositories.size());
      Assert.assertEquals("my-repo", repositories.get(0).getId());
      Assert.assertEquals("http://my-url.com", repositories.get(0).getUrl());
   }

   @Test
   public void testRemoveRepository() throws Exception
   {
      project.getFacet(DependencyFacet.class).addRepository("repo", "http://my-repo.com");
      try (CommandController c = testHarness
               .createCommandController(RemoveRepositoryCommand.class, project.getRoot()))
      {
         c.initialize();
         Assert.assertFalse(c.isValid());
         c.setValueFor("url", "http://my-repo.com");
         Assert.assertTrue(c.isValid());
         Result result = c.execute();
         Assert.assertNotNull(result);
      }

      List<DependencyRepository> repositories = project.getFacet(DependencyFacet.class).getRepositories();
      Assert.assertTrue(repositories.isEmpty());
   }

}
