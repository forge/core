/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.maven.projects.MavenBuildSystem;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.mock.MockBuildSystem;
import org.jboss.forge.addon.projects.mock.MockProjectType;
import org.jboss.forge.addon.projects.ui.dependencies.AddDependenciesCommand;
import org.jboss.forge.addon.projects.ui.dependencies.AddManagedDependenciesCommand;
import org.jboss.forge.addon.projects.ui.dependencies.HasDependenciesCommand;
import org.jboss.forge.addon.projects.ui.dependencies.HasManagedDependenciesCommand;
import org.jboss.forge.addon.projects.ui.dependencies.RemoveDependenciesCommand;
import org.jboss.forge.addon.projects.ui.dependencies.RemoveManagedDependenciesCommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
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
public class ProjectDependencyCommandsTest
{
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
               .addClass(MockProjectType.class)
               .addClass(MockBuildSystem.class)
               .addAsServiceProvider(Service.class, ProjectDependencyCommandsTest.class, MockProjectType.class,
                        MockBuildSystem.class);

      return archive;
   }

   private MavenBuildSystem build;
   private ProjectFactory factory;
   private UITestHarness testHarness;
   private DependencyInstaller installer;

   @Before
   public void setUp()
   {
      build = SimpleContainer.getServices(getClass().getClassLoader(), MavenBuildSystem.class).get();
      factory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
      installer = SimpleContainer.getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
   }

   private static final String COORDINATES = "org.jboss.forge.addon:projects-api:";
   private static final String COORDINATES2 = "org.jboss.forge.addon:projects-impl:";
   private static final Dependency DEPENDENCY = DependencyBuilder.create(COORDINATES).setVersion("2.0.0.Final");
   private static final Dependency DEPENDENCY2 = DependencyBuilder.create(COORDINATES2).setVersion("2.0.0.Final");

   @Test
   public void testAddDependency() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(AddDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", COORDINATES);
            command.execute();
         }

         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testAddDependencies() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(AddDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", Arrays.asList(COORDINATES, COORDINATES2));
            command.execute();
         }

         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testAddDependencyWithDifferentScopeThenManaged() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         installer.installManaged(project, DEPENDENCY);

         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));

         try (CommandController command = testHarness.createCommandController(AddDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", COORDINATES + ":test");
            command.execute();
         }
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));

         Dependency dependency = project.getFacet(DependencyFacet.class).getDirectDependency(
                  DependencyBuilder.create(COORDINATES));
         Assert.assertEquals("test", dependency.getScopeType());
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testAddManagedDependency() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(AddManagedDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", COORDINATES);
            command.execute();
         }
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testAddManagedDependencies() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(AddManagedDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", Arrays.asList(COORDINATES, COORDINATES2));
            command.execute();
         }

         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testRemoveDependency() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         installer.install(project, DEPENDENCY);

         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(RemoveDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", COORDINATES);
            command.setValueFor("removeManaged", "true");
            command.execute();
         }

         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testRemoveDependencies() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         installer.install(project, DEPENDENCY);
         installer.install(project, DEPENDENCY2);

         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(RemoveDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", Arrays.asList(COORDINATES, COORDINATES2));
            command.setValueFor("removeManaged", "false");
            command.execute();
         }
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testRemoveManagedDependency() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         installer.install(project, DEPENDENCY);

         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(RemoveManagedDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", DEPENDENCY);
            command.setValueFor("removeUnmanaged", "true");
            command.execute();
         }
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testRemoveManagedDependencies() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         installer.install(project, DEPENDENCY);
         installer.install(project, DEPENDENCY2);

         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(RemoveManagedDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", Arrays.asList(DEPENDENCY, DEPENDENCY2));
            command.setValueFor("removeUnmanaged", "false");
            command.execute();
         }

         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testHasDependencyReturnSuccessOnFound() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         installer.install(project, DEPENDENCY);

         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));

         try (CommandController command = testHarness.createCommandController(HasDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", COORDINATES);
            Result result = command.execute();
            Assert.assertFalse(result instanceof Failed);
         }
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testHasDependencyReturnFailureOnMissing() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(HasDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", COORDINATES);
            Result result = command.execute();
            Assert.assertTrue(result instanceof Failed);
         }
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testHasDependencyReturnFailureOnSomeFound() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         installer.install(project, DEPENDENCY);

         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));

         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(HasDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", Arrays.asList(COORDINATES, COORDINATES2));
            Result result = command.execute();
            Assert.assertTrue(result instanceof Failed);
         }
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testHasManagedDependencyReturnSuccessOnFound() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         installer.installManaged(project, DEPENDENCY);

         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));

         try (CommandController command = testHarness.createCommandController(HasManagedDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", COORDINATES);
            Result result = command.execute();
            Assert.assertFalse(result instanceof Failed);
         }
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testHasManagedDependencyReturnFailureOnMissing() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));

         try (CommandController command = testHarness.createCommandController(HasManagedDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", COORDINATES);
            Result result = command.execute();
            Assert.assertTrue(result instanceof Failed);
         }
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }

   @Test
   public void testHasManagedDependencyReturnFailureOnSomeFound() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         installer.installManaged(project, DEPENDENCY);

         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY2));
         Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY2));

         try (CommandController command = testHarness.createCommandController(HasManagedDependenciesCommand.class,
                  project.getRoot()))
         {
            command.initialize();
            command.setValueFor("arguments", Arrays.asList(COORDINATES, COORDINATES2));
            Result result = command.execute();
            Assert.assertTrue(result instanceof Failed);
         }
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }
}