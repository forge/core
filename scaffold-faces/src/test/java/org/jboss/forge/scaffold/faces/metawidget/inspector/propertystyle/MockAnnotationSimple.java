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
