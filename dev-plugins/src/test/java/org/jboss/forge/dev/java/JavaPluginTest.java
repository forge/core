/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.java;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jboss.forge.parser.java.AnnotationElement;
import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaAnnotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.parser.java.JavaSource;
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
   public void testCreateJavaInterface() throws Exception
   {
      getShell()
               .execute(
                        "java new-interface --package org.jboss.forge.test.interfaces \"public interface TestingInterfaceCreation {}\"");
      getShell().execute("build");
      JavaInterface javaInterface = (JavaInterface) getProject()
               .getFacet(JavaSourceFacet.class)
               .getJavaResource(
                        Packages.toFileSyntax("org.jboss.forge.test.interfaces.TestingInterfaceCreation"))
               .getJavaSource();
      assertNotNull(javaInterface);
   }

   @Test
   public void testCreateJavaMethodOnInterface() throws Exception
   {
      getShell()
               .execute(
                        "java new-interface --package org.jboss.forge.test.interfaces \"public interface TestingInterfaceCreation {}\"");
      getShell().execute("java new-method \"public void testing();\"");
      getShell().execute("ls");
      getShell().execute("build");
      JavaInterface javaInterface = (JavaInterface) getProject()
               .getFacet(JavaSourceFacet.class)
               .getJavaResource(
                        Packages.toFileSyntax("org.jboss.forge.test.interfaces.TestingInterfaceCreation"))
               .getJavaSource();
      Method<JavaInterface> method = javaInterface.getMethod("testing");
      assertNotNull(method);
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
   public void testCreateEnumType() throws Exception
   {
      getShell().execute(
               "java new-enum-type --package org.jboss.forge.test.types \"public enum TestingEnumTypeCreation{}\"");
      getShell().execute("ls");
      getShell().execute("build");
      JavaEnum javaEnum = (JavaEnum) getProject().getFacet(JavaSourceFacet.class)
               .getEnumTypeResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingEnumTypeCreation"))
               .getJavaSource();
      assertNotNull(javaEnum);
   }

   @Test
   public void testCreateEnumConst() throws Exception
   {
      getShell().execute(
               "java new-enum-type --package org.jboss.forge.test.types \"public enum TestingEnumTypeCreation{}\"");
      getShell().execute("java new-enum-const \"A\"");
      getShell().execute("ls");
      getShell().execute("build");
      JavaEnum javaEnum = (JavaEnum) getProject().getFacet(JavaSourceFacet.class)
               .getEnumTypeResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingEnumTypeCreation"))
               .getJavaSource();
      EnumConstant<JavaEnum> enumConst = javaEnum.getEnumConstant("A");
      assertNotNull(enumConst);
   }

   @Test
   public void testCreateAnnotationType() throws Exception
   {
      getShell()
               .execute(
                        "java new-annotation-type --package org.jboss.forge.test.types \"public @interface TestingAnnotationTypeCreation{}\"");
      getShell().execute("ls");
      getShell().execute("build");
      JavaSource<?> source = getProject().getFacet(JavaSourceFacet.class)
               .getJavaResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingAnnotationTypeCreation"))
               .getJavaSource();
      assertNotNull(source);
      assertTrue(source.isAnnotation());
   }

   @Test
   public void testCreateAnnotationElement() throws Exception
   {
      getShell()
               .execute(
                        "java new-annotation-type --package org.jboss.forge.test.types \"public @interface TestingAnnotationTypeCreation {}\"");
      getShell().execute("java new-annotation-element \"int testInt();\"");
      getShell().execute("java new-annotation-element 'String[] testStrings() default {\"\"};'");
      getShell().execute("ls");
      getShell().execute("build");
      JavaSource<?> source = getProject().getFacet(JavaSourceFacet.class)
               .getJavaResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingAnnotationTypeCreation"))
               .getJavaSource();
      assertTrue(source.isAnnotation());
      AnnotationElement testInt = JavaAnnotation.class.cast(source).getAnnotationElement("testInt");
      assertNotNull(testInt);
      assertEquals("testInt", testInt.getName());
      assertEquals("int", testInt.getType());
      assertNull(testInt.getDefaultValue().getLiteral());

      AnnotationElement testStrings = JavaAnnotation.class.cast(source).getAnnotationElement("testStrings");
      assertNotNull(testStrings);
      assertEquals("testStrings", testStrings.getName());
      assertEquals("String[]", testStrings.getTypeInspector().getName());
      assertEquals("{\"\"}", testStrings.getDefaultValue().getLiteral());
   }

}
