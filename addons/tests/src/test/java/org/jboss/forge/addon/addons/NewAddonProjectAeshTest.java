package org.jboss.forge.addon.addons;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.test.TestAeshSettingsProvider;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Ignore
@RunWith(Arquillian.class)
public class NewAddonProjectAeshTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:shell"),
            @AddonDependency(name = "org.jboss.forge.addon:dependencies"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:addons"),
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:dependencies"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources")
               );

      return archive;
   }

   @Inject
   private Shell shell;

   @Inject
   private TestAeshSettingsProvider streams;

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
               "--type \"Furnace AddonDependency\" " +
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

      // TODO Wizard steps are not implemented by Aesh, so we can't actually invoke the entire wizard.

      // DependencyFacet dependencies = project.getFacet(DependencyFacet.class);
      // Assert.assertTrue(dependencies.hasEffectiveDependency(DependencyBuilder.create()
      // .setGroupId("org.jboss.forge.furnace").setArtifactId("furnace-api")));
      // Assert.assertTrue("ADDON module is missing", projectRoot.getChild("addon").exists());
      // Assert.assertTrue("API module is missing", projectRoot.getChild("api").exists());
      // Assert.assertTrue("IMPL module is missing", projectRoot.getChild("impl").exists());
      // Assert.assertTrue("SPI module is missing", projectRoot.getChild("spi").exists());
      // Assert.assertTrue("TESTS module is missing", projectRoot.getChild("tests").exists());

      shell.stopShell();
   }
}
