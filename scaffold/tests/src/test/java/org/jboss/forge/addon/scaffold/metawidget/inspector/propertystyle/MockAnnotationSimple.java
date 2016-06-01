/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.metawidget.inspector.propertystyle;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public @interface MockAnnotationSimple
{
   byte aByte() default 0;

   short aShort() default 0;

   int anInt() default 0;

   long aLong() default 0;

   float aFloat() default 0;

   double aDouble() default 0;

   char aChar() default 'a';

   boolean aBoolean() default false;

   byte[] aByteArray() default {};

   short[] aShortArray() default {};

   int[] anIntArray() default {};

   long[] aLongArray() default {};

   float[] aFloatArray() default {};

   double[] aDoubleArray() default {};

   char[] aCharArray() default {};

   boolean[] aBooleanArray() default {};

   String aString() default "";
}
