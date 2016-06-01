/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.parser;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.mock.command.Career;
import org.jboss.forge.addon.shell.mock.command.FooCommand;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class CommandCompletionTest
{
   private static final int QUANTITY = 5;

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addClasses(FooCommand.class, Career.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private ShellTest test;

   @Before
   public void setUp() throws IOException
   {
      test.clearScreen();
   }

   @After
   public void tearDown() throws Exception
   {
      test.close();
   }

   @Test
   public void testCommandAutocomplete() throws Exception
   {
      test.waitForCompletion("foocommand ", "foocomm", QUANTITY, TimeUnit.SECONDS);
   }

   @Test
   public void testCommandAutocompleteOption() throws Exception
   {
      test.waitForCompletion("foocommand --help ", "foocommand --h", QUANTITY, TimeUnit.SECONDS);
      Assert.assertEquals("foocommand --help ", test.getBuffer());
   }

   @Test
   public void testCommandAutocompleteOptionShortName() throws Exception
   {
      test.waitForCompletion("foocommand -h ", "foocommand -h", QUANTITY, TimeUnit.SECONDS);
      Assert.assertEquals("foocommand -h ", test.getBuffer());
   }

   @Test
   public void testValuesWithSpaceCompletion() throws Exception
   {
      test.waitForCompletion("foocommand --value-with-spaces Value\\ ",
               "foocommand --value-with-spaces Value",
               QUANTITY, TimeUnit.SECONDS);
      String stdOut = test.waitForCompletion(QUANTITY, TimeUnit.SECONDS);
      Assert.assertThat(
               stdOut,
               allOf(containsString("Value 1"), containsString("Value 2"), containsString("Value 10"),
                        containsString("Value 100")));
   }

   @Test
   public void testValuesWithSpaceCompletionWithSlash() throws Exception
   {
      test.write("foocommand --value-with-spaces Value\\");
      test.sendCompletionSignal();
      test.waitForBufferChanged(new Callable<Object>()
      {
         @Override
         public Object call()
         {
            String stdOut = test.getStdOut();
            Assert.assertThat(
                     stdOut,
                     allOf(not(containsString("Value 1")), not(containsString("Value 2")),
                              not(containsString("Value 10")),
                              not(containsString("Value 100"))));
            return null;
         }
      }, QUANTITY, TimeUnit.SECONDS);
   }

   @Test
   public void testUISelectOneWithEnum() throws Exception
   {
      test.waitForCompletion("foocommand --career ME", "foocommand --career M",
               QUANTITY, TimeUnit.SECONDS);
      String stdOut = test.waitForCompletion(QUANTITY, TimeUnit.SECONDS);
      Assert.assertThat(stdOut,
               allOf(containsString(Career.MEDICINE.toString()), containsString(Career.MECHANICS.toString())));
   }

   @Test
   public void testUISelectManyWithEnum() throws Exception
   {
      test.waitForCompletion("foocommand --many-career ME", "foocommand --many-career M",
               QUANTITY, TimeUnit.SECONDS);
      String stdOut = test.waitForCompletion(QUANTITY, TimeUnit.SECONDS);
      Assert.assertThat(stdOut,
               allOf(containsString(Career.MEDICINE.toString()), containsString(Career.MECHANICS.toString())));
   }

   @Test
   public void testUISelectManyWithEnumWildcard() throws Exception
   {
      test.waitForCompletion("foocommand --many-career ME", "foocommand --many-career M",
               QUANTITY, TimeUnit.SECONDS);
      String stdOut = test.waitForCompletion(QUANTITY, TimeUnit.SECONDS);
      Assert.assertThat(stdOut,
               allOf(containsString(Career.MEDICINE.toString()), containsString(Career.MECHANICS.toString())));

      test.write("*");
      Result result = test.execute(stdOut, QUANTITY, TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);
      Assert.assertThat(result.getMessage(),
               allOf(containsString(Career.MEDICINE.toString()), containsString(Career.MECHANICS.toString())));
   }

   @Test
   public void testDisabledOptionsShouldNotBeDisplayed() throws Exception
   {
      test.waitForCompletion("foocommand --", "foocommand --", QUANTITY, TimeUnit.SECONDS);
      String stdOut = test.waitForCompletion(QUANTITY, TimeUnit.SECONDS);
      Assert.assertThat(stdOut, not(containsString("--disabledOption")));
   }
}
