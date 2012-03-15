/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle;

import static org.junit.Assert.*;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.MockAnnotationComplex.anEnum;
import org.jboss.forge.scaffold.util.ScaffoldUtil;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.metawidget.inspector.impl.propertystyle.Property;

public class ForgePropertyStyleTest
         extends AbstractShellTest
{
   
   @Inject private Configuration config;
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

      ForgePropertyStyle propertyStyle = new ForgePropertyStyle(new ForgePropertyStyleConfig().setProject(project).setConfig(config));
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

      propertyStyle = new ForgePropertyStyle(new ForgePropertyStyleConfig().setProject(project).setConfig(config)
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
