/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.parser;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.mock.wizard.MockCommand;
import org.jboss.forge.addon.shell.mock.wizard.MockNoOptionsCommand;
import org.jboss.forge.addon.shell.test.ShellTest;
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
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class StatefulCompletionTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(MockCommand.class, MockNoOptionsCommand.class)
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
   public void testCommandAutocompleteNoArguments() throws Exception
   {
      test.clearScreen();
      test.waitForCompletion("mock-command ", "mock", 5, TimeUnit.SECONDS);
      test.waitForCompletion(5000, TimeUnit.SECONDS);
      Assert.assertEquals("mock-command --", test.getBuffer().getLine());
      String stdout = test.waitForCompletion(5, TimeUnit.SECONDS);

      Assert.assertThat(stdout, containsString("--proceed"));
      Assert.assertThat(stdout, containsString("--key"));
      Assert.assertThat(stdout, containsString("--values"));
   }

   @Test
   public void testCommandAutocompleteNoOptions() throws Exception
   {
      test.clearScreen();
      test.waitForCompletion("no-opts-command ", "no-opts-", 5, TimeUnit.SECONDS);
      test.waitForCompletion(5, TimeUnit.SECONDS);
      Assert.assertEquals("no-opts-command ", test.getBuffer().getLine());
   }

}
