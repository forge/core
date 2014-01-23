package org.jboss.forge.addon.projects.impl;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.maven.projects.MavenBuildSystem;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.mock.MockBuildSystem;
import org.jboss.forge.addon.projects.mock.MockProjectType;
import org.jboss.forge.addon.projects.ui.AddDependencyCommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.ui.test.UITestHarness;
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

   private static final String COORDINATES = "org.jboss.forge.addon:projects-api:2.0.0-SNAPSHOT";
   private static final Dependency DEPENDENCY = DependencyBuilder.create(COORDINATES);

   @Test
   public void testAddDependency() throws Exception
   {
      Project project = factory.createTempProject(build);
      try
      {
         CommandController command = testHarness.createCommandController(AddDependencyCommand.class,
                  project.getProjectRoot());
         command.initialize();
         command.setValueFor("dependency", COORDINATES);
         Assert.assertTrue(command.isValid());
         Assert.assertTrue(command.canExecute());
         command.execute();

         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasDirectManagedDependency(DEPENDENCY));
         Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveManagedDependency(DEPENDENCY));
      }
      finally
      {
         project.getProjectRoot().delete(true);
      }
   }
}