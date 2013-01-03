/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.util;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Arrays
{
   public static <ELEMENTTYPE> ELEMENTTYPE[] append(ELEMENTTYPE[] array, ELEMENTTYPE element)
   {
      final int length = array.length;
      array = java.util.Arrays.copyOf(array, length + 1);
      array[length] = element;
      return array;
   }

   public static <ELEMENTTYPE> ELEMENTTYPE[] copy(ELEMENTTYPE[] array, ELEMENTTYPE[] target)
   {
      Assert.isTrue(array.length == target.length, "Source and destination arrays must be of the same length");
      System.arraycopy(array, 0, target, 0, array.length);
      return target;
   }
}
