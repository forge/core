/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

/**
 * @author Jose Donizetti
 */
public class MovePluginTest extends AbstractShellTest
{
   @Test
   public void testMoveFileRelative() throws Exception
   {
      initializeJavaProject();
      Shell shell = getShell();

      shell.execute("mkdir moveFolder");
      shell.execute("touch fileToMove.txt");

      Resource<?> fileToMove = shell.getCurrentDirectory().getChild("fileToMove.txt");
      assertTrue(fileToMove.exists());

      shell.execute("mv fileToMove.txt moveFolder");

      assertFalse(fileToMove.exists());

      shell.execute("cd moveFolder");
      Resource<?> movedFile = shell.getCurrentDirectory().getChild("fileToMove.txt");

      assertTrue(movedFile.exists());
   }

   @Test
   public void testMoveFile() throws Exception
   {
      initializeJavaProject();
      Shell shell = getShell();

      shell.execute("mkdir moveFolder");
      shell.execute("touch fileToMove.txt");

      Resource<?> fileToMove = shell.getCurrentDirectory().getChild("fileToMove.txt");
      assertTrue(fileToMove.exists());

      shell.execute("mv fileToMove.txt moveFolder");

      assertFalse(fileToMove.exists());

      shell.execute("cd moveFolder");
      Resource<?> movedFile = shell.getCurrentDirectory().getChild("fileToMove.txt");

      assertTrue(movedFile.exists());
   }

   @Test
   public void testMoveFileInsideFolder() throws Exception
   {
      initializeJavaProject();
      Shell shell = getShell();

      shell.execute("mkdir moveFolder");
      shell.execute("mkdir moveFolder/insideFolder");
      shell.execute("touch fileToMove.txt");

      Resource<?> fileToMove = shell.getCurrentDirectory().getChild("fileToMove.txt");
      assertTrue(fileToMove.exists());

      shell.execute("mv fileToMove.txt moveFolder/insideFolder");

      assertFalse(fileToMove.exists());

      shell.execute("cd moveFolder/insideFolder");
      Resource<?> movedFile = shell.getCurrentDirectory().getChild("fileToMove.txt");

      assertTrue(movedFile.exists());
   }

   @Test
   public void testMoveFileOutsideFolder() throws Exception
   {
      initializeJavaProject();
      Shell shell = getShell();

      shell.execute("mkdir moveFolder");
      shell.execute("cd moveFolder");
      shell.execute("touch fileToMove.txt");

      DirectoryResource moveFolder = shell.getCurrentDirectory();
      Resource<?> fileToMove = moveFolder.getChild("fileToMove.txt");
      assertTrue(fileToMove.exists());

      shell.execute("mv fileToMove.txt ../");

      assertFalse(fileToMove.exists());

      shell.execute("cd ..");
      Resource<?> movedFile = shell.getCurrentDirectory().getChild("fileToMove.txt");

      assertTrue(movedFile.exists());
   }

   @Test
   public void testMoveFolderOutsideOtherFolder() throws Exception
   {
      initializeJavaProject();
      Shell shell = getShell();

      shell.execute("mkdir folder");
      shell.execute("cd folder");
      shell.execute("mkdir insideFolder");

      DirectoryResource folder = shell.getCurrentDirectory();

      Resource<?> insideFolder = folder.getChild("insideFolder");
      assertTrue(insideFolder.exists());

      shell.execute("mv insideFolder ../");

      assertFalse(insideFolder.exists());

      shell.execute("cd ../");
      Resource<?> movedFolder = shell.getCurrentDirectory().getChild("insideFolder");

      assertTrue(movedFolder.exists());
   }

   @Test
   public void testRenameFile() throws Exception
   {
      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("touch fileToRename.txt");

      Resource<?> fileToRename = shell.getCurrentDirectory().getChild("fileToRename.txt");
      Resource<?> renamedFile = shell.getCurrentDirectory().getChild("renamedFile.txt");

      shell.execute("mv fileToRename.txt renamedFile.txt");

      assertFalse(fileToRename.exists());
      assertTrue(renamedFile.exists());
   }

   @Test
   public void testMoveDirectory() throws Exception
   {
      initializeJavaProject();
      Shell shell = getShell();

      shell.execute("mkdir folder");
      shell.execute("mkdir folderToMove");

      Resource<?> folder = shell.getCurrentDirectory().getChild("folder");
      assertTrue(folder.exists());

      shell.execute("mv folder folderToMove");

      assertFalse(folder.exists());

      shell.execute("cd folderToMove");
      Resource<?> movedFolder = shell.getCurrentDirectory().getChild("folder");

      assertTrue(movedFolder.exists());
   }

