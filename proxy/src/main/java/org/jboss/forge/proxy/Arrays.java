/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.proxy;

import org.jboss.forge.furnace.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class Arrays
{
   public static <ELEMENTTYPE> ELEMENTTYPE[] append(ELEMENTTYPE[] array, ELEMENTTYPE... elements)
   {
      final int length = array.length;
      array = java.util.Arrays.copyOf(array, length + elements.length);
      System.arraycopy(elements, 0, array, length, elements.length);
      return array;
   }

   public static <ELEMENTTYPE> ELEMENTTYPE[] prepend(ELEMENTTYPE[] array, ELEMENTTYPE... elements)
   {
      final int length = array.length;
      array = java.util.Arrays.copyOf(array, length + elements.length);
      System.arraycopy(array, 0, array, elements.length, length);
      System.arraycopy(elements, 0, array, 0, elements.length);
      return array;
   }

   public static <ELEMENTTYPE> ELEMENTTYPE[] copy(ELEMENTTYPE[] source, ELEMENTTYPE[] target)
   {
      Assert.isTrue(source.length == target.length, "Source and destination arrays must be of the same length.");
      System.arraycopy(source, 0, target, 0, source.length);
      return target;
   }

   public static <ELEMENTTYPE> ELEMENTTYPE[] shiftLeft(ELEMENTTYPE[] source, ELEMENTTYPE[] target)
   {
      Assert.isTrue(source.length > 0, "Source array length cannot be zero.");
      Assert.isTrue(source.length - 1 == target.length,
               "Destination array must be one element shorter than the source array.");

      System.arraycopy(source, 1, target, 0, target.length);
      return target;
   }

   public static <ELEMENTTYPE> boolean contains(ELEMENTTYPE[] array, ELEMENTTYPE value)
   {
      return indexOf(array, value) >= 0;
   }

   public static <ELEMENTTYPE> int indexOf(ELEMENTTYPE[] array, ELEMENTTYPE value)
   {
      if (array.length == 0)
         return -1;

      for (int i = 0; i < array.length; i++)
      {
         ELEMENTTYPE element = array[i];
         if (element == value)
            return i;
      }
      return -1;
   }

   public static <ELEMENTTYPE> ELEMENTTYPE[] removeElementAtIndex(ELEMENTTYPE[] array, int index)
   {
      Assert.isTrue(array.length > 0, "Cannot remove an element from an already empty array.");

      ELEMENTTYPE[] result = java.util.Arrays.copyOf(array, array.length - 1);
      if (result.length > 0 && array.length + 1 != index && index != result.length)
         System.arraycopy(array, index + 1, result, index, array.length - (array.length - index));
      return result;
   }
}