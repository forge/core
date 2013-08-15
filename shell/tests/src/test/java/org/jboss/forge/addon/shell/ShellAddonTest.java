/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.Key;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
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
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(FooCommand.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private Shell shell;

   @Inject
   private ShellTest test;

   @Inject
   private FooCommand fooCommand;

   @Test
   public void testContainerInjection() throws Exception
   {
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
      test.waitForStdOutChanged("\n", 5, TimeUnit.SECONDS);
      System.out.println("OUT:" + test.getStdOut());
      System.out.println("ERR:" + test.getStdErr());

      test.waitForStdOutChanged("list-services\n", 5, TimeUnit.SECONDS);
      System.out.println("OUT:" + test.getStdOut());
      System.out.println("ERR:" + test.getStdErr());
   }

}
