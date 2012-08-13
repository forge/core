/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.fshparser;

import static org.jboss.forge.shell.command.fshparser.Parse.isReservedWord;
import static org.jboss.forge.shell.command.fshparser.Parse.isTokenPart;
import static org.mvel2.util.ParseTools.balancedCapture;
import static org.mvel2.util.ParseTools.isWhitespace;

import org.mvel2.util.ParseTools;
import org.mvel2.util.StringAppender;

/**
 * @author Mike Brock .
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
public class FSHParser
{
   private char[] expr;
   private int cursor;
   private final int length;

   private Node firstNode;
   private Node node;

   private boolean nest = false;

   public FSHParser(final String expr)
   {
      this.length = (this.expr = expr.toCharArray()).length;
   }

   public FSHParser(final String expr, final boolean nest)
   {
      this.length = (this.expr = expr.toCharArray()).length;
      this.nest = nest;
   }

   public Node parse()
   {
      Node n;
      while ((n = captureLogicalStatement()) != null)
      {
         addNode(n);
      }
      return firstNode;
   }

   private Node nextNode()
   {
      skipWhitespace();
      int start = cursor;

      if (cursor >= length)
      {
         return null;
      }
      else if (expr[cursor] == ';')
      {
         ++cursor;
         return new StatementTerminator();
      }

      switch (expr[cursor])
      {
      case '@':
         start++;
         skipToEOS();

         String scriptTk = new String(expr, start, cursor - start);
         return new ScriptNode(new TokenNode(scriptTk), true);

         // literals
      case '\'':
      case '"':
         cursor = balancedCapture(expr, cursor, expr[cursor]);
         return new StringTokenNode(new String(expr, start + 1, cursor++ - start - 1));

      case '(':
         cursor = balancedCapture(expr, cursor, expr[cursor]);
         return new FSHParser(new String(expr, ++start, cursor++ - start), true).parse();

      default:
         String tk = captureToken();

         if (isReservedWord(tk))
         {
            boolean block = "for".equals(tk) || "if".equals(tk) || "while".equals(tk) || "def".equals(tk);

            start = cursor;
            SkipLoop: while (cursor <= length)
            {
               switch (expr[cursor])
               {
               case '\'':
               case '"':
               case '(':
                  cursor = balancedCapture(expr, cursor, expr[cursor]);

                  if (block)
                  {
                     cursor++;
                     while ((cursor != length) && Character.isWhitespace(expr[cursor]))
                        cursor++;

                     StringAppender buf = new StringAppender("def".equals(tk) ? " " : "");

                     if (cursor != length)
                     {
                        do
                        {
                           boolean openBracket = expr[cursor] == '{';

                           if (openBracket)
                           {
                              cursor++;
                           }

                           buf.append(shellToMVEL(new String(expr, start, cursor - start - (openBracket ? 1 : 0)), true));
                           if (openBracket)
                           {
                              buf.append('{');
                           }

                           start = cursor;

                           if (openBracket)
                           {
                              cursor = balancedCapture(expr, cursor, '{');
                           }
                           else
                           {
                              while ((cursor != length) && (expr[cursor] != ';'))
                                 cursor++;
                           }

                           int offset = (cursor != length) && (expr[cursor] == '}') ? -1 : 0;

                           buf.append(shellToMVEL(new String(expr, start, cursor - start).trim(), false));

                           if (offset == -1)
                           {
                              buf.append("}");
                              cursor++;
                           }

                           tk += buf.toString();
                           buf.reset();

                           start = cursor;
                        }
                        while (ifThenElseBlockContinues());

                        return new ScriptNode(new TokenNode(tk), true);
                     }
                  }
                  break;

               case ';':
                  break SkipLoop;
               }

               cursor++;
            }

            tk += new String(expr, start, cursor - start);
         }

         return tk.startsWith("$") ? new ScriptNode(new TokenNode(tk), false) : new TokenNode(tk);
      }
   }

   protected boolean ifThenElseBlockContinues()
   {
      skipWhitespace();
      if ((cursor + 4) < length)
      {
         if (expr[cursor] != ';')
         {
            cursor--;
         }
         skipWhitespace();

         if ((expr[cursor] == 'e') && (expr[cursor + 1] == 'l') && (expr[cursor + 2] == 's')
                  && (expr[cursor + 3] == 'e')
                  && (isWhitespace(expr[cursor + 4]) || (expr[cursor + 4] == '{')))
         {

            cursor += 4;
            skipWhitespace();

            if (((cursor + 1) < length) && (expr[cursor] == 'i') && (expr[cursor + 1] == 'f'))
            {
               cursor += 2;

               expectNext('(');
               cursor = balancedCapture(expr, cursor, '(') + 1;
            }

            skipWhitespace();

            return true;
         }
      }
      return false;
   }

   private LogicalStatement captureLogicalStatement()
   {
      if (cursor >= length)
      {
         return null;
      }

      Node start = null;
      Node n = null;
      Node d;

      boolean pipe = false;
      boolean script = false;
      boolean nocommand = false;

      while ((d = nextNode()) != null)
      {
         if (d instanceof StatementTerminator)
         {
            break;
         }

         if (start == null)
         {
            start = n = d;
         }

         if (tokenMatch(d, "|"))
         {
            pipe = true;
            break;
         }
         else if ((nest && !script && tokenIsOperator(d)) || tokenMatch(d, "="))
         {
            script = true;
         }

         if (n != d)
         {
            n.setNext(n = d);
         }
      }

      LogicalStatement logicalStatement = new LogicalStatement(script ? new ScriptNode(start, nocommand) : start);

      if (pipe)
      {
         PipeNode pipeNode = new PipeNode(captureLogicalStatement());
         logicalStatement.setNext(pipeNode);
      }

      return logicalStatement;
   }

   private String shellToMVEL(final String subStmt, final boolean noShellCall)
   {
      StringAppender buf = new StringAppender();

      boolean stmtStart = true;
      boolean openShellCall = false;
      boolean scriptOnly = false;

      Nest nest = new Nest();

      for (int i = 0; i < subStmt.length(); i++)
      {
         if (stmtStart)
         {
            while ((i < subStmt.length()) && isWhitespace(subStmt.charAt(i)))
               i++;

            if (i >= subStmt.length())
            {
               break;
            }

            int firstToken = getEndOfToken(subStmt, i);
            String tk = subStmt.substring(i, firstToken).trim();
            if (!noShellCall && (tk.charAt(0) != '@') && ((firstToken == -1) || !isReservedWord(tk)))
            {
               buf.append("shell(\"");
               openShellCall = true;
            }
            else
            {
               scriptOnly = true;
               stmtStart = false;
               if (tk.charAt(0) == '@')
               {
                  continue;
               }
            }

            stmtStart = false;
         }

         switch (subStmt.charAt(i))
         {
         case '\\':
            buf.append("\\");
            if (nest.isLiteral())
            {
               buf.append("\\");
            }
            buf.append(subStmt.charAt(++i));
            if (subStmt.charAt(i) == '\\')
            {
               buf.append("\\");
            }

            break;

         case '\'':
            nest.nestSingleQuote();
            buf.append(subStmt.charAt(i));
            break;

         case '"':
            nest.nestDoubleQuote();
            if (openShellCall)
            {
               buf.append("\\\"");
            }
            else
            {
               buf.append(subStmt.charAt(i));
            }
            break;

         case '(':
            if (!nest.isLiteral())
            {
               nest.bracket++;
            }
            buf.append(subStmt.charAt(i));
            break;

         case '{':
            buf.append(subStmt.charAt(i));

            if (!nest.isLiteral())
            {
               int start = ++i;
               buf.append(shellToMVEL(subStmt.substring(start,
                        i = balancedCapture(subStmt.toCharArray(), i, '{')), false)).append('}');
            }

            break;

         case '[':
            if (!nest.isLiteral())
            {
               nest.square++;
            }
            buf.append(subStmt.charAt(i));
            break;

         case ')':
            if (!nest.isLiteral())
            {
               nest.bracket--;
            }
            buf.append(subStmt.charAt(i));
            break;

         case '}':
            if (!nest.isLiteral())
            {
               nest.curly--;
            }
            buf.append(subStmt.charAt(i));
            break;

         case ']':
            if (!nest.isLiteral())
            {
               nest.square--;
            }
            buf.append(subStmt.charAt(i));
            break;

         case ';':
            if (!nest.isLiteral())
            {
               if (openShellCall)
               {
                  buf.append("\")");
                  openShellCall = false;
               }

               stmtStart = true;
               scriptOnly = false;
            }

            buf.append(subStmt.charAt(i));
            break;

         case '$':
            if (!scriptOnly)
            {
               buf.append("\"+");

               int start = ++i;
               i = captureToken(i, subStmt.length(), subStmt.toCharArray());
               buf.append(subStmt.substring(start, i));

               if (i < subStmt.length())
               {
                  buf.append("+\"");
                  i--;
               }
               else
               {
                  buf.append(")");
                  openShellCall = false;
               }
            }
            else
            {
               if (++i < subStmt.length())
               {
                  buf.append(subStmt.charAt(i));
               }
            }
            break;

         default:
            buf.append(subStmt.charAt(i));
         }

      }

      if (nest.isLiteral())
      {
         throw new RuntimeException("unterminated string literal while parsing script");
      }

      if (nest.isNested())
      {
         throw new RuntimeException("unterminated nest while parsing script");
      }

      if (openShellCall)
      {
         buf.append("\")");
      }

      return buf.toString();
   }

   private int getEndOfToken(final String s, final int offset)
   {
      return captureToken(offset, s.length(), s.toCharArray());
   }

   private String captureToken()
   {
      int start = cursor;
      cursor = captureToken(cursor, length, expr);
      return new String(expr, start, cursor - start).replace("\\ ", " ");
   }

   private static int captureToken(int cursor, final int length, final char[] expr)
   {
      if (cursor >= length)
      {
         return length;
      }

      int start = cursor;

      if (isTokenPart(expr[cursor]))
      {
         boolean capturing = true;

         do
         {
            while ((cursor != length) && isTokenPart(expr[cursor]))
            {
               if (expr[cursor] == '\\' && cursor + 1 < length && expr[cursor + 1] == ' ')
               {
                  cursor++; // make sure '\ ' are included in the token
               }
               cursor++;
            }

            if (cursor == length)
            {
               capturing = false;
            }
            else
            {
               int c = nextNonBlank(cursor, expr);
               if ((c == -1) || ((expr[cursor] != '(') && (expr[cursor] != '[')))
               {
                  capturing = false;
               }
               else
               {
                  cursor = balancedCapture(expr, cursor, expr[cursor]) + 1;
               }
            }

         }
         while (capturing);

      }
      else
      {
         Skip: while (cursor != length)
         {
            switch (expr[cursor])
            {
            case ' ':
            case '\t':
            case '\r':
            case ';':
            case '=':
               break Skip;

            default:
               if (isTokenPart(expr[cursor]))
               {
                  break Skip;
               }
            }
            cursor++;
         }
      }

      if (cursor == start)
      {
         cursor++;
      }
      return cursor;
   }

   private void skipWhitespace()
   {
      while ((cursor < length) && ParseTools.isWhitespace(expr[cursor]))
         cursor++;
   }

   public void skipToEOS()
   {
      while ((cursor < length) && (expr[cursor] != ';'))
      {
         switch (expr[cursor])
         {
         case '{':
         case '(':
         case '"':
         case '\'':
            cursor = ParseTools.balancedCapture(expr, cursor, expr[cursor]);
            break;
         }
         cursor++;
      }
   }

   private void addNode(final Node n)
   {
      if (node == null)
      {
         firstNode = node = n;
      }
      else
      {
         node.setNext(node = n);
      }
   }

   private void expectNext(final char c)
   {
      while ((cursor != length) && (expr[cursor] != c))
         cursor++;

      if ((cursor == length) || (expr[cursor] != c))
      {
         throw new RuntimeException("expected '('");
      }
   }

   private static char nextNonBlank(int cursor, final char[] expr)
   {
      while ((cursor != expr.length) && isWhitespace(expr[cursor]))
         cursor++;

      if (cursor == expr.length)
      {
         return (char) -1;
      }
      else
      {
         return expr[cursor];
      }
   }

   private static boolean tokenIsOperator(final Node n)
   {
      return (n instanceof TokenNode) && Parse.isOperator(((TokenNode) n).getValue());
   }

   public static boolean tokenMatch(final Node n, final String text)
   {
      return (n instanceof TokenNode) && ((TokenNode) n).getValue().equals(text);
   }

   private static class Nest
   {
      int bracket = 0;
      int curly = 0;
      int square = 0;
      int doubleQuote = 0;
      int singleQuote = 0;

      boolean isNested()
      {
         return bracket + curly + square != 0;
      }

      boolean isLiteral()
      {
         return doubleQuote + singleQuote != 0;
      }

      public void nestDoubleQuote()
      {
         if (doubleQuote == 0)
         {
            doubleQuote = 1;
         }
         else
         {
            doubleQuote = 0;
         }
      }

      public void nestSingleQuote()
      {
         if (singleQuote == 0)
         {
            singleQuote = 1;
         }
         else
         {
            singleQuote = 0;
         }
      }
   }
}
