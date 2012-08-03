package org.jboss.forge.shell.test.plugins.builtin;

import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import static org.junit.Assert.*;

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

}
