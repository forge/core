/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.fshparser;

import static java.lang.Character.isJavaIdentifierPart;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

import org.mvel2.MVEL;

/**
 * @author Mike Brock .
 */
public abstract class Parse
{
   private static final Set<String> reservedWords = new HashSet<String>();
   private static final Set<String> operators = new HashSet<String>();

   static
   {
      reservedWords.add("if");
      reservedWords.add("else");
      reservedWords.add("for");
      reservedWords.add("new");
      reservedWords.add("return");
      reservedWords.add("do");
      reservedWords.add("while");
      reservedWords.add("def");

      operators.add("+");
      operators.add("-");
      operators.add("/");
      operators.add("*");
      operators.add("%");
      operators.add("&&");
      operators.add("||");
      operators.add("=");
   }

   public static boolean isReservedWord(String word)
   {
      return reservedWords.contains(word);
   }

   public static boolean isTokenPart(char c)
   {
      switch (c)
      {
      case ':':
      case '.':
      case '-':
      case '\\':
      case '/':
      case '%':
      case '+':
      case '*':
      case '?':
      case '~':
      case '#':
      case '$':
      case '[':
      case ']':

         return true;
      default:
         return isJavaIdentifierPart(c);
      }
   }

   public static boolean isOperator(String str)
   {
      return operators.contains(str);
   }

   public static String disassemble(Node n)
   {
      if (n == null)
      {
         return "";
      }

      StringBuilder build = new StringBuilder();

      do
      {
         if (n instanceof PipeNode)
         {
            build.append('|')
                     .append(disassemble(((NestedNode) n).getNest()));
         }
         else if (n instanceof NestedNode)
         {
            build.append('(')
                     .append(disassemble(((NestedNode) n).getNest()))
                     .append(')');
         }
         else if (n instanceof TokenNode)
         {
            build.append(((TokenNode) n).getValue());
         }

      }
      while ((n = n.getNext()) != null);

      return build.toString();
   }

   public static String queueToString(Queue<String> tokens)
   {
      StringBuilder sb = new StringBuilder();

      Iterator<String> iter = tokens.iterator();
      while (iter.hasNext())
      {
         sb.append(iter.next());
         if (iter.hasNext())
         {
            sb.append(" ");
         }
      }

      return sb.toString();
   }

   public static String executeScript(ScriptNode node, final FSHRuntime runtime)
   {
      String toExec = queueToString(new AutoReducingQueue(node.getNest(), runtime));

      // System.out.println("\n----\n" + toExec + "\n========\n");

      Object r = MVEL.eval(toExec, runtime, runtime.getShell().getEnvironment().getProperties());
      if (r == null)
      {
         return null;
      }
      else
      {
         return String.valueOf(r);
      }
   }
}
