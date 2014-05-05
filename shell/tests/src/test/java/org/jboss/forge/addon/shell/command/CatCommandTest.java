package org.jboss.forge.addon.shell.command;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CatCommandTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private ShellTest shellTest;

   @Inject
   private ProjectFactory projectFactory;

   @Test(timeout = 10000)
   public void testCatCommandInvalidArgument() throws Exception
   {
      Result result = shellTest.execute("cat foo bar", 5, TimeUnit.SECONDS);
      Assert.assertTrue(result instanceof Failed);
      String out = shellTest.getStdOut();
      Assert.assertThat(out, containsString("cat: foo: No such file or directory"));
      Assert.assertThat(out, containsString("cat: bar: No such file or directory"));
   }

   @Test
   public void testCatCommand() throws Exception
   {
      Project project = projectFactory.createTempProject();
      File target = new File(project.getRoot().getFullyQualifiedName(), "test.java");
      target.createNewFile();

      FileResource<?> source = project.getRoot().getChild(target.getName()).reify(FileResource.class);
      source.setContents("public void test() {}");

      shellTest.execute("cat " + source.getFullyQualifiedName(), 5, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString("test()"));
   }

   @Test
   public void testCatColoredCommand() throws Exception
   {
      Project project = projectFactory.createTempProject();
      File target = new File(project.getRoot().getFullyQualifiedName(), "test.java");
      target.createNewFile();

      FileResource<?> source = project.getRoot().getChild(target.getName()).reify(FileResource.class);
      source.setContents("public void test() {}");

      shellTest.execute("cat " + source.getFullyQualifiedName() + " --color", 5, TimeUnit.SECONDS);
      // the string should be colors, so there are color codes between the statements
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.not(CoreMatchers.containsString("public void")));
   }
}
