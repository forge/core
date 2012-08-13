/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.util.OSUtils;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ChangeDirectoryPluginTest extends AbstractShellTest
{
   @Inject
   private ResourceFactory factory;

   @Test
   public void testTildeAliasesHomeDir() throws Exception
   {

      DirectoryResource home = new DirectoryResource(factory, OSUtils.getUserHomeDir());

      Shell shell = getShell();
      Resource<?> currentDirectory = shell.getCurrentResource();
      assertNotSame(home.getFullyQualifiedName(), currentDirectory.getFullyQualifiedName());

      shell.execute("cd ~");

      currentDirectory = shell.getCurrentResource();
      assertEquals(home.getFullyQualifiedName(), currentDirectory.getFullyQualifiedName());
   }

   @Test
   public void testDoubleTildeAliasesProjectRoot() throws Exception
   {
      DirectoryResource home = new DirectoryResource(factory, OSUtils.getUserHomeDir());

      initializeJavaProject();
      Shell shell = getShell();
      Resource<?> rootDir = shell.getCurrentResource();
      shell.execute("cd src/main/java");

      Resource<?> currentDirectory = shell.getCurrentResource();
      assertNotSame(rootDir.getFullyQualifiedName(), currentDirectory.getFullyQualifiedName());

      shell.execute("cd ~~");

      currentDirectory = shell.getCurrentResource();
      assertNotSame(home.getFullyQualifiedName(), currentDirectory.getFullyQualifiedName());
      assertEquals(rootDir.getFullyQualifiedName(), currentDirectory.getFullyQualifiedName());
   }

   @Test
   public void testDotMeansSameDirectory() throws Exception
   {
      Shell shell = getShell();
      shell.execute("cd ~");
      Resource<?> currentDirectory = shell.getCurrentResource();

      shell.execute("cd .");

      Resource<?> newDir = shell.getCurrentResource();
      assertEquals(currentDirectory.getFullyQualifiedName(), newDir.getFullyQualifiedName());
   }

   @Test
   public void testDotMeansSameDirectoryWithTrailingSlash() throws Exception
   {
      Shell shell = getShell();
      shell.execute("cd ~");
      Resource<?> currentDirectory = shell.getCurrentResource();

      shell.execute("cd ." + File.separator);

      Resource<?> newDir = shell.getCurrentResource();
      assertEquals(currentDirectory.getFullyQualifiedName(), newDir.getFullyQualifiedName());
   }

   @Test
   public void testDoubleDotMeansParentDirectory() throws Exception
   {
      Shell shell = getShell();
      shell.execute("cd ~");

      Resource<?> parentDir = shell.getCurrentResource().getParent();

      shell.execute("cd ..\\");

      Resource<?> newDir = shell.getCurrentResource();
      assertEquals(newDir.getFullyQualifiedName(), parentDir.getFullyQualifiedName());
   }

   @Test
   public void testReturnToLastDirectory() throws Exception
   {
      Shell shell = getShell();
      shell.execute("cd ~");

      Resource<?> base = shell.getCurrentResource();

      shell.execute("cd ../");
      shell.execute("cd -");

      Resource<?> newDir = shell.getCurrentResource();
      assertEquals(base, newDir);
   }

   @Test
   public void testAbsolutePath() throws Exception
   {
      Shell shell = getShell();
      shell.execute("cd ~");

      Resource<?> currentDirectory = shell.getCurrentResource();
      Resource<?> parent = currentDirectory.getParent();

      String parentPath = parent.getFullyQualifiedName();

      shell.execute("cd '" + parentPath + "'");

      Resource<?> newDir = shell.getCurrentResource();
      assertEquals(newDir.getFullyQualifiedName(), parent.getFullyQualifiedName());
   }

}
