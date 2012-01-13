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

import java.io.InputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyle.AnnotationProxy;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.MockAnnotation.anEnum;

public class ForgePropertyStyleTest
         extends TestCase
{
   //
   // Public methods
   //

   public void testAnnotationProxy()
   {
      InputStream stream = ForgePropertyStyleTest.class
               .getResourceAsStream("/org/jboss/forge/scaffold/faces/metawidget/inspector/propertystyle/MockAnnotatedMethod.java");
      Method<JavaClass> method = JavaParser.parse(JavaClass.class, stream).getMethods().get(0);

      @SuppressWarnings("rawtypes")
      MockAnnotation mockAnnotation = AnnotationProxy.newInstance((Annotation) method
               .getAnnotation(MockAnnotation.class));

      assertEquals(MockAnnotation.class, mockAnnotation.annotationType());
      assertEquals((byte) 1, mockAnnotation.aByte());
      assertEquals((short) 2, mockAnnotation.aShort());
      assertEquals(3, mockAnnotation.anInt());
      assertEquals(4l, mockAnnotation.aLong());
      assertEquals(5f, mockAnnotation.aFloat());
      assertEquals(6d, mockAnnotation.aDouble());
      assertEquals('a', mockAnnotation.aChar());
      assertEquals(true, mockAnnotation.aBoolean());
      assertEquals("Foo", mockAnnotation.aString());
      // assertEquals( Date.class, mockAnnotation.aClass() );
      // assertEquals( 42, mockAnnotation.anAnnotation().value() );
      assertEquals(anEnum.ONE, mockAnnotation.anEnum());
      assertTrue(Arrays.equals(new byte[] { 7, 8 }, mockAnnotation.aByteArray()));
      assertTrue(Arrays.equals(new short[] { 9, 10 }, mockAnnotation.aShortArray()));
      assertTrue(Arrays.equals(new int[] { 11, 12 }, mockAnnotation.anIntArray()));
      assertTrue(Arrays.equals(new long[] { 13l, 14l }, mockAnnotation.aLongArray()));
      assertTrue(Arrays.equals(new float[] { 15f, 16f }, mockAnnotation.aFloatArray()));
      assertTrue(Arrays.equals(new double[] { 17d, 18d }, mockAnnotation.aDoubleArray()));
      assertTrue(Arrays.equals(new char[] { 'b', 'c' }, mockAnnotation.aCharArray()));
      assertTrue(Arrays.equals(new boolean[] { false, true }, mockAnnotation.aBooleanArray()));
      assertTrue(Arrays.equals(new String[] { "Bar", "Baz" }, mockAnnotation.aStringArray()));
      // assertTrue(Arrays.equals(new Class[] { Calendar.class, Color.class }, mockAnnotation.aClassArray()));
      // assertTrue( Arrays.equals(new Class[]{Calendar.class,Color.class}, mockAnnotation.anAnnotationArray() ));
      assertTrue(Arrays.equals(new anEnum[] { anEnum.TWO, anEnum.THREE }, mockAnnotation.anEnumArray()));
   }
}
