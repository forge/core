/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.parser;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.mock.wizard.MockWizardBegin;
import org.jboss.forge.addon.shell.mock.wizard.MockWizardStep;
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
public class WizardCompletionTest
{
   private static final int TIMEOUT = 5;

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(MockWizardBegin.class, MockWizardStep.class)
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

   @Test(timeout = 10000)
   public void testWizardInitialStepAutocomplete() throws Exception
   {
      test.clearScreen();
      assertCompletionStep("mockwizard ", "mockw");
      assertCompletionStep("mockwizard --values ", "--v");
      String stdout = assertCompletionStepWithSuggestions("mockwizard --values foo --", "foo --");

      Assert.assertThat(stdout, containsString("--proceed"));
      Assert.assertThat(stdout, containsString("--key"));
      Assert.assertThat(stdout, not(containsString("--selections")));
      Assert.assertThat(stdout, not(containsString("--done")));

      assertCompletionStep("mockwizard --values foo --proceed ", "p");
      stdout = assertCompletionStepWithSuggestions("mockwizard --values foo --proceed --", "--");

      Assert.assertThat(stdout, containsString("--key"));
      Assert.assertThat(stdout, containsString("--done"));
      Assert.assertThat(stdout, containsString("--selections"));

      assertCompletionStep("mockwizard --values foo --proceed --selections ", "sel");
      stdout = assertCompletionStepWithSuggestions("mockwizard --values foo --proceed --selections blah --", "blah --");
      Assert.assertThat(stdout, not(containsString("--key")));
      Assert.assertThat(stdout, containsString("--done"));
   }

   @Test(timeout = 10000)
   public void testWizardInitialStepAutocompleteBooleanFlagWithValue() throws Exception
   {
      test.clearScreen();
      assertCompletionStep("mockwizard ", "mockw");
      assertCompletionStep("mockwizard --values ", "--v");
      String stdout = assertCompletionStepWithSuggestions("mockwizard --values foo --", "foo --");

      Assert.assertThat(stdout, containsString("--proceed"));
      Assert.assertThat(stdout, containsString("--key"));
      Assert.assertThat(stdout, not(containsString("--selections")));
      Assert.assertThat(stdout, not(containsString("--done")));

      assertCompletionStep("mockwizard --values foo --proceed ", "p");
      stdout = assertCompletionStepWithSuggestions("mockwizard --values foo --proceed true --", "true --");

      Assert.assertThat(stdout, containsString("--key"));
      Assert.assertThat(stdout, containsString("--done"));
      Assert.assertThat(stdout, containsString("--selections"));

      assertCompletionStep("mockwizard --values foo --proceed true --selections ", "sel");
   }

   private void assertCompletionStep(final String expected, final String write) throws TimeoutException
   {
      test.waitForBufferValue(new Callable<String>()
      {
         @Override
         public String call() throws Exception
         {
            test.write(write);
            test.sendCompletionSignal();
            return null;
         }
      }, TIMEOUT, TimeUnit.SECONDS, expected);
      Assert.assertThat(test.getBuffer().getLine(), equalTo(expected));
   }

   private String assertCompletionStepWithSuggestions(final String expected, final String write)
            throws TimeoutException
   {
      test.waitForStdOutValue(new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            test.waitForBufferValue(new Callable<String>()
            {
               @Override
               public String call() throws Exception
               {
                  test.write(write);
                  test.sendCompletionSignal();
                  return null;
               }
            }, TIMEOUT, TimeUnit.SECONDS, expected);
            Assert.assertThat(test.getBuffer().getLine(), equalTo(expected));
            return null;
         }
      }, TIMEOUT, TimeUnit.SECONDS, expected);

      return test.getStdOut();
   }

}
