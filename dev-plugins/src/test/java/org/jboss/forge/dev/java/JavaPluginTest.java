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
package org.jboss.forge.dev.java;

import static junit.framework.Assert.assertNotNull;

import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.util.Packages;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Before;
import org.junit.Test;

public class JavaPluginTest extends AbstractShellTest
{

   @Before
   public void initializeTest() throws Exception
   {
      initializeJavaProject();
   }

   @Test
   public void testCreateJavaClass() throws Exception
   {
      getShell().execute(
               "java new-class --package org.jboss.forge.test.classes \"public class TestingClassCreation {}\"");
      getShell().execute("build");
      JavaClass javaClass = (JavaClass) getProject().getFacet(JavaSourceFacet.class)
               .getJavaResource(Packages.toFileSyntax("org.jboss.forge.test.classes.TestingClassCreation"))
               .getJavaSource();
      assertNotNull(javaClass);
   }

   @Test
   public void testCreateJavaField() throws Exception
   {
      getShell().execute(
               "java new-class --package org.jboss.forge.test.classes \"public class TestingFieldCreation {}\"");
      getShell().execute("java new-field \"private int testing;\"");
      getShell().execute("ls");
      getShell().execute("build");
      JavaClass javaClass = (JavaClass) getProject().getFacet(JavaSourceFacet.class)
               .getJavaResource(Packages.toFileSyntax("org.jboss.forge.test.classes.TestingFieldCreation"))
               .getJavaSource();
      Field<JavaClass> field = javaClass.getField("testing");
      assertNotNull(field);
   }

   @Test
   public void testCreateJavaMethod() throws Exception
   {
      getShell().execute(
               "java new-class --package org.jboss.forge.test.classes \"public class TestingMethodCreation {}\"");
      getShell().execute("java new-method \"public void testing(){}\"");
      getShell().execute("ls");
      getShell().execute("build");
      JavaClass javaClass = (JavaClass) getProject().getFacet(JavaSourceFacet.class)
               .getJavaResource(Packages.toFileSyntax("org.jboss.forge.test.classes.TestingMethodCreation"))
               .getJavaSource();
      Method<JavaClass> method = javaClass.getMethod("testing");
      assertNotNull(method);
   }

   @Test
   public void testCreateEnumType() throws Exception
   {
      getShell().execute("java new-enum-type --package org.jboss.forge.test.types \"public enum TestingEnumTypeCreation{}\"");
      getShell().execute("ls");
      getShell().execute("build");
      JavaEnum javaEnum = (JavaEnum) getProject().getFacet(JavaSourceFacet.class).getEnumTypeResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingEnumTypeCreation")).getJavaSource();
      assertNotNull(javaEnum);
   }

   @Test
   public void testCreateEnumConst() throws Exception
   {
      getShell().execute("java new-enum-type --package org.jboss.forge.test.types \"public enum TestingEnumTypeCreation{}\"");
      getShell().execute("java new-enum-const \"A\"");
      getShell().execute("ls");
      getShell().execute("build");
      JavaEnum javaEnum = (JavaEnum) getProject().getFacet(JavaSourceFacet.class).getEnumTypeResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingEnumTypeCreation")).getJavaSource();
      EnumConstant<JavaEnum> enumConst = javaEnum.getEnumConstant("A");
      assertNotNull(enumConst);
   }
}
