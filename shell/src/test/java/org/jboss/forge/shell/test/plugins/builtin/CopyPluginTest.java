/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

/**
 * Test for Copy Plugin
 * 
 * @author tremes@redhat.com
 * 
 */

public class CopyPluginTest extends AbstractShellTest
{

   @Test
   public void testCopyFileToFolder() throws Exception
   {

      String testFolder = "testFolder";
      String file = "copyFile";

      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("mkdir " + testFolder);
      shell.execute("touch " + file);

      Resource<?> fileToCopy = shell.getCurrentDirectory().getChild(file);
      assertTrue(fileToCopy.exists());

      shell.execute("cp " + file + " " + testFolder);
      assertTrue(fileToCopy.exists());

      shell.execute("cd " + testFolder);
      Resource<?> copy = shell.getCurrentDirectory().getChild(file);

      assertTrue(copy.exists());

   }

   @Test
   public void testCopyFileToNewFile() throws Exception
   {

      String file = "copyFile";
      String nonExisting = "newNoneExisting";

      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("touch " + file);

      Resource<?> fileToCopy = shell.getCurrentDirectory().getChild(file);
      assertTrue(fileToCopy.exists());

      shell.execute("cp " + file + " " + nonExisting);
      Resource<?> copy = shell.getCurrentDirectory().getChild(nonExisting);
      assertTrue(copy.exists());

   }

   @Test
   public void testCopyFileWithRewrite() throws Exception
   {

      String testFolder = "testFolder";
      String file = "copyFile";

      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("mkdir " + testFolder);
      shell.execute("touch " + file);
      shell.execute("cd " + testFolder);
      shell.execute("touch " + file);
      shell.execute("cd ..");

      FileResource<?> fileToCopy = (FileResource<?>) shell.getCurrentDirectory().getChild(file);
      assertTrue(fileToCopy.exists());

      shell.execute("cd " + testFolder);
      FileResource<?> copy = (FileResource<?>) shell.getCurrentDirectory().getChild(file);
      shell.execute("cp " + file + " " + testFolder);
      assertTrue(fileToCopy.exists());
      assertTrue(copy.exists());
      assertTrue(compareFiles(fileToCopy.getUnderlyingResourceObject(), copy.getUnderlyingResourceObject()));

   }

   @Test
   public void testCopyEmptyFolder() throws Exception
   {

      String testFolder = "testFolder";
      String newFolder = "newFolder";

      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("mkdir " + testFolder);

      Resource<?> dirToCopy = shell.getCurrentDirectory().getChildDirectory(testFolder);
      assertTrue(dirToCopy.exists());

      shell.execute("cp " + testFolder + " " + newFolder);
      Resource<?> copy = shell.getCurrentDirectory().getChild(newFolder);
      assertTrue(copy.exists());
      assertTrue(((DirectoryResource) copy).isDirectory());

   }

   @Test
   public void testCopyEmptyFolderToExisting() throws Exception
   {

      String testFolder = "testFolder";
      String newFolder = "newFolder";

      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("mkdir " + testFolder);
      shell.execute("mkdir " + newFolder);

      Resource<?> dirToCopy = shell.getCurrentDirectory().getChildDirectory(testFolder);
      assertTrue(dirToCopy.exists());

      shell.execute("cp " + testFolder + " " + newFolder);
      Resource<?> targetParent = shell.getCurrentDirectory().getChild(newFolder);
      Resource<?> copy = ((DirectoryResource) targetParent).getChildDirectory(testFolder);
      assertTrue(copy.exists());
      assertTrue(((DirectoryResource) copy).isDirectory());

   }

