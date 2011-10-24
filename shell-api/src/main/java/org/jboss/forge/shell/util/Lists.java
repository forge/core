/*
 * JBoss, by Red Hat.
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
