/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.util;

/**
 * Utility class for creating pre-condition assertions in method implementations.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Assert
{
   /**
    * Assert that the given {@link Object} is not <code>null</code>; otherwise, throw an
    * {@link IllegalArgumentException} with the given message.
    */
   public static void notNull(Object object, String message) throws IllegalArgumentException
   {
      if (object == null)
      {
         throw new IllegalArgumentException(message);
      }
   }

   /**
    * Assert that the given boolean value is <code>true</code>; otherwise, throw an {@link IllegalArgumentException}
    * with the given message.
    */
   public static void isTrue(boolean value, String message)
   {
      if (value != true)
      {
         throw new IllegalArgumentException(message);
      }
   }
}
