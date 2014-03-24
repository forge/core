package org.jboss.forge.addon.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.util.Arrays;

import javax.inject.Inject;

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
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProjectDependencyCommandsTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClass(MockProjectType.class)
               .addClass(MockBuildSystem.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );

      return archive;
   }

   @Inject
   private MavenBuildSystem build;

   @Inject
   private ProjectFactory factory;

   @Inject
   private UITestHarness testHarness;

   @Inject
   private DependencyInstaller installer;

   private static final String COORDINATES = "org.jboss.forge.addon:projects-api:2.0.0.Final";
   private static final String COORDINATES2 = "org.jboss.forge.addon:projects-impl:2.0.0.Final";
   private static final Dependency DEPENDENCY = DependencyBuilder.create(COORDINATES);
   private static final Dependency DEPENDENCY2 = DependencyBuilder.create(COORDINATES2);

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

         CommandController command = testHarness.createCommandController(AddDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", COORDINATES);
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         command.execute();

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

         CommandController command = testHarness.createCommandController(AddDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", Arrays.asList(COORDINATES, COORDINATES2));
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         command.execute();

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

         CommandController command = testHarness.createCommandController(AddDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", COORDINATES + ":test");
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         command.execute();

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

         CommandController command = testHarness.createCommandController(AddManagedDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", COORDINATES);
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         command.execute();

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

         CommandController command = testHarness.createCommandController(AddManagedDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", Arrays.asList(COORDINATES, COORDINATES2));
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         command.execute();

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

         CommandController command = testHarness.createCommandController(RemoveDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", DEPENDENCY);
         command.setValueFor("removeManaged", "true");
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         command.execute();

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

         CommandController command = testHarness.createCommandController(RemoveDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", Arrays.asList(DEPENDENCY, DEPENDENCY2));
         command.setValueFor("removeManaged", "false");
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         command.execute();

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

         CommandController command = testHarness.createCommandController(RemoveManagedDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", DEPENDENCY);
         command.setValueFor("removeUnmanaged", "true");
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         command.execute();

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

         CommandController command = testHarness.createCommandController(RemoveManagedDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", Arrays.asList(DEPENDENCY, DEPENDENCY2));
         command.setValueFor("removeUnmanaged", "false");
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         command.execute();

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

         CommandController command = testHarness.createCommandController(HasDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", COORDINATES);
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         Result result = command.execute();
         Assert.assertFalse(result instanceof Failed);
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

         CommandController command = testHarness.createCommandController(HasDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", COORDINATES);
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         Result result = command.execute();
         Assert.assertTrue(result instanceof Failed);
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

         CommandController command = testHarness.createCommandController(HasDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", Arrays.asList(COORDINATES, COORDINATES2));
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         Result result = command.execute();
         Assert.assertTrue(result instanceof Failed);
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

         CommandController command = testHarness.createCommandController(HasManagedDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", COORDINATES);
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         Result result = command.execute();
         Assert.assertFalse(result instanceof Failed);
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

         CommandController command = testHarness.createCommandController(HasManagedDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", COORDINATES);
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         Result result = command.execute();
         Assert.assertTrue(result instanceof Failed);
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

         CommandController command = testHarness.createCommandController(HasManagedDependenciesCommand.class,
                  project.getRoot());
         command.initialize();
         command.setValueFor("arguments", Arrays.asList(COORDINATES, COORDINATES2));
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         Result result = command.execute();
         Assert.assertTrue(result instanceof Failed);
      }
      finally
      {
         project.getRoot().delete(true);
      }
   }
}