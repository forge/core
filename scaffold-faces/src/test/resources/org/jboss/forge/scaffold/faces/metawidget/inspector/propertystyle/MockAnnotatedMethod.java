package org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.AnotherMockAnnotation;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.MockAnnotation;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.MockAnnotation.anEnum;

public class MockAnnotatedMethod
{
   @MockAnnotation(
            aByte = 1,
            aShort = 2,
            anInt = 3,
            aLong = 4l,
            aFloat = 5f,
            aDouble = 6d,
            aChar = 'a',
            aBoolean = true,
            aString = "Foo",
            aClass = Date.class,
            anAnnotation = @AnotherMockAnnotation( 43 ),
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
            anAnnotationArray = { @AnotherMockAnnotation },
            anEnumArray = { anEnum.TWO, anEnum.THREE })
   public MockAnnotatedMethod()
   {
   }
}
