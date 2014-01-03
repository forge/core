/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.MockCommandExecutionListener;
import org.jboss.forge.addon.shell.mock.command.Career;
import org.jboss.forge.addon.shell.mock.command.FooCommand;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
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
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ManProviderTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(FooCommand.class, Career.class)
               .addBeansXML()
               .addClass(MockCommandExecutionListener.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   private final int timeoutQuantity = 500;

   @Inject
   private ShellTest test;

   @After
   public void after() throws IOException
   {
      test.clearScreen();
   }

   @Test(timeout = 10000)
   public void testManOutput() throws Exception
   {
      test.clearScreen();
      MockCommandExecutionListener listener = new MockCommandExecutionListener();
      test.getShell().addCommandExecutionListener(listener);
      Result result = test.execute("man exit", timeoutQuantity, TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);
      String out = test.getStdOut();
      Assert.assertThat(out, containsString("exit the shell"));
      Assert.assertTrue(listener.isPreExecuted());
      Assert.assertTrue(listener.isPostExecuted());
   }

   @Test(timeout = 10000000)
   public void testManPageForUndocumentedForgeCommand() throws Exception
   {
      test.clearScreen();
      Result result = test.execute("man foocommand", timeoutQuantity, TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);
      String out = test.getStdOut();
      Assert.assertThat(out, containsString("foocommand -- Uncategorized"));
      Assert.assertThat(out, containsString("Do some foo"));
      Assert.assertThat(out, containsString("required"));
      Assert.assertThat(out, containsString("help"));
      Assert.assertThat(out, containsString("target location"));
      Assert.assertThat(out, containsString("[FileResource]"));
   }
}
