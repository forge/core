/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.mock.MockBuildSystem;
import org.jboss.forge.addon.projects.mock.MockProject;
import org.jboss.forge.addon.projects.mock.MockProjectType;
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

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ProjectInjectionTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClass(MockProjectType.class)
               .addClass(MockBuildSystem.class)
               .addClass(MockProject.class)
               .addClass(ProjectUICommand.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );

      return archive;
   }

   @Inject
   private ProjectFactory project;

   @Inject
   private UITestHarness testHarness;

   @Test
   public void testProjectInjection() throws Exception
   {
      Project tempProject = project.createTempProject();
      try (CommandController c = testHarness.createCommandController("project-inject", tempProject.getProjectRoot()))
      {
         c.initialize();
         Assert.assertTrue("Project should have been injected", c.isValid());
      }
   }

   @Test
   public void testNoProjectInjection() throws Exception
   {
      try (CommandController c = testHarness.createCommandController("project-inject"))
      {
         c.initialize();
         Assert.assertFalse("Project shouldn't have been injected", c.isValid());
      }
   }
}
