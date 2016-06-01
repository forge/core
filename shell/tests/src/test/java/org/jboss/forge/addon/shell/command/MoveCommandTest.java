/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="danielsoro@gmail.com">Daniel Cunha (soro)</a>
 */
@RunWith(Arquillian.class)
public class MoveCommandTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML();
   }

   @Inject
   ShellTest shell;

   @After
   public void tearDown() throws Exception
   {
      shell.close();
   }

   @Test
   public void testMoveFileToFolder() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      String testFolder = "testFolder";
      String file = "moveFile";
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("touch " + file, 15, TimeUnit.SECONDS);

      File testFolderFile = new File(tmpDir, testFolder);
      File fileSource = new File(tmpDir, file);
      File fileTarget = new File(testFolderFile, file);

      Assert.assertTrue(fileSource.exists());
      Assert.assertFalse(fileTarget.exists());
      shell.execute("mv " + file + " " + testFolder, 15, TimeUnit.SECONDS);
      Assert.assertTrue(fileTarget.exists());
      Assert.assertFalse(fileSource.exists());
   }

   @Test
   public void testMoveFileToNewFile() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();

      String file = "moveFile";
      String nonExisting = "newNoneExisting";

      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);
      shell.execute("touch " + file, 15, TimeUnit.SECONDS);

      File fileSource = new File(tmpDir, file);
      File fileTarget = new File(tmpDir, nonExisting);

      Assert.assertTrue(fileSource.exists());
      Assert.assertFalse(fileTarget.exists());

      shell.execute("mv " + file + " " + nonExisting, 15, TimeUnit.SECONDS);
      Assert.assertFalse(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
   }

   @Test
   public void testMoveFileWithRelativePathToNewFile() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String file = "moveFile";
      String nonExisting = "newNoneExisting";

      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      String relativePath = testFolder + File.separator + file;
      shell.execute("touch " + relativePath, 15, TimeUnit.SECONDS);

      File fileSource = new File(tmpDir, relativePath);
      File fileTarget = new File(tmpDir, nonExisting);

      Assert.assertTrue(fileSource.exists());
      Assert.assertFalse(fileTarget.exists());
      shell.execute("mv " + relativePath + " " + nonExisting, 15, TimeUnit.SECONDS);
      Assert.assertFalse(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
   }

   @Test
   public void testMoveFileWithRelativePathToExistingFolder() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String targetFolder = "targetFolder";
      String file = "moveFile";

      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + targetFolder, 15, TimeUnit.SECONDS);
      String relativePath = testFolder.concat(File.separator).concat(file);
      shell.execute("touch " + relativePath, 15, TimeUnit.SECONDS);

      File fileSource = new File(tmpDir, relativePath);
      File fileTarget = new File(new File(tmpDir, targetFolder), file);

      Assert.assertTrue(fileSource.exists());
      Assert.assertFalse(fileTarget.exists());
      shell.execute("mv " + relativePath + " " + targetFolder, 15, TimeUnit.SECONDS);
      Assert.assertFalse(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
   }

   @Test
   public void testMovePathToFile() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String file = "moveFile";

      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("touch " + file, 15, TimeUnit.SECONDS);

      Result execute = shell.execute("mv " + testFolder + " " + file, 15, TimeUnit.SECONDS);
      Assert.assertTrue(execute instanceof Failed);
   }
}
