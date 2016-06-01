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
public @interface MockAnnotationComplex
{
   Class<?> aClass();

   NestedMockAnnotation anAnnotation();

   anEnum anEnum();

   byte[] aByteArray();

   short[] aShortArray();

   int[] anIntArray();

   long[] aLongArray();

   float[] aFloatArray();

   double[] aDoubleArray();

   char[] aCharArray();

   boolean[] aBooleanArray();

   String[] aStringArray();

   Class<?>[] aClassArray();

   NestedMockAnnotation[] anAnnotationArray();

   anEnum[] anEnumArray();

   //
   // Inner class
   //

   public static enum anEnum {

      ONE,
      TWO,
      THREE
   }
}
