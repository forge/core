package org.jboss.forge.addon.git.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.git.gitignore.resources.GitIgnoreResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GitIgnoreCommandsTest
{

   @Inject
   private ProjectFactory projectFactory;

   private Project project;

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven"),
            @AddonDeployment(name = "org.jboss.forge.addon:git")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:git"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );

      return archive;
   }

   @Inject
   private UITestHarness testHarness;

   @Before
   public void setup() throws Exception
   {
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
      gitIgnoreRemovePatternTester.setValueFor("pattern", "target/");
      gitIgnoreRemovePatternTester.execute();

      GitIgnoreResource gitignore = gitIgnoreResource();
      String content = Streams.toString(gitignore.getResourceInputStream());
      assertFalse(content.contains("target/"));
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
