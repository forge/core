/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script.ui;

import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class RunCommandTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addClasses(ThrowExceptionCommand.class)
               .addAsServiceProvider(Service.class, RunCommandTest.class, ThrowExceptionCommand.class);
   }

   private static final int COMMAND_TIMEOUT = 5000;

   private ShellTest shellTest;
   private ResourceFactory resourceFactory;

   @Before
   public void setUp() throws Exception
   {
      shellTest = SimpleContainer.getServices(getClass().getClassLoader(), ShellTest.class).get();
      resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
      shellTest.clearScreen();
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void testRunScriptSingleLine() throws Exception
   {
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      shellTest.getShell().setCurrentResource(temp);

      FileResource<?> script = (FileResource<?>) temp.getChild("script.fsh");
      script.setContents("touch foo.txt");

      Resource<?> child = temp.getChild("foo.txt");
      Assert.assertFalse(child.exists());

      Result result = shellTest.execute("run script.fsh", COMMAND_TIMEOUT, TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);

      Assert.assertTrue(child.exists());
      child.delete();
   }

   @Test
   public void testRunScriptMultiLine() throws Exception
   {
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      shellTest.getShell().setCurrentResource(temp);

      FileResource<?> script = (FileResource<?>) temp.getChild("script.fsh");
      script.setContents("touch foo.txt\n"
               + "touch foo2.txt");

      Resource<?> child = temp.getChild("foo.txt");
      Resource<?> child2 = temp.getChild("foo2.txt");
      Assert.assertFalse(child.exists());
      Assert.assertFalse(child2.exists());

      Result result = shellTest.execute("run script.fsh", COMMAND_TIMEOUT, TimeUnit.MINUTES);
      Assert.assertFalse(result instanceof Failed);

      Assert.assertTrue(child.exists());
      Assert.assertTrue(child2.exists());
      child.delete();
      child2.delete();
   }

   @Test
   public void testRunScriptMultiLineWithRandomWhitespace() throws Exception
   {
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      shellTest.getShell().setCurrentResource(temp);

      FileResource<?> script = (FileResource<?>) temp.getChild("script.fsh");
      script.setContents("\ntouch foo.txt\n\n\n\t\n"
               + "touch foo2.txt\r\n");

      Resource<?> child = temp.getChild("foo.txt");
      Resource<?> child2 = temp.getChild("foo2.txt");
      Assert.assertFalse(child.exists());
      Assert.assertFalse(child2.exists());

      Result result = shellTest.execute("run script.fsh", COMMAND_TIMEOUT, TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);

      Assert.assertTrue(child.exists());
      Assert.assertTrue(child2.exists());
      child.delete();
      child2.delete();
   }

   @Test
   public void testRunScriptFailure() throws Exception
   {
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      shellTest.getShell().setCurrentResource(temp);

      FileResource<?> script = (FileResource<?>) temp.getChild("script.fsh");
      script.setContents("throw-exception");

      Result result = shellTest.execute("run script.fsh", COMMAND_TIMEOUT, TimeUnit.SECONDS);
      Assert.assertTrue(result instanceof Failed);
   }

   @Test
   public void testRunScriptMultiLineWithComments() throws Exception
   {
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      shellTest.getShell().setCurrentResource(temp);

      FileResource<?> script = (FileResource<?>) temp.getChild("script.fsh");
      script.setContents("\ntouch foo.txt\n\n\n\t\n"
               + "#touch foo2.txt\r\n");

      Resource<?> child = temp.getChild("foo.txt");
      Resource<?> child2 = temp.getChild("foo2.txt");
      Assert.assertFalse(child.exists());
      Assert.assertFalse(child2.exists());

      Result result = shellTest.execute("run script.fsh", COMMAND_TIMEOUT, TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);

      Assert.assertTrue(child.exists());
      Assert.assertFalse(child2.exists());
      child.delete();
      child2.delete();
   }

   @Test
   public void testRunCommandLinux() throws Exception
   {
      Assume.assumeTrue(OperatingSystemUtils.isLinux());
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      FileResource<?> fileResource = (FileResource<?>) temp.getChild("file.txt");
      fileResource.createNewFile();
      fileResource.deleteOnExit();
      Result result = shellTest.execute("run -c \"ls " + temp.getFullyQualifiedName() + "\"", COMMAND_TIMEOUT,
               TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString("file.txt"));
   }

   @Test
   public void testKeepShellContext() throws Exception
   {
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      Shell shell = shellTest.getShell();
      shell.setCurrentResource(temp);

      FileResource<?> script = (FileResource<?>) temp.getChild("script.fsh");
      script.setContents("mkdir foo\ncd foo");
      Result result = shellTest.execute("run script.fsh", COMMAND_TIMEOUT, TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);
      Resource<?> child = temp.getChild("foo");
      Assert.assertTrue(child.exists());
      Assert.assertEquals(child, shell.getCurrentResource());
   }

   @Test
   public void testRunScriptFailureWithUnknownCommands() throws Exception
   {
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      shellTest.getShell().setCurrentResource(temp);

      FileResource<?> script = (FileResource<?>) temp.getChild("script.fsh");
      script.setContents("touch foo.txt\nblah");

      Result result = shellTest.execute("run script.fsh", COMMAND_TIMEOUT, TimeUnit.SECONDS);
      Assert.assertTrue(result instanceof Failed);
   }

   @Test
   public void testRunScriptMultipleLines() throws Exception
   {
      DirectoryResource temp = (DirectoryResource) resourceFactory.create(OperatingSystemUtils.createTempDir());
      temp.deleteOnExit();
      shellTest.getShell().setCurrentResource(temp);

      FileResource<?> script = (FileResource<?>) temp.getChild("script.fsh");
      script.setContents("touch \\\n\t foo.txt");
      Result result = shellTest.execute("run script.fsh", COMMAND_TIMEOUT, TimeUnit.SECONDS);
      Assert.assertFalse(result instanceof Failed);
      Resource<?> child = temp.getChild("foo.txt");
      Assert.assertTrue(child.exists());

   }
}
