package org.jboss.forge.addons;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.aesh.ForgeShell;
import org.jboss.forge.aesh.TestShellConfiguration;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class NewAddonProjectAeshTest
{
   @Deployment
   @Dependencies({ @Addon(name = "org.jboss.forge:ui", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:aesh-test-harness", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:aesh", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:maven", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:addons", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:resources", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:projects", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:projects", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:aesh", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:aesh-test-harness", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:resources", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private ForgeShell shell;

   @Inject
   private TestShellConfiguration streams;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   @Test
   public void testContainerInjection() throws Exception
   {
      Assert.assertNotNull(shell);

      File target = File.createTempFile("forge", "new-project-aesh");
      target.delete();

      streams.getStdIn().write(("new-project " +
               "--named lincoln " +
               "--topLevelPackage org.lincoln " +
               "--targetLocation " + target.getAbsolutePath() + " " +
               "--type \"Forge Addon\" " +
               "--overwrite " +
               "--version 1.0.0-SNAPSHOT").getBytes());

      streams.getStdIn().write("\n".getBytes());

      Thread.sleep(1500);

      System.out.println("OUT:" + streams.getStdOut().toString());
      System.out.println("ERR:" + streams.getStdErr().toString());

      DirectoryResource projectRoot = (DirectoryResource) resourceFactory.create(target);
      Project project = projectFactory.findProject(projectRoot.getChildDirectory("lincoln"));

      Assert.assertNotNull(project);

      Assert.assertTrue(project.getProjectRoot().exists());
      // Assert.assertTrue("ADDON module is missing", projectRoot.getChild("addon").exists());
      // Assert.assertTrue("API module is missing", projectRoot.getChild("api").exists());
      // Assert.assertTrue("IMPL module is missing", projectRoot.getChild("impl").exists());
      // Assert.assertTrue("SPI module is missing", projectRoot.getChild("spi").exists());
      // Assert.assertTrue("TESTS module is missing", projectRoot.getChild("tests").exists());

      shell.stopShell();
   }
}
