/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.util;

public class Types
{
   public static Class<?> boxPrimitive(Class<?> cls)
   {
      if (cls == int.class || cls == Integer.class)
      {
         return Integer.class;
      }
      else if (cls == int[].class || cls == Integer[].class)
      {
         return Integer[].class;
      }
      else if (cls == char.class || cls == Character.class)
      {
         return Character.class;
      }
      else if (cls == char[].class || cls == Character[].class)
      {
         return Character[].class;
      }
      else if (cls == long.class || cls == Long.class)
      {
         return Long.class;
      }
      else if (cls == long[].class || cls == Long[].class)
      {
         return Long[].class;
      }
      else if (cls == short.class || cls == Short.class)
      {
         return Short.class;
      }
      else if (cls == short[].class || cls == Short[].class)
      {
         return Short[].class;
      }
      else if (cls == double.class || cls == Double.class)
      {
         return Double.class;
      }
      else if (cls == double[].class || cls == Double[].class)
      {
         return Double[].class;
      }
      else if (cls == float.class || cls == Float.class)
      {
         return Float.class;
      }
      else if (cls == float[].class || cls == Float[].class)
      {
         return Float[].class;
      }
      else if (cls == boolean.class || cls == Boolean.class)
      {
         return Boolean.class;
      }
      else if (cls == boolean[].class || cls == Boolean[].class)
      {
         return Boolean[].class;
      }
      else if (cls == byte.class || cls == Byte.class)
      {
         return Byte.class;
      }
      else if (cls == byte[].class || cls == Byte[].class)
      {
         return Byte[].class;
      }

      return cls;
   }

   public static Class<?> unboxPrimitive(Class<?> cls)
   {
      if (cls == Integer.class || cls == int.class)
      {
         return int.class;
      }
      else if (cls == Integer[].class || cls == int[].class)
      {
         return int[].class;
      }
      else if (cls == Long.class || cls == long.class)
      {
         return long.class;
      }
      else if (cls == Long[].class || cls == long[].class)
      {
         return long[].class;
      }
      else if (cls == Character.class || cls == char.class)
      {
         return char.class;
      }
      else if (cls == Character[].class || cls == char[].class)
      {
         return char[].class;
      }
      else if (cls == Short.class || cls == short.class)
      {
         return short.class;
      }
      else if (cls == Short[].class || cls == short[].class)
      {
         return short[].class;
      }
      else if (cls == Double.class || cls == double.class)
      {
         return double.class;
      }
      else if (cls == Double[].class || cls == double[].class)
      {
         return double[].class;
      }
      else if (cls == Float.class || cls == float.class)
      {
         return float.class;
      }
      else if (cls == Float[].class || cls == float[].class)
      {
         return float[].class;
      }
      else if (cls == Boolean.class || cls == boolean.class)
      {
         return boolean.class;
      }
      else if (cls == Boolean[].class || cls == boolean[].class)
      {
         return boolean[].class;
      }
      else if (cls == Byte.class || cls == byte.class)
      {
         return byte.class;
      }
      else if (cls == Byte[].class || cls == byte[].class)
      {
         return byte[].class;
      }

      return cls;
   }
}
