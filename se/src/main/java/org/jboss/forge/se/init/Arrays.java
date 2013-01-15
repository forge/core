/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.se.init;

import org.jboss.forge.container.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class Arrays
{
   public static <ELEMENTTYPE> ELEMENTTYPE[] append(ELEMENTTYPE[] array, ELEMENTTYPE element)
   {
      final int length = array.length;
      array = java.util.Arrays.copyOf(array, length + 1);
      array[length] = element;
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
}
