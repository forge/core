/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.mock.MockCommandExecutionListener;
import org.jboss.forge.addon.shell.mock.command.Career;
import org.jboss.forge.addon.shell.mock.command.FooCommand;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
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
public class ForgeManProviderTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addClasses(FooCommand.class, Career.class)
               .addBeansXML()
               .addClass(MockCommandExecutionListener.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   private final int timeoutQuantity = 5;

   @Inject
   private ShellTest test;

   @After
   public void tearDown() throws Exception
   {
      test.close();
   }

   @Test(timeout = 10000)
   public void testManOutput() throws Exception
   {
      MockCommandExecutionListener listener = new MockCommandExecutionListener();
      test.getShell().addCommandExecutionListener(listener);
      test.execute("man exit");
      // Wait for the above line to be consumed
      Thread.sleep(1000);
      test.waitForStdOutValue("Exit the shell", timeoutQuantity, TimeUnit.SECONDS);
      test.execute("q");
      // Wait for the above line to be consumed
      Thread.sleep(1000);
      Assert.assertThat(test.getStdOut(), containsString("Exit the shell"));
      Assert.assertTrue(listener.isPreExecuted());
      Assert.assertTrue(listener.isPostExecuted());
      Assert.assertFalse(listener.getResult() instanceof Failed);
   }

   @Test(timeout = 10000)
   public void testManPageForUndocumentedForgeCommand() throws Exception
   {
      MockCommandExecutionListener listener = new MockCommandExecutionListener();
      test.getShell().addCommandExecutionListener(listener);
      test.execute("man foocommand");
      // Wait for the above line to be consumed
      Thread.sleep(1000);
      test.execute("q");
      test.waitForStdOutValue("foocommand -- Uncategorized", timeoutQuantity, TimeUnit.SECONDS);
      String out = test.getStdOut();
      Assert.assertThat(out, containsString("foocommand -- Uncategorized"));
      Assert.assertThat(out, containsString("Do some foo"));
      Assert.assertThat(out, containsString("required"));
      Assert.assertThat(out, containsString("help"));
      Assert.assertThat(out, containsString("target location"));
      Assert.assertThat(out, containsString("[FileResource]"));
      Assert.assertFalse(listener.getResult() instanceof Failed);
   }
}
