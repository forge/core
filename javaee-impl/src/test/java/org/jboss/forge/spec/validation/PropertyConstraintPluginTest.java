/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.validation;

import static org.jboss.forge.shell.util.ConstraintInspector.getName;
import static org.jboss.forge.spec.javaee.validation.util.ResourceHelper.getJavaClassFromResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.validation.PropertyConstraintPlugin;
import org.jboss.forge.spec.javaee.validation.ValidationPlugin;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Pollet
 */
@RunWith(Arquillian.class)
public class PropertyConstraintPluginTest extends SingletonAbstractShellTest
{
   private static final String VALIDATION_PLUGIN_NAME = getName(ValidationPlugin.class);
   private static final String CONSTRAINT_PLUGIN_NAME = getName(PropertyConstraintPlugin.class);
   private static final String CLASS_NAME = "Foo";
   private static final String PROPERTY_NAME = "foo";
   private static final String PROPERTY_ACESSOR_NAME = "getFoo";

   @Before
   @Override
   public void beforeTest() throws Exception
   {
      super.beforeTest();
      initializeJavaProject();

      // setup validation
      queueInputLines("", "", "");
      getShell().execute(VALIDATION_PLUGIN_NAME + " setup");

      // create a class
      assertTrue(getProject().hasFacet(JavaSourceFacet.class));

      final JavaSourceFacet javaSourceFacet = getProject().getFacet(JavaSourceFacet.class);
      assertNotNull(javaSourceFacet);

      final JavaClass fooClass = JavaParser.create(JavaClass.class)
               .setPackage(javaSourceFacet.getBasePackage())
               .setName(CLASS_NAME);
      fooClass.addField("private String " + PROPERTY_NAME + ";");
      fooClass.addMethod("public String " + PROPERTY_ACESSOR_NAME + "(){return foo;}");

      JavaResource fooResource = javaSourceFacet.saveJavaSource(fooClass);
      assertNotNull(fooResource);

      // pick-up the created resource
      getShell().execute("pick-up " + fooResource.getFullyQualifiedName());
   }

