/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import static org.junit.Assert.assertTrue;

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
