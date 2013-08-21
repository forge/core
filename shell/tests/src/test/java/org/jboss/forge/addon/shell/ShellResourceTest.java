/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
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
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell"),
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private ShellTest shellTest;

   @Inject
   private ResourceFactory resourceFactory;

   @Test
   public void testPwdCommand()
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      FileResource<?> tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result pwdResult = shellTest.execute("pwd");
      Assert.assertNotNull(pwdResult);
      Assert.assertEquals(tempResource.getFullyQualifiedName(), pwdResult.getMessage());
      tempDir.delete();
   }

   @Test
   public void testChangeDirCommand()
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      DirectoryResource childDirectory = tempResource.getChildDirectory("child");
      childDirectory.mkdir();
      childDirectory.deleteOnExit();
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result changeDirResult = shellTest.execute("cd child");
      Assert.assertNotNull(changeDirResult);
      Assert.assertEquals(childDirectory, shell.getCurrentResource());
      childDirectory.delete();
      tempDir.delete();
   }

   @Test
   public void testChangeDirCommandFailed()
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result changeDirResult = shellTest.execute("cd child");
      Assert.assertTrue(changeDirResult instanceof Failed);
      Assert.assertEquals("child: No such file or directory", changeDirResult.getMessage());
   }

   @Test
   public void testChangeDirAbsolute()
   {
      File userHome = OperatingSystemUtils.getUserHomeDir();
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result changeDirResult = shellTest.execute("cd " + userHome.getAbsolutePath());
      Assert.assertNotNull(changeDirResult);
      Assert.assertEquals(userHome, shell.getCurrentResource().getUnderlyingResourceObject());
   }

   @Test
   public void testChangeDirEmpty()
   {
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource tempResource = resourceFactory.create(tempDir).reify(DirectoryResource.class);
      tempDir.deleteOnExit();

      Shell shell = shellTest.getShell();
      shell.setCurrentResource(tempResource);
      Result changeDirResult = shellTest.execute("cd");
      Assert.assertNotNull(changeDirResult);
      Assert.assertNull(changeDirResult.getMessage());
   }

}
