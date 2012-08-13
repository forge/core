/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
