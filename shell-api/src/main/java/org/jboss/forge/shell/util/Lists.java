/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.util;

import java.util.List;

import org.jboss.forge.parser.java.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Lists
{
   /**
    * Return the index of the last element of this List, if any (size - 1), if the list is empty, return 0;
    */
   public static int indexOfLastElement(List<?> list)
   {
      Assert.notNull(list, "Argument List<?> must not be null.");
      return list.size() == 0 ? 0 : list.size() - 1;
   }

   /**
    * Return the the last element of this List, if any. If the list contains elements, return the element at index:
    * "size - 1", if the list is empty, return null;
    */
   public static <T> T lastElement(List<T> list)
   {
      Assert.notNull(list, "Argument List<?> must not be null.");
      if (list.isEmpty())
         return null;

      return list.get(list.size() - 1);
   }
}
