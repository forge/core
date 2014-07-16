/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.mock.wizard.MockMultipleArgsCommand;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vineet Reynolds
 */
@RunWith(Arquillian.class)
public class ShellInputManyTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(MockMultipleArgsCommand.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
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
   public void testCommandForInputMany() throws Exception
   {
      // Verify autocomplete for parameter works
      test.clearScreen();
      test.waitForCompletion("mock-command-inputmany ", "mock", 5, TimeUnit.SECONDS);
      test.waitForCompletion("mock-command-inputmany --values ", "--v", 5, TimeUnit.SECONDS);
      Assert.assertEquals("mock-command-inputmany --values ", test.getBuffer());

      test.clearScreen();

      // Verify that command execution works for one argument
      Result result = test.execute("mock-command-inputmany --values one", 5, TimeUnit.SECONDS);
      Assert.assertEquals("Command executed with input values : one ", result.getMessage());

      // Verify that command execution works for multiple arguments
      result = test.execute("mock-command-inputmany --values one two three", 5, TimeUnit.SECONDS);
      Assert.assertEquals("Command executed with input values : one two three ", result.getMessage());

      // Verify that command execution fails for no arguments
      test.clearScreen();
      test.waitForStdErrChanged("mock-command-inputmany --values\n", 5, TimeUnit.SECONDS);
      Assert.assertThat(test.getStdErr(),
               containsString("Must specify at least one value for --values before continuing."));
   }

}
