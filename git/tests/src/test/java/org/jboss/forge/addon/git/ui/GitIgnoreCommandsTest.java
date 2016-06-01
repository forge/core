/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.git.gitignore.resources.GitIgnoreResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Streams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GitIgnoreCommandsTest
{

   private UITestHarness testHarness;
   private ProjectFactory projectFactory;

   private Project project;

   @Before
   public void setup() throws Exception
   {
      AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      this.projectFactory = addonRegistry.getServices(ProjectFactory.class).get();
      this.testHarness = addonRegistry.getServices(UITestHarness.class).get();
      project = projectFactory.createTempProject();
      CommandController gitSetupCommandTester = testHarness.createCommandController(GitSetupCommand.class,
               project.getRoot());
      gitSetupCommandTester.initialize();
      gitSetupCommandTester.execute();

      CommandController gitIgnoreSetupTester = testHarness.createCommandController(GitIgnoreSetupCommand.class,
               project.getRoot());
      gitIgnoreSetupTester.initialize();
      gitIgnoreSetupTester.setValueFor("templateRepoDir", getCloneDir());
      gitIgnoreSetupTester.execute();

      Resource<?> cloneDir = getCloneDir();
      boolean templateFound = false;
      for (Resource<?> resource : cloneDir.listResources())
      {
         if (resource.getName().endsWith(".gitignore"))
         {
            templateFound = true;
            break;
         }
      }
      assertTrue(templateFound);
   }

   @Test
   public void testGitIgnoreUpdateRepo() throws Exception
   {
      CommandController gitIgnoreUpdateRepoTester = testHarness.createCommandController(
               GitIgnoreUpdateRepoCommand.class, project.getRoot());
      gitIgnoreUpdateRepoTester.initialize();
      Result result = gitIgnoreUpdateRepoTester.execute();
      assertTrue(result.getMessage().contains("Local gitignore repository updated"));
   }

   @Test
   public void testGitIgnoreListTemplates() throws Exception
   {
      CommandController gitIgnoreListTemplatesTester = testHarness.createCommandController(
               GitIgnoreListTemplatesCommand.class, project.getRoot());
      gitIgnoreListTemplatesTester.initialize();
      Result result = gitIgnoreListTemplatesTester.execute();
      String listOutput = result.getMessage().substring(result.getMessage().indexOf("==="));
      assertFalse(listOutput.contains(".gitignore"));
      assertTrue(listOutput.contains("= Languages ="));
      assertTrue(listOutput.contains("= Globals ="));
      assertTrue(listOutput.contains("Java"));
      assertTrue(listOutput.contains("Eclipse"));
   }

   @Test
   public void testGitIgnoreCreate() throws Exception
   {
      executeGitIgnoreCreate();

      GitIgnoreResource gitignore = gitIgnoreResource();
      assertTrue(gitignore.exists());
      String content = Streams.toString(gitignore.getResourceInputStream());
      assertTrue(content.contains(".settings/"));
   }

   @Test
   public void testGitIgnoreAddPattern() throws Exception
   {
      executeGitIgnoreCreate();

      CommandController gitIgnoreAddPatternTester = testHarness.createCommandController(
               GitIgnoreAddPatternCommand.class, project.getRoot());
      gitIgnoreAddPatternTester.initialize();
      gitIgnoreAddPatternTester.setValueFor("pattern", "*.forge");
      gitIgnoreAddPatternTester.execute();

      GitIgnoreResource gitignore = gitIgnoreResource();
      String content = Streams.toString(gitignore.getResourceInputStream());
      assertTrue(content.contains("*.forge"));
   }

   @Test
   public void testGitIgnoreRemovePattern() throws Exception
   {
      executeGitIgnoreCreate();

      CommandController gitIgnoreRemovePatternTester = testHarness.createCommandController(
               GitIgnoreRemovePatternCommand.class, project.getRoot());
      gitIgnoreRemovePatternTester.initialize();
      gitIgnoreRemovePatternTester.setValueFor("pattern", ".metadata");
      gitIgnoreRemovePatternTester.execute();

      GitIgnoreResource gitignore = gitIgnoreResource();
      String content = Streams.toString(gitignore.getResourceInputStream());
      assertFalse(content.contains(".metadata"));
   }

   @Test
   public void testGitIgnoreListPatterns() throws Exception
   {
      executeGitIgnoreCreate();

      CommandController gitIgnoreListPatternsTester = testHarness.createCommandController(
               GitIgnoreListPatternsCommand.class, project.getRoot());
      gitIgnoreListPatternsTester.initialize();
      Result result = gitIgnoreListPatternsTester.execute();

      assertTrue(result.getMessage().contains("target/"));
      assertTrue(result.getMessage().contains(".settings/"));
   }

   private Resource<?> getCloneDir()
   {
      return project.getRoot().getChild("gibo");
   }

   private GitIgnoreResource gitIgnoreResource()
   {
      return project.getRoot().reify(DirectoryResource.class)
               .getChildOfType(GitIgnoreResource.class, ".gitignore");
   }

   private void executeGitIgnoreCreate() throws Exception
   {
      CommandController gitIgnoreCreateTester = testHarness.createCommandController(GitIgnoreCreateCommand.class,
               project.getRoot());
      gitIgnoreCreateTester.initialize();
      gitIgnoreCreateTester.setValueFor("templates", "Eclipse Maven");
      gitIgnoreCreateTester.execute();
   }

   @After
   public void tearDown() throws Exception
   {
      project.getRoot().delete(true);
   }

}
