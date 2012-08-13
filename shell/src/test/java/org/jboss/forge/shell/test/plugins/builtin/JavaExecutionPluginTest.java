/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Scanner;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class JavaExecutionPluginTest extends AbstractShellTest
{
   private Shell shell;
   private String projectPath;
   private File testFile;

   @Override
   @Before
   public void beforeTest() throws Exception
   {
      super.beforeTest();
      initializeJavaProject();
      shell = getShell();

      projectPath = getProject().getProjectRoot().getUnderlyingResourceObject().getAbsolutePath();
      File classesDir = new File(projectPath + "/src/main/java/test");
      classesDir.mkdirs();
      InputStream resourceAsStream = this.getClass().getResourceAsStream("/test/TestClass.java");
      FileOutputStream out = new FileOutputStream(classesDir + "/TestClass.java");
      int b;
      while ((b = resourceAsStream.read()) != -1)
      {
         out.write(b);
      }

      out.close();
      resourceAsStream.close();

      testFile = new File(projectPath + "/test.txt");
   }

   @Test
   public void testExecuteClass() throws Exception
   {
      assumeThat(testFile.exists(), is(false));

      shell.execute("project install-facet forge.maven.JavaExecutionFacet");
      shell.execute("execute-java --class test.TestClass");

      assertTrue(testFile.exists());
   }

   @Test
   public void testExecuteClassWithSingleArgument() throws Exception
   {

      shell.execute("project install-facet forge.maven.JavaExecutionFacet");
      shell.execute("execute-java --class test.TestClass --arguments a");

      Scanner scanner = new Scanner(testFile);
      String s = scanner.nextLine();
      scanner.close();
      assertEquals("a", s);
   }

   @Test
   public void testExecuteClassWithMultipleArguments() throws Exception
   {

      shell.execute("project install-facet forge.maven.JavaExecutionFacet");
      shell.execute("execute-java --class test.TestClass --arguments \"a b c\"");

      Scanner scanner = new Scanner(testFile);
      String s = scanner.nextLine();
      scanner.close();
      assertEquals("a b c", s);
   }

   @After
   public void removeTestFile()
   {
      File file = new File(projectPath + "/test.txt");
      if (file.exists())
      {
         file.delete();
      }
   }
}
