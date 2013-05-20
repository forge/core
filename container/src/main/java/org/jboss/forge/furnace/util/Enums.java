/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.util;

import java.util.Arrays;
import java.util.List;

/**
 * Utility methods for enum types.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Enums
{
   public static Enum<?> valueOf(final Class<?> type, final Object value)
   {
      if (value != null)
      {
         List<?> enums = Arrays.asList(type.getEnumConstants());
         for (Object e : enums)
         {
            if (e.toString().equals(value.toString())
                     || ((Enum<?>) e).name().equals(value.toString()))
            {
               return (Enum<?>) e;
            }
         }
      }
      return null;
   }

   public static boolean hasValue(final Class<?> type, final Object value)
   {
      return valueOf(type, value) != null;
   }

   public static <T extends Enum<T>> List<T> getValues(final Class<T> type)
   {
      return Arrays.asList(type.getEnumConstants());
   }
}
