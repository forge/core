/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Mike Brock
 */
@Alias("echo")
@Help("Writes input to output.")
@Topic("Shell Environment")
public class Echo implements Plugin
{
   @Inject
   Shell shell;

   @DefaultCommand
   public void run(
            @Option(help = "The text to be echoed") final String[] tokens,
            final PipeOut out)
   {
      if (tokens == null || tokens.length == 0)
      {
         return;
      }

      out.println(echo(shell, promptExpressionParser(shell, tokensToString(tokens))));
   }

   public static String tokensToString(String... tokens)
   {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < tokens.length; i++)
      {
         sb.append(tokens[i]);
         if (i + 1 < tokens.length)
         {
            sb.append(" ");
         }
      }

      return sb.toString();
   }

   public static String promptExpressionParser(Shell shell, String input)
   {
      StringBuilder builder = new StringBuilder();
      char[] expr = input.toCharArray();
      ShellColor c = null;

      int i = 0;
      int start = 0;
      for (; i < expr.length; i++)
      {
         switch (expr[i])
         {
         case '\\':
            if (i + 1 < expr.length)
            {
               /**
                * Handle escape codes here.
                */
               switch (expr[++i])
               {
               case '\\':
                  builder.append(new String(expr, start, i - start - 1));
                  builder.append("\\");
                  start = i + 1;
                  break;

               case 'w':
                  builder.append(new String(expr, start, i - start - 1));
                  builder.append(shell.getEnvironment().getProperty("CWD"));
                  start = i + 1;
                  break;

               case 'W':
                  builder.append(new String(expr, start, i - start - 1));
                  String v = (String) shell.getEnvironment().getProperty("CWD");
                  builder.append(v.substring(v.lastIndexOf('/') + 1));
                  start = i + 1;
                  break;

               case 'd':
                  builder.append(new String(expr, start, i - start - 1));
                  builder.append(new SimpleDateFormat("EEE MMM dd").format(new Date()));
                  start = i + 1;
                  break;

               case 't':
                  builder.append(new String(expr, start, i - start - 1));
                  builder.append(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                  start = i + 1;
                  break;

               case 'T':
                  builder.append(new String(expr, start, i - start - 1));
                  builder.append(new SimpleDateFormat("hh:mm:ss").format(new Date()));
                  start = i + 1;
                  break;

               case '@':
                  builder.append(new String(expr, start, i - start - 1));
                  builder.append(new SimpleDateFormat("KK:mmaa").format(new Date()));
                  start = i + 1;
                  break;

               case '$':
                  builder.append(new String(expr, start, i - start - 1));
                  builder.append("\\$");
                  start = i + 1;
                  break;

               case 'r':
                  builder.append(new String(expr, start, i - start - 1));
                  builder.append("\r");
                  start = i + 1;
                  break;
               case 'n':
                  builder.append(new String(expr, start, i - start - 1));
                  builder.append("\n");
                  start = i + 1;
                  break;

               case 'c':
                  if (i + 1 < expr.length)
                  {
                     switch (expr[++i])
                     {
                     case '{':
                        boolean nextNodeColor = false;

                        builder.append(new String(expr, start, i - start - 2));

                        start = i;
                        while (i < input.length() && input.charAt(i) != '}')
                           i++;

                        if (i == input.length() && input.charAt(i) != '}')
                        {
                           builder.append(new String(expr, start, i - start));
                        }
                        else
                        {
                           String color = new String(expr, start + 1, i - start - 1);

                           start = ++i;

                           Capture: while (i < expr.length)
                           {
                              switch (expr[i])
                              {
                              case '\\':
                                 if (i + 1 < expr.length)
                                 {
                                    if (expr[i + 1] == 'c')
                                    {
                                       if ((i + 2 < expr.length) && expr[i + 2] == '{')
                                       {
                                          nextNodeColor = true;
                                       }
                                       break Capture;
                                    }
                                 }

                              default:
                                 i++;
                              }
                           }

                           if (c != null && c != ShellColor.NONE)
                           {
                              builder.append(shell.renderColor(ShellColor.NONE, ""));
                           }

                           c = ShellColor.NONE;
                           for (ShellColor sc : ShellColor.values())
                           {
                              if (sc.name().equalsIgnoreCase(color == null ? "" : color.trim()))
                              {
                                 c = sc;
                                 break;
                              }
                           }

                           String toColorize = promptExpressionParser(shell, new String(expr, start, i - start));
                           String cStr = shell.renderColor(c, toColorize);

                           builder.append(cStr);
                           if (nextNodeColor)
                           {
                              start = i--;
                           }
                           else
                           {
                              start = i += 2;
                           }
                        }

                        break;

                     default:
                        start = i += 2;
                     }
                  }
               }
            }
         }
      }

      if (start < expr.length && i > start)
      {
         builder.append(new String(expr, start, i - start));
      }

      return builder.toString();
   }

   public static String echo(Shell shell, String input)
   {
      char[] expr = input.toCharArray();
      StringBuilder out = new StringBuilder();
      int start = 0;
      int i = 0;
      while (i < expr.length)
      {
         if (i >= expr.length)
         {
            break;
         }

         switch (expr[i])
         {
         case '\\':
            if (i + 1 < expr.length && expr[i + 1] == '$')
            {
               out.append(new String(expr, start, i - start));
               out.append('$');
               start = i += 2;
            }
            break;

         case '$':
            out.append(new String(expr, start, i - start));
            start = ++i;
            while (i != expr.length && Character.isJavaIdentifierPart(expr[i]) && expr[i] != 27)
            {
               i++;
            }

            String var = new String(expr, start, i - start);
            if (shell.getEnvironment().getProperties().containsKey(var))
            {
               out.append(String.valueOf(shell.getEnvironment().getProperties().get(var)));
            }

            start = i;
            break;

         default:
            if (Character.isWhitespace(expr[i]))
            {
               out.append(new String(expr, start, i - start));

               start = i;
               while (i != expr.length && Character.isWhitespace(expr[i]))
               {
                  i++;
               }

               out.append(new String(expr, start, i - start));

               start = i;
               continue;
            }
         }
         i++;
      }

      if (start < expr.length && i > start)
      {
         out.append(new String(expr, start, i - start));
      }

      return out.toString();
   }
}
