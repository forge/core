package org.jboss.forge.addon.shell;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.Key;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.ForgeShell;
import org.jboss.forge.addon.shell.test.ShellTests;
import org.jboss.forge.addon.shell.test.TestShellConfiguration;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AeshAddonTest
{
   private KeyOperation completeChar = new KeyOperation(Key.CTRL_I, Operation.COMPLETE);

   @Deployment
   @Dependencies({ @Addon(name = "org.jboss.forge.addon:ui", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:aesh-test-harness", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:aesh", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:resources", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(FooCommand.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:ui", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:aesh", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:aesh-test-harness", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:resources", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private ForgeShell shell;

   @Inject
   private TestShellConfiguration streams;

   @Inject
   private FooCommand fooCommand;

   @Test
   public void testContainerInjection() throws Exception
   {
      Assert.assertNotNull(shell);

      streams.getStdIn().write(("foo\n").getBytes());
      System.out.println("OUT:" + streams.getStdOut().toString());
      System.out.println("ERR:" + streams.getStdErr().toString());

      String prompt = shell.getPrompt();
      Assert.assertEquals("[forge]$ ", prompt);

      streams.getStdIn().write("fo".getBytes());
      streams.getStdIn().write(completeChar.getFirstValue());
      System.out.println("OUT:" + streams.getStdOut().toString());
      System.out.println("ERR:" + streams.getStdErr().toString());
      streams.getStdIn().write("\n".getBytes());
      ShellTests.waitForCallback(streams.getStdOut(), 10, TimeUnit.SECONDS);
      System.out.println("OUT:" + streams.getStdOut().toString());
      System.out.println("ERR:" + streams.getStdErr().toString());

      streams.getStdIn().write(("list-services\n").getBytes());
      ShellTests.waitForCallback(streams.getStdOut(), 10, TimeUnit.SECONDS);
      System.out.println("OUT:" + streams.getStdOut().toString());
      System.out.println("ERR:" + streams.getStdErr().toString());

      streams.getStdIn().write(("exit\n").getBytes());
      System.out.println("OUT:" + streams.getStdOut().toString());
      System.out.println("ERR:" + streams.getStdErr().toString());

      shell.stopShell();
   }

}
