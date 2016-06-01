/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.Key;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.mock.command.FooCommand;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ShellAddonTest
{
   private KeyOperation completeChar = new KeyOperation(Key.CTRL_I, Operation.COMPLETE);

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:resources"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addPackage(FooCommand.class.getPackage())
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private ShellTest test;

   @Inject
   private FooCommand fooCommand;

   @After
   public void tearDown() throws Exception
   {
      test.close();
   }

   @Test
   public void testContainerInjection() throws Exception
   {
      Shell shell = test.getShell();
      Assert.assertNotNull(shell);
      Assert.assertNotNull(fooCommand);

      test.getStdIn().write(("foo\n").getBytes());
      System.out.println("OUT:" + test.getStdOut());
      System.out.println("ERR:" + test.getStdErr());

      // String prompt = shell.getPrompt();
      // Assert.assertEquals("[forge]$ ", prompt);

      test.getStdIn().write("fo".getBytes());
      test.getStdIn().write(completeChar.getFirstValue());
      test.getStdIn().flush();
      System.out.println("OUT:" + test.getStdOut());
      System.out.println("ERR:" + test.getStdErr());
      test.waitForStdOutChanged("\n", 15, TimeUnit.SECONDS);
      System.out.println("OUT:" + test.getStdOut());
      System.out.println("ERR:" + test.getStdErr());

      test.waitForStdOutChanged("list-services\n", 15, TimeUnit.SECONDS);
      System.out.println("OUT:" + test.getStdOut());
      System.out.println("ERR:" + test.getStdErr());
   }

   @Test
   @Ignore("Until ShellTest is used properly")
   public void testDidYouMean() throws Exception
   {
      test.waitForStdOutChanged("cde\n", 15, TimeUnit.SECONDS);
      Assert.assertThat(test.getStdOut(), CoreMatchers.containsString("Did you mean this?"));
   }
}
