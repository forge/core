package org.jboss.forge.shell.plugins.builtin;

import static org.junit.Assert.*;

import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class MkdirPluginTest extends AbstractShellTest
{

   @Test(expected = RuntimeException.class)
   public void testMkdirOnExistentDirectory() throws Exception
   {
      initializeJavaProject();
      getShell().execute("mkdir test");
      assertTrue(getProject().getProjectRoot().getChild("test").exists());
      getShell().execute("mkdir test");
   }

   @Test
   public void testMkdirs() throws Exception
   {
      initializeJavaProject();
      getShell().execute("mkdir test/subFolder");
      assertTrue(getProject().getProjectRoot().getChild("test/subFolder").exists());
   }

   @Test
   public void testMkdir() throws Exception
   {
      initializeJavaProject();
      getShell().execute("mkdir test");
      assertTrue(getProject().getProjectRoot().getChild("test").exists());
   }

   @Test
   public void testMkdirRelative() throws Exception
   {
      initializeJavaProject();
      getShell().execute("mkdir folderOne");

      getShell().execute("cd folderOne");

      getShell().execute("mkdir ../folderTwo/subFolder");

      assertTrue(getProject().getProjectRoot().getChild("folderTwo/subFolder").exists());
   }


}
