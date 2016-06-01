/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GitSetupCommandTest
{
   private ProjectFactory projectFactory;
   private UITestHarness testHarness;

   private Project project;
   private CommandController commandController;

   @Before
   public void setup() throws Exception
   {
      AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      this.projectFactory = addonRegistry.getServices(ProjectFactory.class).get();
      this.testHarness = addonRegistry.getServices(UITestHarness.class).get();
      project = projectFactory.createTempProject();
      commandController = testHarness.createCommandController(GitSetupCommand.class, project.getRoot());
      commandController.initialize();
   }

   @Test
   public void testGitSetup() throws Exception
   {
      Result result = commandController.execute();
      assertTrue(project.getRoot().reify(DirectoryResource.class).getChildDirectory(".git").isDirectory());
      assertEquals("GIT has been installed.", result.getMessage());
   }

   @Test
   public void testGitSetupCalledTwice() throws Exception
   {
      commandController.execute();
      assertTrue(project.getRoot().reify(DirectoryResource.class).getChildDirectory(".git").isDirectory());

      commandController.initialize();
      Result result = commandController.execute();
      assertTrue(project.getRoot().reify(DirectoryResource.class).getChildDirectory(".git").isDirectory());
      assertEquals("GIT has been installed.", result.getMessage());
   }

   @After
   public void tearDown() throws Exception
   {
      project.getRoot().delete(true);
   }

}
