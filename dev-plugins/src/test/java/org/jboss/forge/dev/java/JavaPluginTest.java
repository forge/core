/*
 * Copyright 2012-2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.java;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
   public void testCreateJavaClassNamed() throws Exception
   {
      getShell().execute(
               "java new-class --named TestingClassCreation --package org.jboss.forge.test.classes");
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
   public void testCreateJavaInterfaceNamed() throws Exception
   {
      getShell()
               .execute(
                        "java new-interface --named TestingInterfaceCreation --package org.jboss.forge.test.interfaces");
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
               .getJavaResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingEnumTypeCreation"))
               .getJavaSource();
      assertNotNull(javaEnum);
   }

   @Test
   public void testCreateEnumTypeNamed() throws Exception
   {
      getShell().execute(
               "java new-enum-type --named TestingEnumTypeCreation --package org.jboss.forge.test.types");
      getShell().execute("ls");
      getShell().execute("build");
      JavaEnum javaEnum = (JavaEnum) getProject().getFacet(JavaSourceFacet.class)
               .getJavaResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingEnumTypeCreation"))
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
               .getJavaResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingEnumTypeCreation"))
               .getJavaSource();
      EnumConstant<JavaEnum> enumConst = javaEnum.getEnumConstant("A");
      assertNotNull(enumConst);
   }

   @Test
   public void testCreateAnnotationType() throws Exception
   {
      queueInputLines("");
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
      JavaAnnotation annotation = JavaAnnotation.class.cast(source);
      assertFalse(annotation.hasAnnotation(Documented.class));
      assertFalse(annotation.hasAnnotation(Retention.class));
      assertFalse(annotation.hasAnnotation(Target.class));

      resetInputQueue();
      getShell()
               .execute(
                        "java new-annotation-type --package org.jboss.forge.test.types --no-target \"public @interface TestingAnnotationTypeCreation{}\"");
      getShell().execute("ls");
      getShell().execute("build");
      source = getProject().getFacet(JavaSourceFacet.class)
               .getJavaResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingAnnotationTypeCreation"))
               .getJavaSource();
      assertNotNull(source);
      assertTrue(source.isAnnotation());
      annotation = JavaAnnotation.class.cast(source);
      assertFalse(annotation.hasAnnotation(Documented.class));
      assertFalse(annotation.hasAnnotation(Retention.class));
      assertFalse(annotation.hasAnnotation(Target.class));

      resetInputQueue();
      queueInputLines("*");
      getShell()
               .execute(
                        "java new-annotation-type --package org.jboss.forge.test.types  --documented --retention-policy RUNTIME \"public @interface TestingAnnotationTypeCreation{}\"");
      source = getProject().getFacet(JavaSourceFacet.class)
               .getJavaResource(Packages.toFileSyntax("org.jboss.forge.test.types.TestingAnnotationTypeCreation"))
               .getJavaSource();
      assertNotNull(source);
      assertTrue(source.isAnnotation());
      annotation = JavaAnnotation.class.cast(source);
      assertTrue(annotation.hasAnnotation(Documented.class));
      assertTrue(annotation.hasAnnotation(Retention.class));
      assertTrue(annotation.hasAnnotation(Target.class));
      assertArrayEquals(ElementType.values(),
               annotation.getAnnotation(Target.class).getEnumArrayValue(ElementType.class));
      assertSame(RetentionPolicy.RUNTIME, annotation.getAnnotation(Retention.class).getEnumValue(RetentionPolicy.class));
   }

   @Test
   public void testCreateAnnotationElement() throws Exception
   {
      queueInputLines("");
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

   @Test
   public void testCreateAnnotationElementFromTypeAndName() throws Exception
   {
      queueInputLines("");
      getShell()
               .execute(
                        "java new-annotation-type --package org.jboss.forge.test.types \"public @interface TestingAnnotationTypeCreation {}\"");
      getShell().execute("java new-annotation-element --type int --name testInt");
      getShell().execute("java new-annotation-element --type String[] --name testStrings");
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
      assertNull(testStrings.getDefaultValue().getLiteral());
   }

   @Test(expected = RuntimeException.class)
   public void testCreateAnnotationElementMissingOptions() throws Exception
   {
      queueInputLines("");
      getShell()
               .execute(
                        "java new-annotation-type --package org.jboss.forge.test.types \"public @interface TestingAnnotationTypeCreation {}\"");
      getShell().execute("java new-annotation-element");
   }

   @Test
   public void testCreateAnnotationInRootFolder() throws Exception
   {
      queueInputLines("");
      getShell()
               .execute(
                        "java new-annotation-type --named TestingAnnotationTypeCreation");
      assertTrue(getProject()
               .getFacet(JavaSourceFacet.class)
               .getJavaResource(
                        Packages.toFileSyntax("TestingAnnotationTypeCreation"))
               .exists());

   }

   @Test
   public void testCreateEnumInRootFolder() throws Exception
   {
      queueInputLines("");
      getShell()
               .execute(
                        "java new-enum-type --named TestingEnumTypeCreation");
      assertTrue(getProject()
               .getFacet(JavaSourceFacet.class)
               .getJavaResource(
                        Packages.toFileSyntax("TestingEnumTypeCreation"))
               .exists());
   }

   @Test
   public void testCreateAnnotationWithoutBrackets() throws Exception
   {
      queueInputLines("", "N");
      getShell()
               .execute(
                        "java new-annotation-type \"public @interface TestingAnnotationTypeCreation\"");
      assertFalse(getProject()
               .getFacet(JavaSourceFacet.class)
               .getJavaResource(
                        Packages.toFileSyntax("TestingAnnotationTypeCreation"))
               .exists());

   }

   @Test
   public void testCreateAnnotationNamed() throws Exception
   {
      queueInputLines("");
      getShell()
               .execute(
                        "java new-annotation-type --named TestingAnnotationTypeCreation --package org.jboss.forge.test.annotations");
      getShell().execute("build");

      assertNotNull(getProject()
               .getFacet(JavaSourceFacet.class)
               .getJavaResource(
                        Packages.toFileSyntax("org.jboss.forge.test.annotations.TestingAnnotationTypeCreation"))
               .getJavaSource());
   }

}
