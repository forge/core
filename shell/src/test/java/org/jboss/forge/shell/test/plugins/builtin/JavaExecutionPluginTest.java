/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.shell.test.plugins.builtin;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

public class JavaExecutionPluginTest extends AbstractShellTest
{
   private Shell shell;
   private String projectPath;
   private File testFile;

   @Override
   @Before
   public void beforeTest() throws IOException
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
   public void testExecuteClass()
   {
      assumeThat(testFile.exists(), is(false));

      shell.execute("project install-facet forge.maven.JavaExecutionFacet");
      shell.execute("execute-java --class test.TestClass");

      assertTrue(testFile.exists());
   }


   @Test
   public void testExecuteClassWithSingleArgument() throws FileNotFoundException
   {

      shell.execute("project install-facet forge.maven.JavaExecutionFacet");
      shell.execute("execute-java --class test.TestClass --arguments a");

      Scanner scanner = new Scanner(testFile);
      String s = scanner.nextLine();
      assertThat(s, is("a"));
   }
   @Test
   public void testExecuteClassWithMultipleArguments() throws FileNotFoundException
   {

      shell.execute("project install-facet forge.maven.JavaExecutionFacet");
      shell.execute("execute-java --class test.TestClass --arguments \"a b c\"");

      Scanner scanner = new Scanner(testFile);
      String s = scanner.nextLine();
      assertThat(s, is("a b c"));
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