   @Test
   public void testRenameDirectory() throws Exception
   {
      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("mkdir directoryToRename");

      Resource<?> directoryToRename = shell.getCurrentDirectory().getChild("directoryToRename");
      Resource<?> renamedDirectory = shell.getCurrentDirectory().getChild("renamedDirectory");

      shell.execute("mv directoryToRename renamedDirectory");

      assertFalse(directoryToRename.exists());
      assertTrue(renamedDirectory.exists());
   }
   
   @Test
   public void testMoveFileWithRelativePath() throws Exception
   {

	  String testFolder = "testFolder"; 
      String file = "moveFile";
      String nonExisting = "newNoneExisting";

      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("mkdir " + testFolder);
      String relativePath =  testFolder.concat("/").concat(file);
      shell.execute("touch " + relativePath);

      Resource<?> fileToMove = shell.getCurrentDirectory().getChild(testFolder).getChild(file);
      assertTrue(fileToMove.exists());

      shell.execute("mv " + relativePath + " " + nonExisting);
      Resource<?> move = shell.getCurrentDirectory().getChild(nonExisting);
      assertTrue(move.exists());
      assertFalse(fileToMove.exists());

   }
   
   @Test
   public void testMoveFolderToNonExistingFolder() throws Exception
   {

      String testFolder = "testFolder";
      String newFolder = "newFolder";
      String nonExisting = "nonExisting";
      String subFolderA = "subFolder1";
      String subFolderB = "subFolder2";
      String fileA = "file1";
      String fileB = "file2";
      String relativePath = newFolder.concat("/").concat(nonExisting);

      initializeJavaProject();
      Shell shell = getShell();
      shell.execute("mkdir " + testFolder);
      shell.execute("mkdir " + newFolder);
      shell.execute("cd " + testFolder);
      shell.execute("mkdir " + subFolderA);
      shell.execute("mkdir " + subFolderB);
      shell.execute("touch " + fileA);
      shell.execute("cd " + subFolderA);
      shell.execute("touch " + fileB);
      shell.execute("cd ..");
      shell.execute("cd ..");
    
      Resource<?> dirToMove = shell.getCurrentDirectory().getChildDirectory(testFolder);
      assertTrue(dirToMove.exists());
      FileResource<?> file1 = (FileResource<?>) dirToMove.getChild(fileA);
      assertTrue(file1.exists());
      Resource<?> subFolder1 = ((DirectoryResource) dirToMove).getChildDirectory(subFolderA);
      FileResource<?> file2 = (FileResource<?>) subFolder1.getChild(fileB);
      assertTrue(file2.exists());

      assertTrue(((DirectoryResource) subFolder1).isDirectory());
      assertTrue(((DirectoryResource) subFolder1).exists());

      Resource<?> subFolder2 = ((DirectoryResource) dirToMove).getChildDirectory(subFolderB);
      assertTrue(((DirectoryResource) subFolder2).isDirectory());
      assertTrue(((DirectoryResource) subFolder2).exists());

      shell.execute("mv " + testFolder + " " + relativePath);

      Resource<?> targetParent = shell.getCurrentDirectory().getChildDirectory(newFolder);
      assertTrue(targetParent.exists());
      assertTrue(((DirectoryResource) targetParent).isDirectory());

      Resource<?> move = ((DirectoryResource) targetParent).getChildDirectory(nonExisting);
      assertTrue(move.exists());
      assertTrue(((DirectoryResource) move).isDirectory());

      FileResource<?> movedfile1 = (FileResource<?>) move.getChild(fileA);
      assertTrue(movedfile1.exists());

      DirectoryResource movedSubFolder1 = ((DirectoryResource) move).getChildDirectory(subFolderA);
      assertTrue(movedSubFolder1.exists());
      FileResource<?> movedfile2 = (FileResource<?>) movedSubFolder1.getChild(fileB);
      assertTrue(movedfile2.exists());

      DirectoryResource movedSubFolder2 = ((DirectoryResource) move).getChildDirectory(subFolderB);
      assertTrue(movedSubFolder2.exists());
      assertFalse(dirToMove.exists());
   }

}
