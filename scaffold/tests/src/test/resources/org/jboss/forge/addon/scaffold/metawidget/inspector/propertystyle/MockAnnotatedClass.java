/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.metawidget.inspector.propertystyle;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

import org.jboss.forge.addon.scaffold.metawidget.inspector.propertystyle.NestedMockAnnotation;
import org.jboss.forge.addon.scaffold.metawidget.inspector.propertystyle.MockAnnotationSimple;
import org.jboss.forge.addon.scaffold.metawidget.inspector.propertystyle.MockAnnotationComplex;
import org.jboss.forge.addon.scaffold.metawidget.inspector.propertystyle.MockAnnotationComplex.anEnum;

/**
 * Tests all annotation types, defined on both fields and getters
 */

public class MockAnnotatedClass
{
   @MockAnnotationSimple(
            aByte = 1,
            aShort = 2,
            anInt = 3,
            aLong = 4l,
            aFloat = 5f)
   private String mockAnnotatedProperty;

   @MockAnnotationSimple(
            aDouble = 6d,
            aChar = 'a',
            aBoolean = true,
            aString = "Foo")
   private String mMockAnnotatedProperty;

   @MockAnnotationComplex(
            aClass = Date.class,
            anAnnotation = @NestedMockAnnotation( 43 ),
            anEnum = anEnum.ONE,
            aByteArray = { 7, 8 },
            aShortArray = { 9, 10 },
            anIntArray = { 11, 12 },
            aLongArray = { 13l, 14l },
            aFloatArray = { 15f, 16f },
            aDoubleArray = { 17d, 18d },
            aCharArray = { 'b', 'c' },
            aBooleanArray = { false, true },
            aStringArray = { "Bar", "Baz" },
            aClassArray = { Calendar.class, Color.class },
            anAnnotationArray = { @NestedMockAnnotation },
            anEnumArray = { anEnum.TWO, anEnum.THREE })
   public String getMockAnnotatedProperty()
   {
   }
}
