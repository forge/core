/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.jboss.forge.maven.ProjectImpl;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyle.ForgeProperty;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.MockAnnotationComplex.anEnum;
import org.jboss.forge.scaffold.util.ScaffoldUtil;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.metawidget.inspector.impl.propertystyle.Property;

public class ForgePropertyStyleTest
         extends AbstractShellTest
{
   //
   // Public methods
   //

   @Test
   public void testAnnotationProxy()
            throws Exception
   {
      Project project = initializeJavaProject();

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      ScaffoldUtil
               .createOrOverwrite(
                        null,
                        java.getJavaResource("org/jboss/forge/scaffold/faces/metawidget/inspector/propertystyle/MockAnnotatedClass.java"),
                        getClass()
                                 .getResourceAsStream(
                                          "/org/jboss/forge/scaffold/faces/metawidget/inspector/propertystyle/MockAnnotatedClass.java"),
                        true);

      // Test default private field convention

      ForgePropertyStyle propertyStyle = new ForgePropertyStyle(new ForgePropertyStyleConfig().setProject(project));
      Map<String, Property> properties = propertyStyle
               .getProperties("org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.MockAnnotatedClass");

      Property property = properties.get("mockAnnotatedProperty");

      MockAnnotationSimple mockAnnotationSimple = property.getAnnotation(MockAnnotationSimple.class);
      assertEquals(MockAnnotationSimple.class, mockAnnotationSimple.annotationType());
      assertEquals((byte) 1, mockAnnotationSimple.aByte());
      assertEquals((short) 2, mockAnnotationSimple.aShort());
      assertEquals(3, mockAnnotationSimple.anInt());
      assertEquals(4l, mockAnnotationSimple.aLong());
      assertEquals(5f, mockAnnotationSimple.aFloat(), 0.01);
      assertEquals(0d, mockAnnotationSimple.aDouble(), 0.01);
      assertEquals('a', mockAnnotationSimple.aChar());
      assertEquals(false, mockAnnotationSimple.aBoolean());
      assertEquals("", mockAnnotationSimple.aString());

      testMockAnnotationComplex(property);

      // Test custom private field convention

      propertyStyle = new ForgePropertyStyle(new ForgePropertyStyleConfig().setProject(project)
               .setPrivateFieldConvention(new MessageFormat("m{1}")));
      properties = propertyStyle
               .getProperties("org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.MockAnnotatedClass");

      property = properties.get("mockAnnotatedProperty");

      mockAnnotationSimple = property.getAnnotation(MockAnnotationSimple.class);
      assertEquals(MockAnnotationSimple.class, mockAnnotationSimple.annotationType());
      assertEquals((byte) 0, mockAnnotationSimple.aByte());
      assertEquals((short) 0, mockAnnotationSimple.aShort());
      assertEquals(0, mockAnnotationSimple.anInt());
      assertEquals(0l, mockAnnotationSimple.aLong());
      assertEquals(0f, mockAnnotationSimple.aFloat(), 0.01);
      assertEquals(6d, mockAnnotationSimple.aDouble(), 0.01);
      assertEquals('a', mockAnnotationSimple.aChar());
      assertEquals(true, mockAnnotationSimple.aBoolean());
      assertEquals("Foo", mockAnnotationSimple.aString());

      testMockAnnotationComplex(property);
   }

   @Test
   public void testManuallyGenerated()
            throws Exception
   {
      Project project = initializeJavaProject();

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      ScaffoldUtil
               .createOrOverwrite(
                        null,
                        java.getJavaResource("org/jboss/forge/scaffold/faces/metawidget/inspector/propertystyle/ManuallyGeneratedClass.java"),
                        getClass()
                                 .getResourceAsStream(
                                          "/org/jboss/forge/scaffold/faces/metawidget/inspector/propertystyle/ManuallyGeneratedClass.java"),
                        true);

      ForgePropertyStyle propertyStyle = new ForgePropertyStyle(new ForgePropertyStyleConfig().setProject(project));
      Map<String, Property> properties = propertyStyle
               .getProperties("org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ManuallyGeneratedClass");

      Property property = properties.get("normalField");
      assertEquals( "normalField", ((ForgeProperty) property).getName() );
      assertEquals( "public getNormalField() : String", ((ForgeProperty) property).getReadMethod().toSignature() );
      assertEquals( "public setNormalField(String) : void", ((ForgeProperty) property).getWriteMethod().toSignature() );

      property = properties.get("URL");
      assertEquals( "URL", ((ForgeProperty) property).getName() );
      assertEquals( "public getURL() : String", ((ForgeProperty) property).getReadMethod().toSignature() );
      assertEquals( "public setURL(String) : void", ((ForgeProperty) property).getWriteMethod().toSignature() );

      property = properties.get("aFIELD");
      assertEquals( "aFIELD", ((ForgeProperty) property).getName() );
      assertEquals( "public getaFIELD() : String", ((ForgeProperty) property).getReadMethod().toSignature() );
      assertEquals( "public setaFIELD(String) : void", ((ForgeProperty) property).getWriteMethod().toSignature() );

      assertEquals( 3, properties.size() );
   }

   public void testConfig()
   {
      ForgePropertyStyleConfig config1 = new ForgePropertyStyleConfig();
      ForgePropertyStyleConfig config2 = new ForgePropertyStyleConfig();

      assertTrue(config1.equals(config2));
      assertEquals(config1.hashCode(), config2.hashCode());
      assertTrue(!config1.equals("Foo"));
      Project project = new ProjectImpl(null, null);
      config1.setProject(project);
      assertTrue(!config1.equals(config2));
      config2.setProject(project);
      assertTrue(config1.equals(config2));
      assertEquals(config1.hashCode(), config2.hashCode());
   }

   //
   // Private methods
   //

   private void testMockAnnotationComplex(Property property)
   {
      MockAnnotationComplex mockAnnotationComplex = property.getAnnotation(MockAnnotationComplex.class);
      assertEquals(MockAnnotationComplex.class, mockAnnotationComplex.annotationType());
      assertEquals(Date.class, mockAnnotationComplex.aClass());
      // TODO: assertEquals( 42, mockAnnotationComplex.anAnnotation().value() );
      assertEquals(anEnum.ONE, mockAnnotationComplex.anEnum());
      assertTrue(Arrays.equals(new byte[] { 7, 8 }, mockAnnotationComplex.aByteArray()));
      assertTrue(Arrays.equals(new short[] { 9, 10 }, mockAnnotationComplex.aShortArray()));
      assertTrue(Arrays.equals(new int[] { 11, 12 }, mockAnnotationComplex.anIntArray()));
      assertTrue(Arrays.equals(new long[] { 13l, 14l }, mockAnnotationComplex.aLongArray()));
      assertTrue(Arrays.equals(new float[] { 15f, 16f }, mockAnnotationComplex.aFloatArray()));
      assertTrue(Arrays.equals(new double[] { 17d, 18d }, mockAnnotationComplex.aDoubleArray()));
      assertTrue(Arrays.equals(new char[] { 'b', 'c' }, mockAnnotationComplex.aCharArray()));
      assertTrue(Arrays.equals(new boolean[] { false, true }, mockAnnotationComplex.aBooleanArray()));
      assertTrue(Arrays.equals(new String[] { "Bar", "Baz" }, mockAnnotationComplex.aStringArray()));
      assertTrue(Arrays.equals(new Class[] { Calendar.class, Color.class }, mockAnnotationComplex.aClassArray()));
      // TODO: assertTrue( Arrays.equals( ...,mockAnnotationComplex.anAnnotationArray() ));
      assertTrue(Arrays.equals(new anEnum[] { anEnum.TWO, anEnum.THREE }, mockAnnotationComplex.anEnumArray()));
   }
}
