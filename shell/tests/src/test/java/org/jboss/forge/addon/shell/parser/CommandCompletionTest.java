/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.parser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.*;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.mock.command.Career;
import org.jboss.forge.addon.shell.mock.command.FooCommand;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
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
public class CommandCompletionTest
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
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private ShellTest test;

   @Inject
   private ResourceFactory resourceFactory;

   @After
   public void after() throws IOException
   {
      test.clearScreen();
   }

   @Test
   public void testCommandAutocomplete() throws Exception
   {
      test.waitForCompletion("foocommand ", "foocomm", 5, TimeUnit.SECONDS);
   }

   @Test
   public void testCommandAutocompleteOption() throws Exception
   {
      test.waitForCompletion("foocommand --help ", "foocommand --h", 5, TimeUnit.SECONDS);
      Assert.assertEquals("foocommand --help ", test.getBuffer().getLine());
   }

   @Test
   @Ignore("Ignoring short options at the moment")
   public void testCommandAutocompleteOptionShortName() throws Exception
   {
      test.waitForCompletion("foocommand -h ", "foocommand -h", 5, TimeUnit.SECONDS);
      Assert.assertEquals("foocommand -h ", test.getBuffer().getLine());
   }

   @Test
   @Ignore("Until shell is fixed")
   public void testEscapes() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      tempDir.deleteOnExit();
      DirectoryResource currentResource = resourceFactory.create(DirectoryResource.class, tempDir);
      Shell shell = test.getShell();
      shell.setCurrentResource(currentResource);
      DirectoryResource child = currentResource.getChildDirectory("Forge 2 Escape");
      child.mkdir();
      child.deleteOnExit();
      Result result = test.execute("cd Forge\\ 2\\ Escape", 10, TimeUnit.SECONDS);
      Assert.assertThat(result.getMessage(), CoreMatchers.nullValue());
      Assert.assertEquals(shell.getCurrentResource(), child);
      currentResource.delete(true);
   }

   @Test
   public void testQuotes() throws Exception
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      tempDir.deleteOnExit();
      DirectoryResource currentResource = resourceFactory.create(DirectoryResource.class, tempDir);
      Shell shell = test.getShell();
      shell.setCurrentResource(currentResource);
      FileResource<?> child = currentResource.getChildDirectory("Forge 2 Escape");
      child.mkdir();
      child.deleteOnExit();
      Result result = test.execute("cd \"Forge 2 Escape\"", 10, TimeUnit.SECONDS);
      Assert.assertThat(result.getMessage(), nullValue());
      Assert.assertEquals(shell.getCurrentResource(), child);
      currentResource.delete(true);
   }

   // FIXME: this fails because it throws an exception when trying to create a ParsedCompleteObject
   @Test
   @Ignore("Until shell is fixed")
   public void testValuesWithSpaceCompletion() throws Exception
   {
      test.waitForCompletion("foocommand --valueWithSpaces Value\\ ",
               "foocommand --valueWithSpaces Value",
               5, TimeUnit.SECONDS);
      String stdOut = test.waitForCompletion(5, TimeUnit.SECONDS);
      Assert.assertThat(
               stdOut,
               allOf(containsString("Value 1"), containsString("Value 2"), containsString("Value 10"),
                        containsString("Value 100")));
   }

   @Test
   @Ignore("Until shell is fixed")
   public void testValuesWithSpaceCompletionWithSlash() throws Exception
   {
      test.write("foocommand --valueWithSpaces Value\\");
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
            String line = test.getBuffer().getLine();
            Assert.assertEquals("foocommand --valueWithSpaces Value\\", line);
            return null;
         }
      }, 5, TimeUnit.SECONDS);
   }

   @Test
   public void testUISelectOneWithEnum() throws Exception
   {
      test.waitForCompletion("foocommand --career ME", "foocommand --career M",
               5, TimeUnit.SECONDS);
      String stdOut = test.waitForCompletion(5, TimeUnit.SECONDS);
      Assert.assertThat(stdOut,
               allOf(containsString(Career.MEDICINE.toString()), containsString(Career.MECHANICS.toString())));
   }

   @Test
   public void testUISelectManyWithEnum() throws Exception
   {
      test.waitForCompletion("foocommand --manyCareer ME", "foocommand --manyCareer M",
               5, TimeUnit.SECONDS);
      String stdOut = test.waitForCompletion(5, TimeUnit.SECONDS);
      Assert.assertThat(stdOut,
               allOf(containsString(Career.MEDICINE.toString()), containsString(Career.MECHANICS.toString())));
   }

   @Test
   public void testDisabledOptionsShouldNotBeDisplayed() throws Exception
   {
      test.waitForCompletion("foocommand --", "foocommand --", 5, TimeUnit.SECONDS);
      String stdOut = test.waitForCompletion(5, TimeUnit.SECONDS);
      Assert.assertThat(stdOut, not(containsString("--disabledOption")));
   }
}
