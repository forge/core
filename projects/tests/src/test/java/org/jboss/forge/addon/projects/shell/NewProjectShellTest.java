package org.jboss.forge.addon.projects.shell;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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
               "--version 1.0.0-SNAPSHOT"), 10000, TimeUnit.SECONDS);

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

}
