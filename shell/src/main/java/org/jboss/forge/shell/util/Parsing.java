/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.util;

public abstract class Parsing
{
   public static int firstWhitespace(String str)
   {
      int len = str.length();
      for (int i = 0; i < len; i++)
      {
         switch (str.charAt(i))
         {
         case '\t':
         case ' ':
            return i;
         }

      }

      return str.length();
   }

   public static String firstToken(String str)
   {
      return str.substring(0, firstWhitespace(str));
   }
}
