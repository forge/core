/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.util;

import java.util.HashSet;
import java.util.Set;

import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;

/**
 * Should be used to colorize a Java Source file or Java Source string snippit.
 * 
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaColorizer
{
   public static final ShellColor CLR_ANNOTATION = ShellColor.YELLOW;
   public static final ShellColor CLR_STR_LITERAL = ShellColor.GREEN;
   public static final ShellColor CLR_KEYWORD = ShellColor.BLUE;
   public static final ShellColor CLR_COMMENT = ShellColor.CYAN;

   /**
    * Perform colorization of a Java source string.
    */
   public static String format(ShellPrintWriter writer, String in)
   {
      StringBuilder output = new StringBuilder("");

      char[] arr = in.toCharArray();
      int start = 0;
      boolean capture = false;

      for (int cursor = 0; cursor < arr.length; cursor++)
      {
         if (Character.isWhitespace(arr[cursor]))
         {
            if (capture)
            {
               doCapture(writer, arr, output, start, cursor);
               capture = false;
            }

         }
         else if ((arr[cursor] == '"') || (arr[cursor] == '\''))
         {
            if (capture)
            {
               output.append(getHTMLizedString(arr, start, cursor - start));
               capture = false;
            }

            cursor = balancedCapture(arr, start = cursor, arr[cursor]);
            output.append(writer.renderColor(CLR_STR_LITERAL, getHTMLizedString(arr, start, ++cursor - start)));

         }
         else if (!capture)
         {
            if (Character.isJavaIdentifierPart(arr[cursor]))
            {
               capture = true;
               start = cursor;
            }
            else if (arr[cursor] == '/')
            {
               start = cursor++;
               if (arr[cursor] == '/')
               {
                  while ((cursor != arr.length) && (arr[cursor] != '\n'))
                     cursor++;

                  String comment = getHTMLizedString(arr, start, ++cursor - start + 1);
                  output.append(writer.renderColor(CLR_COMMENT, comment));
                  cursor++;
               }
               else if (arr[cursor] == '*')
               {
                  while ((cursor != arr.length) && !((arr[cursor] == '*') && (arr[cursor + 1] == '/')))
                     cursor++;

                  String comment = getHTMLizedString(arr, start, ++cursor - start + 1);
                  output.append(writer.renderColor(CLR_COMMENT, comment));
                  cursor++;
               }
               else
               {
                  output.append(arr, start, cursor - start);
               }
            }
            else if (arr[cursor] == '@')
            {
               start = cursor++;
               while ((cursor != arr.length) && Character.isJavaIdentifierPart(arr[cursor]))
                  cursor++;
               String token = new String(arr, start, cursor - start);
               output.append(writer.renderColor(CLR_ANNOTATION, token));
            }
         }

         if (arr[cursor] == '(')
         {
            if (capture)
            {
               doCapture(writer, arr, output, start, cursor);
               capture = false;
            }
            output.append(arr[cursor]);
         }
         else if (!capture)
         {
            output.append(arr[cursor]);
         }
      }

      if (capture)
      {
         doCapture(writer, arr, output, start, arr.length);
         capture = false;
      }

      return output.toString();

   }

   private static String getHTMLizedString(char[] arr, int start, int length)
   {
      return new String(arr, start, length);
   }

   private static void doCapture(ShellPrintWriter writer, char[] arr, StringBuilder output, int start, int cursor)
   {
      String tk = new String(arr, start, cursor - start).trim();
      if (LITERALS.contains(tk))
      {
         output.append(writer.renderColor(CLR_KEYWORD, tk));
      }
      else
      {
         output.append(tk);
      }
   }

   private static final Set<String> LITERALS = new HashSet<String>();

   static
   {
      LITERALS.add("public");
      LITERALS.add("private");
      LITERALS.add("protected");
      LITERALS.add("final");
      LITERALS.add("void");
      LITERALS.add("class");
      LITERALS.add("interface");
      LITERALS.add("static");
      LITERALS.add("package");
      LITERALS.add("import");
      LITERALS.add("implements");
      LITERALS.add("extends");
      LITERALS.add("try");
      LITERALS.add("catch");
      LITERALS.add("finally");
      LITERALS.add("while");
      LITERALS.add("for");
      LITERALS.add("if");
      LITERALS.add("else");
      LITERALS.add("true");
      LITERALS.add("false");
      LITERALS.add("new");
      LITERALS.add("this");
      LITERALS.add("switch");
      LITERALS.add("case");
      LITERALS.add("break");
      LITERALS.add("continue");
      LITERALS.add("return");
      LITERALS.add("throw");
      LITERALS.add("volatile");
      LITERALS.add("synchronized");
      LITERALS.add("null");
   }

   private static int balancedCapture(char[] chars, int start, char type)
   {
      int depth = 1;
      char term = type;
      switch (type)
      {
      case '[':
         term = ']';
         break;
      case '{':
         term = '}';
         break;
      case '(':
         term = ')';
         break;
      }

      if (type == term)
      {
         for (start++; start < chars.length; start++)
         {
            if (chars[start] == type)
            {
               return start;
            }
         }
      }
      else
      {
         for (start++; start < chars.length; start++)
         {
            if ((start < chars.length) && (chars[start] == '/'))
            {
               if (start + 1 == chars.length)
               {
                  return start;
               }
               if (chars[start + 1] == '/')
               {
                  start++;
                  while ((start < chars.length) && (chars[start] != '\n'))
                     start++;
               }
               else if (chars[start + 1] == '*')
               {
                  start += 2;
                  while (start < chars.length)
                  {
                     switch (chars[start])
                     {
                     case '*':
                        if ((start + 1 < chars.length) && (chars[start + 1] == '/'))
                        {
                           break;
                        }
                     case '\r':
                     case '\n':
                        break;
                     }
                     start++;
                  }
               }
            }
            if (start == chars.length)
            {
               return start;
            }
            if ((chars[start] == '\'') || (chars[start] == '"'))
            {
               start = captureStringLiteral(chars[start], chars, start, chars.length);
            }
            else if (chars[start] == type)
            {
               depth++;
            }
            else if ((chars[start] == term) && (--depth == 0))
            {
               return start;
            }
         }
      }

      return start;
   }

   private static int captureStringLiteral(final char type, final char[] expr, int cursor, int length)
   {
      while ((++cursor < length) && (expr[cursor] != type))
      {
         if (expr[cursor] == '\\')
         {
            cursor++;
         }
      }

      return cursor;
   }
}