   @Test
   public void testCopyNonEmptyFolder() throws Exception
   {

      String testFolder = "testFolder";
      String newFolder = "newFolder";
      String subFolderA = "subFolder1";
      String subFolderB = "subFolder2";
      String fileA = "file1";
      String fileB = "file2";

      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("mkdir " + testFolder);
      shell.execute("cd " + testFolder);
      shell.execute("mkdir " + subFolderA);
      shell.execute("mkdir " + subFolderB);
      shell.execute("touch " + fileA);
      shell.execute("cd " + subFolderA);
      shell.execute("touch " + fileB);
      shell.execute("cd ..");
      shell.execute("cd ..");

      Resource<?> dirToCopy = shell.getCurrentDirectory().getChildDirectory(testFolder);
      assertTrue(dirToCopy.exists());
      FileResource<?> file1 = (FileResource<?>) dirToCopy.getChild(fileA);
      assertTrue(file1.exists());
      Resource<?> subFolder1 = ((DirectoryResource) dirToCopy).getChildDirectory(subFolderA);
      Resource<?> file2 = ((DirectoryResource) subFolder1).getChild(fileB);
      assertTrue(file2.exists());

      assertTrue(((DirectoryResource) subFolder1).isDirectory());
      assertTrue(((DirectoryResource) subFolder1).exists());

      Resource<?> subFolder2 = ((DirectoryResource) dirToCopy).getChildDirectory(subFolderB);
      assertTrue(((DirectoryResource) subFolder2).isDirectory());
      assertTrue(((DirectoryResource) subFolder2).exists());

      shell.execute("cp " + testFolder + " " + newFolder);

      Resource<?> copy = shell.getCurrentDirectory().getChildDirectory(newFolder);
      assertTrue(copy.exists());
      assertTrue(((DirectoryResource) copy).isDirectory());

      FileResource<?> copyfile1 = (FileResource<?>) copy.getChild(fileA);
      assertTrue(copyfile1.exists());

      DirectoryResource copySubFolder1 = ((DirectoryResource) copy).getChildDirectory(subFolderA);
      assertTrue(copySubFolder1.exists());
      Resource<?> copyfile2 = ((DirectoryResource) copySubFolder1).getChild(fileB);
      assertTrue(copyfile2.exists());

      DirectoryResource copySubFolder2 = ((DirectoryResource) copy).getChildDirectory(subFolderA);
      assertTrue(copySubFolder2.exists());

   }

   @Test
   public void testCopyFolderToExistingFolder() throws Exception
   {

      String testFolder = "testFolder";
      String newFolder = "newFolder";
      String subFolderA = "subFolder1";
      String subFolderB = "subFolder2";
      String fileA = "file1";
      String fileB = "file2";

      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("mkdir " + testFolder);
      shell.execute("cd " + testFolder);
      shell.execute("mkdir " + subFolderA);
      shell.execute("mkdir " + subFolderB);
      shell.execute("touch " + fileA);
      shell.execute("cd " + subFolderA);
      shell.execute("touch " + fileB);
      shell.execute("cd ..");
      shell.execute("cd ..");
      shell.execute("mkdir " + newFolder);

      Resource<?> dirToCopy = shell.getCurrentDirectory().getChildDirectory(testFolder);
      assertTrue(dirToCopy.exists());
      FileResource<?> file1 = (FileResource<?>) dirToCopy.getChild(fileA);
      assertTrue(file1.exists());
      Resource<?> subFolder1 = ((DirectoryResource) dirToCopy).getChildDirectory(subFolderA);
      FileResource<?> file2 = (FileResource<?>) subFolder1.getChild(fileB);
      assertTrue(file2.exists());

      assertTrue(((DirectoryResource) subFolder1).isDirectory());
      assertTrue(((DirectoryResource) subFolder1).exists());

      Resource<?> subFolder2 = ((DirectoryResource) dirToCopy).getChildDirectory(subFolderB);
      assertTrue(((DirectoryResource) subFolder2).isDirectory());
      assertTrue(((DirectoryResource) subFolder2).exists());

      shell.execute("cp " + testFolder + " " + newFolder);

      Resource<?> targetParent = shell.getCurrentDirectory().getChildDirectory(newFolder);
      assertTrue(targetParent.exists());
      assertTrue(((DirectoryResource) targetParent).isDirectory());

      Resource<?> copy = ((DirectoryResource) targetParent).getChildDirectory(testFolder);
      assertTrue(copy.exists());
      assertTrue(((DirectoryResource) copy).isDirectory());

      FileResource<?> copyfile1 = (FileResource<?>) copy.getChild(fileA);
      assertTrue(copyfile1.exists());
      assertTrue(compareFiles(copyfile1.getUnderlyingResourceObject(), file1.getUnderlyingResourceObject()));

      DirectoryResource copySubFolder1 = ((DirectoryResource) copy).getChildDirectory(subFolderA);
      assertTrue(copySubFolder1.exists());
      FileResource<?> copyfile2 = (FileResource<?>) copySubFolder1.getChild(fileB);
      assertTrue(copyfile2.exists());
      assertTrue(compareFiles(copyfile2.getUnderlyingResourceObject(), file2.getUnderlyingResourceObject()));

      DirectoryResource copySubFolder2 = ((DirectoryResource) copy).getChildDirectory(subFolderB);
      assertTrue(copySubFolder2.exists());
   }

   private boolean compareFiles(File f1, File f2)
   {
      boolean ret = false;
      FileInputStream fis1 = null;
      FileInputStream fis2 = null;
      try
      {
         fis1 = new FileInputStream(f1);
         fis2 = new FileInputStream(f2);

         String fis1Str = Streams.toString(fis1);
         String fis2Str = Streams.toString(fis2);
         ret = fis1Str.equals(fis2Str);
      }
      catch (IOException e)
      {
         ret = false;
      }
      finally
      {
         Streams.closeQuietly(fis1);
         Streams.closeQuietly(fis2);
      }
      return ret;
   }
}
