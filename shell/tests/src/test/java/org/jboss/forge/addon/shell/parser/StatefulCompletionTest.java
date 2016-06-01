/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.parser;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.mock.wizard.MockCommand;
import org.jboss.forge.addon.shell.mock.wizard.MockNoOptionsCommand;
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
public class StatefulCompletionTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addClasses(MockCommand.class, MockNoOptionsCommand.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private ShellTest test;

   @After
   public void tearDown() throws Exception
   {
      test.close();
   }

   @Test
   @Ignore("Review")
   public void testCommandAutocompleteNoArguments() throws Exception
   {
      test.clearScreen();
      test.waitForCompletion("mock-command ", "mock", 15, TimeUnit.SECONDS);
      test.waitForCompletion(5000, TimeUnit.SECONDS);
      Assert.assertEquals("mock-command --", test.getBuffer());
      String stdout = test.waitForCompletion(5, TimeUnit.SECONDS);

      Assert.assertThat(stdout, containsString("--proceed"));
      Assert.assertThat(stdout, containsString("--key"));
      Assert.assertThat(stdout, containsString("--values"));

      test.clearScreen();
      test.waitForCompletion("mock-command --proceed ", "mock-command --pro", 15, TimeUnit.SECONDS);
   }

   @Test
   public void testCommandAutocompleteNoOptions() throws Exception
   {
      test.clearScreen();
      test.waitForCompletion("no-opts-command ", "no-opts-", 15, TimeUnit.SECONDS);
      Assert.assertEquals("no-opts-command ", test.getBuffer());
   }

}
