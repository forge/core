/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CopyCommandTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:resources"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   ShellTest shell;

   @After
   public void tearDown() throws Exception
   {
      shell.close();
   }

   @Test
   public void testCopyFileToFolder() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      String testFolder = "testFolder";
      String file = "copyFile";
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("touch " + file, 15, TimeUnit.SECONDS);

      File testFolderFile = new File(tmpDir, testFolder);
      File fileSource = new File(tmpDir, file);
      File fileTarget = new File(testFolderFile, file);

      Assert.assertTrue(fileSource.exists());
      Assert.assertFalse(fileTarget.exists());
      shell.execute("cp " + file + " " + testFolder, 15, TimeUnit.SECONDS);
      Assert.assertTrue(fileTarget.exists());
      Assert.assertTrue(fileSource.exists());
   }

   @Test
   public void testCopyFileToNewFile() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();

      String file = "copyFile";
      String nonExisting = "newNoneExisting";

      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);
      shell.execute("touch " + file, 15, TimeUnit.SECONDS);

      File fileSource = new File(tmpDir, file);
      File fileTarget = new File(tmpDir, nonExisting);

      Assert.assertTrue(fileSource.exists());
      Assert.assertFalse(fileTarget.exists());

      shell.execute("cp " + file + " " + nonExisting, 15, TimeUnit.SECONDS);
      Assert.assertTrue(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
   }

   @Test
   public void testCopyFileWithRelativePathToNewFile() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String file = "copyFile";
      String nonExisting = "newNoneExisting";

      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      String relativePath = testFolder + File.separator + file;
      shell.execute("touch " + relativePath, 15, TimeUnit.SECONDS);

      File fileSource = new File(tmpDir, relativePath);
      File fileTarget = new File(tmpDir, nonExisting);

      Assert.assertTrue(fileSource.exists());
      Assert.assertFalse(fileTarget.exists());
      shell.execute("cp " + relativePath + " " + nonExisting, 15, TimeUnit.SECONDS);
      Assert.assertTrue(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
   }

   @Test
   public void testCopyFileWithRelativePathToExistingFolder() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String targetFolder = "targetFolder";
      String file = "copyFile";

      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + targetFolder, 15, TimeUnit.SECONDS);
      String relativePath = testFolder.concat(File.separator).concat(file);
      shell.execute("touch " + relativePath, 15, TimeUnit.SECONDS);

      File fileSource = new File(tmpDir, relativePath);
      File fileTarget = new File(new File(tmpDir, targetFolder), file);

      Assert.assertTrue(fileSource.exists());
      Assert.assertFalse(fileTarget.exists());
      shell.execute("cp " + relativePath + " " + targetFolder, 15, TimeUnit.SECONDS);
      Assert.assertTrue(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
   }

   @Test
   public void testCopyFileWithRewrite() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String file = "copyFile";

      shell.execute("touch " + file, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("cd " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("touch " + file, 15, TimeUnit.SECONDS);
      shell.execute("cd ..", 15, TimeUnit.SECONDS);

      File fileSource = new File(tmpDir, file);
      Files.write(fileSource.toPath(), "TEST".getBytes());
      File fileTarget = new File(new File(tmpDir, testFolder), file);
      Assert.assertTrue(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
      shell.execute("cp " + file + " " + testFolder, 5, TimeUnit.MINUTES);
      Assert.assertTrue(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
      Assert.assertTrue(compareFiles(fileSource, fileTarget));
   }

   @Test
   public void testCopyEmptyFolder() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String newFolder = "newFolder";

      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);

      File fileSource = new File(tmpDir, testFolder);
      File fileTarget = new File(tmpDir, newFolder);

      Assert.assertTrue(fileSource.exists());
      Assert.assertFalse(fileTarget.exists());
      shell.execute("cp " + testFolder + " " + newFolder, 15, TimeUnit.SECONDS);
      Assert.assertTrue(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
      Assert.assertTrue(fileTarget.isDirectory());
   }

   @Test
   public void testCopyEmptyFolderToExisting() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String newFolder = "newFolder";

      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + newFolder, 15, TimeUnit.SECONDS);

      File fileSource = new File(tmpDir, testFolder);
      File fileTarget = new File(tmpDir, newFolder);

      Assert.assertTrue(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
      Assert.assertTrue(fileTarget.isDirectory());
      shell.execute("cp " + testFolder + " " + newFolder, 15, TimeUnit.SECONDS);
      Assert.assertTrue(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
      Assert.assertTrue(fileTarget.isDirectory());
   }

   @Test
   public void testCopyNonEmptyFolder() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String newFolder = "newFolder";
      String subFolderA = "subFolder1";
      String subFolderB = "subFolder2";
      String fileA = "file1";
      String fileB = "file2";

      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("cd " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + subFolderA, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + subFolderB, 15, TimeUnit.SECONDS);
      shell.execute("touch " + fileA, 15, TimeUnit.SECONDS);
      shell.execute("cd " + subFolderA, 15, TimeUnit.SECONDS);
      shell.execute("touch " + fileB, 15, TimeUnit.SECONDS);
      shell.execute("cd ..", 15, TimeUnit.SECONDS);
      shell.execute("cd ..", 15, TimeUnit.SECONDS);

      File fileSource = new File(tmpDir, testFolder);
      File fileTarget = new File(tmpDir, newFolder);

      File copySubFolder1 = new File(fileTarget, subFolderA);
      File copySubFolder2 = new File(fileTarget, subFolderB);
      File copyFile1 = new File(fileTarget, fileA);
      File copyFile2 = new File(copySubFolder1, fileB);

      Assert.assertTrue(fileSource.exists());
      Assert.assertFalse(fileTarget.exists());
      Assert.assertFalse(copyFile1.exists());
      Assert.assertFalse(copySubFolder1.exists());
      Assert.assertFalse(copyFile2.exists());
      Assert.assertFalse(copySubFolder2.exists());

      shell.execute("cp " + testFolder + " " + newFolder, 15, TimeUnit.SECONDS);

      Assert.assertTrue(fileSource.exists());
      Assert.assertTrue(fileTarget.exists());
      Assert.assertTrue(fileTarget.isDirectory());

      Assert.assertTrue(copyFile1.exists());
      Assert.assertTrue(copySubFolder1.exists());
      Assert.assertTrue(copyFile2.exists());
      Assert.assertTrue(copySubFolder2.exists());
   }

   @Test
   public void testCopyFolderToExistingFolder() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String newFolder = "newFolder";
      String subFolderA = "subFolder1";
      String subFolderB = "subFolder2";
      String fileA = "file1";
      String fileB = "file2";

      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("cd " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + subFolderA, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + subFolderB, 15, TimeUnit.SECONDS);
      shell.execute("touch " + fileA, 15, TimeUnit.SECONDS);
      shell.execute("cd " + subFolderA, 15, TimeUnit.SECONDS);
      shell.execute("touch " + fileB, 15, TimeUnit.SECONDS);
      shell.execute("cd ..", 15, TimeUnit.SECONDS);
      shell.execute("cd ..", 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + newFolder, 15, TimeUnit.SECONDS);

      File dirToCopy = new File(tmpDir, testFolder);
      Assert.assertTrue(dirToCopy.exists());
      File file1 = new File(dirToCopy, fileA);
      Assert.assertTrue(file1.exists());
      File subFolder1 = new File(dirToCopy, subFolderA);
      File file2 = new File(subFolder1, fileB);
      Assert.assertTrue(file2.exists());

      Assert.assertTrue(subFolder1.isDirectory());
      Assert.assertTrue(subFolder1.exists());

      File subFolder2 = new File(dirToCopy, subFolderB);
      Assert.assertTrue(subFolder2.isDirectory());
      Assert.assertTrue(subFolder2.exists());

      shell.execute("cp " + testFolder + " " + newFolder, 15, TimeUnit.SECONDS);

      File targetParent = new File(tmpDir, newFolder);
      Assert.assertTrue(targetParent.exists());
      Assert.assertTrue(targetParent.isDirectory());

      File copy = new File(targetParent, testFolder);
      Assert.assertTrue(copy.exists());
      Assert.assertTrue(copy.isDirectory());

      File copyFile1 = new File(copy, fileA);
      Assert.assertTrue(copyFile1.exists());
      Assert.assertTrue(compareFiles(copyFile1, file1));

      File copySubFolder1 = new File(copy, subFolderA);
      Assert.assertTrue(copySubFolder1.exists());
      File copyfile2 = new File(copySubFolder1, fileB);
      Assert.assertTrue(copyfile2.exists());
      Assert.assertTrue(compareFiles(copyfile2, file2));

      File copySubFolder2 = new File(copy, subFolderB);
      Assert.assertTrue(copySubFolder2.exists());
   }

   @Test
   public void testCopyFolderToNonExistingFolder() throws Exception
   {
      File tmpDir = OperatingSystemUtils.createTempDir();
      shell.execute("cd " + tmpDir.getAbsolutePath(), 15, TimeUnit.SECONDS);

      String testFolder = "testFolder";
      String newFolder = "newFolder";
      String nonExisting = "nonExisting";
      String subFolderA = "subFolder1";
      String subFolderB = "subFolder2";
      String fileA = "file1";
      String fileB = "file2";
      String relativePath = newFolder.concat(File.separator).concat(nonExisting);

      shell.execute("mkdir " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + newFolder, 15, TimeUnit.SECONDS);
      shell.execute("cd " + testFolder, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + subFolderA, 15, TimeUnit.SECONDS);
      shell.execute("mkdir " + subFolderB, 15, TimeUnit.SECONDS);
      shell.execute("touch " + fileA, 15, TimeUnit.SECONDS);
      shell.execute("cd " + subFolderA, 15, TimeUnit.SECONDS);
      shell.execute("touch " + fileB, 15, TimeUnit.SECONDS);
      shell.execute("cd ..", 15, TimeUnit.SECONDS);
      shell.execute("cd ..", 15, TimeUnit.SECONDS);

      File dirToCopy = new File(tmpDir, testFolder);
      Assert.assertTrue(dirToCopy.exists());
      File file1 = new File(dirToCopy, fileA);
      Assert.assertTrue(file1.exists());

      File subFolder1 = new File(dirToCopy, subFolderA);
      File file2 = new File(subFolder1, fileB);
      Assert.assertTrue(file2.exists());

      Assert.assertTrue(subFolder1.isDirectory());
      Assert.assertTrue(subFolder1.exists());

      File subFolder2 = new File(dirToCopy, subFolderB);
      Assert.assertTrue(subFolder2.isDirectory());
      Assert.assertTrue(subFolder2.exists());

      shell.execute("cp " + testFolder + " " + relativePath, 15, TimeUnit.SECONDS);

      File targetParent = new File(tmpDir, newFolder);
      Assert.assertTrue(targetParent.exists());
      Assert.assertTrue(targetParent.isDirectory());

      File copy = new File(targetParent, nonExisting);
      Assert.assertTrue(copy.exists());
      Assert.assertTrue(copy.isDirectory());

      File copyfile1 = new File(copy, fileA);
      Assert.assertTrue(copyfile1.exists());
      Assert.assertTrue(compareFiles(copyfile1, file1));

      File copySubFolder1 = new File(copy, subFolderA);
      Assert.assertTrue(copySubFolder1.exists());
      File copyfile2 = new File(copySubFolder1, fileB);
      Assert.assertTrue(copyfile2.exists());
      Assert.assertTrue(compareFiles(copyfile2, file2));

      File copySubFolder2 = new File(copy, subFolderB);
      Assert.assertTrue(copySubFolder2.exists());
   }

   private boolean compareFiles(File f1, File f2)
   {
      boolean ret = false;
      try
      {
         byte[] contentsOne = Files.readAllBytes(f1.toPath());
         byte[] contentsTwo = Files.readAllBytes(f2.toPath());
         ret = Arrays.equals(contentsOne, contentsTwo);
      }
      catch (IOException e)
      {
         ret = false;
      }
      return ret;
   }
}
