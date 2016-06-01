/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.mock.command.ResourceTestCommand;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ShellResourceTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")

   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addClass(ResourceTestCommand.class);

      return archive;
   }

   @Inject
   private ShellTest shellTest;

   @Inject
   private ResourceFactory resourceFactory;

   @Before
   public void setUp() throws Exception
   {
      shellTest.clearScreen();
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test(timeout = 10000)
   public void testPwdCommand() throws TimeoutException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      FileResource<?> tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result pwdResult = shellTest.execute("pwd", 10, TimeUnit.SECONDS);
      Assert.assertNotNull(pwdResult);
      Assert.assertThat(shellTest.getStdOut(), containsString(tempResource.getFullyQualifiedName()));
      tempDir.delete();
   }

   @SuppressWarnings("unchecked")
   @Test(timeout = 10000)
   public void testRmCommand() throws TimeoutException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      tempDir.deleteOnExit();
      FileResource<?> fileTxt = tempResource.getChildOfType(FileResource.class, "file.txt");
      fileTxt.createNewFile();
      fileTxt.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result pwdResult = shellTest.execute("rm -f file.txt", 10, TimeUnit.SECONDS);
      Assert.assertNotNull(pwdResult);
      Assert.assertFalse(fileTxt.exists());
   }

   @Test(timeout = 10000)
   public void testChangeDirCommandCompletion() throws TimeoutException
   {
      shellTest.waitForCompletion("cd ", "cd", 15, TimeUnit.SECONDS);
   }

   @Test(timeout = 10000)
   public void testChangeDirCommand() throws TimeoutException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      DirectoryResource childDirectory = tempResource.getChildDirectory("child");
      childDirectory.mkdir();
      childDirectory.deleteOnExit();
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result changeDirResult = shellTest.execute("cd child", 10, TimeUnit.SECONDS);
      Assert.assertNotNull(changeDirResult);
      Assert.assertEquals(childDirectory, shell.getCurrentResource());
      childDirectory.delete();
      tempDir.delete();
   }

   @Test(timeout = 10000)
   public void testChangeDirCommandFailed() throws TimeoutException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result changeDirResult = shellTest.execute("cd child", 10, TimeUnit.SECONDS);
      Assert.assertTrue(changeDirResult instanceof Failed);
      Assert.assertEquals("child: Child resource doesn't exist", changeDirResult.getMessage());
   }

   @Test(timeout = 10000)
   public void testChangeDirAbsolute() throws TimeoutException
   {
      File userHome = OperatingSystemUtils.getUserHomeDir();
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result changeDirResult = shellTest.execute("cd " + userHome.getAbsolutePath(), 10, TimeUnit.SECONDS);
      Assert.assertNotNull(changeDirResult);
      Assert.assertEquals(userHome, shell.getCurrentResource().getUnderlyingResourceObject());
   }

   @Test(timeout = 10000)
   public void testChangeDirEmpty() throws TimeoutException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result changeDirResult = shellTest.execute("cd", 10, TimeUnit.SECONDS);
      Assert.assertNotNull(changeDirResult);
      Assert.assertNull(changeDirResult.getMessage());
   }

   @Test
   public void testListDirCommand() throws TimeoutException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      DirectoryResource childDirectory = tempResource.getChildDirectory("child");
      childDirectory.mkdir();
      childDirectory.deleteOnExit();
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      shellTest.execute("ls", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), containsString("child"));
      childDirectory.delete();
      tempDir.delete();
   }

   @Test
   public void testListAllDirCommand() throws TimeoutException
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      DirectoryResource childDirectory = tempResource.getChildDirectory("child");
      childDirectory.mkdir();
      FileResource<?> afile = tempResource.getChild(".afile").reify(FileResource.class);
      afile.createNewFile();
      afile.deleteOnExit();
      childDirectory.deleteOnExit();
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);

      shellTest.execute("ls", 10, TimeUnit.SECONDS);
      Assert.assertNotNull(shellTest.getStdOut());
      Assert.assertThat(shellTest.getStdOut(), not(containsString(".afile")));

      shellTest.execute("ls -a", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), containsString("child"));
      Assert.assertThat(shellTest.getStdOut(), containsString(".afile"));

      shellTest.execute("ls --all", 10, TimeUnit.SECONDS);
      Assert.assertNotNull(shellTest.getStdOut());
      Assert.assertThat(shellTest.getStdOut(), containsString("child"));
      Assert.assertThat(shellTest.getStdOut(), containsString(".afile"));

      afile.delete();
      childDirectory.delete();
      tempDir.delete();
   }

   @Test(timeout = 10000)
   public void testResolveResources() throws Exception
   {
      String userHomePath = OperatingSystemUtils.getUserHomePath();
      shellTest.execute("resourcecommand --single-file-resource ~", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), containsString("Single File Resource: " + userHomePath));

      shellTest.clearScreen();

      shellTest.execute("resourcecommand --single-file-resource .", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), containsString("Single File Resource: "
               + shellTest.getShell().getCurrentResource()));
   }
}
