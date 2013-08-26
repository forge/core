package org.jboss.forge.addon.projects.shell;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class NewProjectShellTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:addons"),
            @AddonDependency(name = "org.jboss.forge.addon:parser-java"),
            @AddonDependency(name = "org.jboss.forge.addon:projects")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness")
               );

      return archive;
   }

   @Inject
   private ShellTest test;

   @After
   public void after() throws IOException
   {
      test.clearScreen();
   }

   @Test
   public void testWizardCommandExecution() throws Exception
   {
      File target = OperatingSystemUtils.createTempDir();

      Result result = test.execute(("new-project " +
               "--named lincoln " +
               "--topLevelPackage org.lincoln " +
               "--targetLocation " + target.getAbsolutePath() + " " +
               "--type \"Maven - Java\" " +
               "--overwrite " +
               "--version 1.0.0-SNAPSHOT"), 10, TimeUnit.SECONDS);

      Assert.assertFalse(result instanceof Failed);
      Assert.assertTrue(target.exists());
      Assert.assertTrue(target.isDirectory());
      File projectDir = new File(target, "lincoln");
      Assert.assertTrue(projectDir.exists());
      Assert.assertTrue(new File(projectDir, "pom.xml").exists());
   }

   @Test
   public void testTopLevelPackageOptional() throws Exception
   {
      File target = OperatingSystemUtils.createTempDir();

      Result result = test.execute(("new-project " +
               "--named lincoln-three " +
               "--targetLocation " + target.getAbsolutePath() + " " +
               "--type \"Maven - Resources\" " +
               "--version 1.0.0-SNAPSHOT"), 10, TimeUnit.SECONDS);

      Assert.assertFalse(result instanceof Failed);
      Assert.assertTrue(target.exists());
      Assert.assertTrue(target.isDirectory());
      File projectDir = new File(target, "lincoln-three");
      Assert.assertTrue(projectDir.exists());

      File pomFile = new File(projectDir, "pom.xml");
      Assert.assertTrue(pomFile.exists());
      String pomContents = Streams.toString(new BufferedInputStream(
               new FileInputStream(pomFile)));
      Assert.assertTrue(pomContents.contains("org.lincoln.three"));
   }

   @Test
   public void testCompletionFlow() throws Exception
   {
      test.waitForCompletion("new-project ", "new-pr", 5, TimeUnit.SECONDS);
      test.waitForCompletion("new-project --", "", 5, TimeUnit.SECONDS);

      String stdout = test.waitForCompletion(5, TimeUnit.SECONDS);
      Assert.assertThat(stdout, containsString("--named"));
      Assert.assertThat(stdout, containsString("--topLevelPackage"));
      Assert.assertThat(stdout, containsString("--targetLocation"));
      Assert.assertThat(stdout, containsString("--overwrite"));
      Assert.assertThat(stdout, containsString("--type"));
      Assert.assertThat(stdout, containsString("--version"));
      Assert.assertThat(stdout, not(containsString("--addons")));

      stdout = test.waitForCompletion("new-project --named lincoln --type Maven\\ -\\ ",
               "named lincoln --type Mave",
               5, TimeUnit.SECONDS);

      Assert.assertThat(stdout, containsString("Maven - Java"));
      Assert.assertThat(stdout, containsString("Maven - Resources"));

      stdout = test.waitForCompletion("new-project --named lincoln --type Maven\\ -\\ Java ",
               "J", 5, TimeUnit.SECONDS);

      stdout = test.waitForCompletion("new-project --named lincoln --type Maven\\ -\\ Java --",
               "--", 5, TimeUnit.SECONDS);

      Assert.assertThat(stdout, containsString("--topLevelPackage"));
      Assert.assertThat(stdout, containsString("--targetLocation"));
      Assert.assertThat(stdout, containsString("--overwrite"));
      Assert.assertThat(stdout, containsString("--type"));
      Assert.assertThat(stdout, containsString("--version"));
      Assert.assertThat(stdout, not(containsString("--addons")));

   }

}