   @Test
   public void testAddValidConstraint() throws Exception
   {
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Valid --onProperty " + PROPERTY_NAME);
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Valid --onProperty " + PROPERTY_NAME + " --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(Valid.class));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(Valid.class));
   }

   @Test
   public void testAddNullConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Null --onProperty " + PROPERTY_NAME + " --message " + message);
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Null --onProperty " + PROPERTY_NAME + " --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(Null.class));
      assertEquals(message, property.getAnnotation(Null.class).getStringValue("message"));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(Null.class));
   }

   @Test
   public void testAddNotNullConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " NotNull --onProperty " + PROPERTY_NAME + " --message " + message);
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " NotNull --onProperty " + PROPERTY_NAME + " --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(NotNull.class));
      assertEquals(message, property.getAnnotation(NotNull.class).getStringValue("message"));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(NotNull.class));
   }

   @Test
   public void testAddAssertTrueConstraint() throws Exception
   {
      final String message = "Message";
      getShell()
               .execute(CONSTRAINT_PLUGIN_NAME + " AssertTrue --onProperty " + PROPERTY_NAME + " --message " + message);
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " AssertTrue --onProperty " + PROPERTY_NAME + " --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(AssertTrue.class));
      assertEquals(message, property.getAnnotation(AssertTrue.class).getStringValue("message"));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(AssertTrue.class));
   }

   @Test
   public void testAddAssertFalseConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(
               CONSTRAINT_PLUGIN_NAME + " AssertFalse --onProperty " + PROPERTY_NAME + " --message " + message);
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " AssertFalse --onProperty " + PROPERTY_NAME + " --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(AssertFalse.class));
      assertEquals(message, property.getAnnotation(AssertFalse.class).getStringValue("message"));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(AssertFalse.class));
   }

   @Test
   public void testAddMinConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(
               CONSTRAINT_PLUGIN_NAME + " Min --onProperty " + PROPERTY_NAME + " --min 3 --message " + message);
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Min --onProperty " + PROPERTY_NAME + " --min 3 --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(Min.class));
      assertEquals(message, property.getAnnotation(Min.class).getStringValue("message"));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(Min.class));
      assertEquals("3", accessor.getAnnotation(Min.class).getLiteralValue());
   }

   @Test
   public void testAddMaxConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(
               CONSTRAINT_PLUGIN_NAME + " Max --onProperty " + PROPERTY_NAME + " --max 3 --message " + message);
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Max --onProperty " + PROPERTY_NAME + " --max 3 --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(Max.class));
      assertEquals(message, property.getAnnotation(Max.class).getStringValue("message"));
      assertEquals("3", property.getAnnotation(Max.class).getLiteralValue());

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(Max.class));
      assertEquals("3", accessor.getAnnotation(Max.class).getLiteralValue());
   }

   @Test
   public void testAddDecimalMinConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(
               CONSTRAINT_PLUGIN_NAME + " DecimalMin --onProperty " + PROPERTY_NAME + " --min 3 --message " + message);
      getShell()
               .execute(CONSTRAINT_PLUGIN_NAME + " DecimalMin --onProperty " + PROPERTY_NAME + " --min 3 --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(DecimalMin.class));
      assertEquals(message, property.getAnnotation(DecimalMin.class).getStringValue("message"));
      assertEquals("3", property.getAnnotation(DecimalMin.class).getStringValue());

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(DecimalMin.class));
      assertEquals("3", accessor.getAnnotation(DecimalMin.class).getStringValue());
   }

   @Test
   public void testAddDecimalMaxConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(
               CONSTRAINT_PLUGIN_NAME + " DecimalMax --onProperty " + PROPERTY_NAME + " --max 3 --message " + message);
      getShell()
               .execute(CONSTRAINT_PLUGIN_NAME + " DecimalMax --onProperty " + PROPERTY_NAME + " --max 3 --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(DecimalMax.class));
      assertEquals(message, property.getAnnotation(DecimalMax.class).getStringValue("message"));
      assertEquals("3", property.getAnnotation(DecimalMax.class).getStringValue());

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(DecimalMax.class));
      assertEquals("3", accessor.getAnnotation(DecimalMax.class).getStringValue());
   }

   @Test
   public void testAddSizeConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Size --onProperty " + PROPERTY_NAME + " --message " + message);
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Size --onProperty " + PROPERTY_NAME + " --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(Size.class));
      assertEquals(message, property.getAnnotation(Size.class).getStringValue("message"));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(Size.class));
   }

   @Test
   public void testAddSizeConstraintWithMinMax() throws Exception
   {
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Size --onProperty " + PROPERTY_NAME + " --min 0 --max 3");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(Size.class));
      assertEquals("0", property.getAnnotation(Size.class).getLiteralValue("min"));
      assertEquals("3", property.getAnnotation(Size.class).getLiteralValue("max"));
   }

   @Test
   public void testAddDigitsConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(
               CONSTRAINT_PLUGIN_NAME + " Digits --onProperty " + PROPERTY_NAME
                        + " --integer 3 --fraction 4 --message " + message);
      getShell().execute(
               CONSTRAINT_PLUGIN_NAME + " Digits --onProperty " + PROPERTY_NAME
                        + " --integer 4 --fraction 3 --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(Digits.class));
      assertEquals("3", property.getAnnotation(Digits.class).getLiteralValue("integer"));
      assertEquals("4", property.getAnnotation(Digits.class).getLiteralValue("fraction"));
      assertEquals(message, property.getAnnotation(Digits.class).getStringValue("message"));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(Digits.class));
      assertEquals("4", accessor.getAnnotation(Digits.class).getLiteralValue("integer"));
      assertEquals("3", accessor.getAnnotation(Digits.class).getLiteralValue("fraction"));
   }

   @Test
   public void testAddPastConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Past --onProperty " + PROPERTY_NAME + " --message " + message);
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Past --onProperty " + PROPERTY_NAME + " --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(Past.class));
      assertEquals(message, property.getAnnotation(Past.class).getStringValue("message"));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(Past.class));
   }

   @Test
   public void testAddFutureConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Future --onProperty " + PROPERTY_NAME + " --message " + message);
      getShell().execute(CONSTRAINT_PLUGIN_NAME + " Future --onProperty " + PROPERTY_NAME + " --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(Future.class));
      assertEquals(message, property.getAnnotation(Future.class).getStringValue("message"));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(Future.class));
   }

   @Test
   public void testAddPatternConstraint() throws Exception
   {
      final String message = "Message";
      getShell().execute(
               CONSTRAINT_PLUGIN_NAME + " Pattern --onProperty " + PROPERTY_NAME + " --regexp [a-z]* --message "
                        + message);
      getShell().execute(
               CONSTRAINT_PLUGIN_NAME + " Pattern --onProperty " + PROPERTY_NAME + " --regexp [a-z]* --onAccessor");

      final JavaClass fooClass = getJavaClassFromResource(getShell().getCurrentResource());
      final Field<JavaClass> property = fooClass.getField(PROPERTY_NAME);

      assertNotNull(property);
      assertTrue(property.hasAnnotation(Pattern.class));
      assertEquals("[a-z]*", property.getAnnotation(Pattern.class).getStringValue("regexp"));
      assertEquals(message, property.getAnnotation(Pattern.class).getStringValue("message"));
      assertEquals(null, property.getAnnotation(Pattern.class).getStringValue("flags"));

      final Method<JavaClass> accessor = fooClass.getMethod(PROPERTY_ACESSOR_NAME);

      assertNotNull(accessor);
      assertTrue(accessor.hasAnnotation(Pattern.class));
      assertEquals("[a-z]*", accessor.getAnnotation(Pattern.class).getStringValue("regexp"));
   }
}
