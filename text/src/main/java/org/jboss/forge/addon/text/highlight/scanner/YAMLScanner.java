/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.StringScanner;
import org.jboss.forge.addon.text.highlight.TokenType;

/*
 * Based on https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/yaml.rb
 * Last update sha: 603ff7d0b14521cfd0408aa68e2e1cb6ea9086bc
 *
 */
public class YAMLScanner implements Scanner
{
   private static final Pattern SPACE = Pattern.compile(" +[\\t ]*");
   private static final Pattern SPACE_NEWLINE = Pattern.compile("\\n+");
   private static final Pattern COMMENT = Pattern.compile("#.*");
   private static final Pattern HEAD = Pattern.compile("---|\\.\\.\\.");
   private static final Pattern DOCTYPE = Pattern.compile("%.*");
   private static final Pattern STRING = Pattern.compile("(?:\"[^\"]*\")(?=: |:$)", Pattern.MULTILINE);
   private static final Pattern DOUBLE_QUOTE = Pattern.compile("\"");
   private static final Pattern COMMENT_ONELINE = Pattern.compile(" [^\"\\\\]* (?: \\\\. [^\"\\\\]* )* ", Pattern.DOTALL|Pattern.COMMENTS);
   private static final Pattern LINE_CONTINUE = Pattern.compile("[|>][-+]?");
   private static final Pattern STRING_ENDLINE = Pattern.compile("(?![!\"*&]).+?(?=$|\\s+#)", Pattern.MULTILINE);
   private static final Pattern OPERATOR = Pattern.compile("[-:](?= |$)", Pattern.MULTILINE);
   private static final Pattern OPERATOR_BRACKETS = Pattern.compile("[,{}\\[\\]]");
   private static final Pattern KEY = Pattern.compile("[-\\w.()\\/ ]*\\S(?= *:(?: |$))", Pattern.MULTILINE);
   private static final Pattern KEY_2 = Pattern.compile("(?:\"[^\"\\n]*\"|'[^'\\n]*')(?= *:(?: |$))", Pattern.MULTILINE);
   private static final Pattern TYPE_EXP = Pattern.compile("(![\\w\\/]+)(:([\\w:]+))?");
   private static final Pattern VARIABLE = Pattern.compile("&\\S+");
   private static final Pattern GLOBAL_VARIABLE = Pattern.compile("\\*\\w+");
   private static final Pattern CLASS_VARIABLE = Pattern.compile("<<");
   private static final Pattern OCTAL = Pattern.compile("\\d\\d:\\d\\d:\\d\\d");
   private static final Pattern OCTAL_2 = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d(\\.\\d+)? [-+]\\d\\d:\\d\\d");
   private static final Pattern SYMBOL = Pattern.compile(":\\w+");
   private static final Pattern ERROR = Pattern.compile("[^:\\s]+(:(?! |$)[^:\\s]*)* .*");
   private static final Pattern ERROR_2 = Pattern.compile("[^:\\s]+(:(?! |$)[^:\\s]*)*");

   public enum State
   {
      initial,
      value,
      colon
   }

   public static final Type TYPE = new Type("YAML", "\\.(yml|yaml)$");

   @Override
   public Type getType() {
      return TYPE;
   }

   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options) {
      Context contxt = new Context();
      contxt.state = State.initial;
      contxt.key_indent = null;

      while (source.hasMore())
      {
         MatchResult m = null;
         if(source.isBeginningOfLine())
         {
            contxt.key_indent = null;
         }

         if ((m = source.scan(SPACE)) != null)
         {
            encoder.textToken(m.group(), TokenType.space);
         }
         else if ((m = source.scan(SPACE_NEWLINE)) != null)
         {
            encoder.textToken(m.group(), TokenType.space);
            if (m.group().indexOf("\n") != -1)
            {
               contxt.state = State.initial;
            }
         }
         else if ((m = source.scan(COMMENT)) != null)
         {
            encoder.textToken(m.group(), TokenType.comment);
         }
         else if (source.isBeginningOfLine() && head_doctype(source, encoder))
         {
            continue;
         }
         else if (contxt.state == State.value && delimiter(source, encoder, contxt))
         {
            continue;
         }
         else if (value(source, encoder, contxt))
         {
            continue;
         }
         else
         {
            if(!source.hasMore())
            {
               throw new RuntimeException("unexpected end");
            }
            encoder.textToken(source.next(), TokenType.error);
         }
      }
   }

   private boolean head_doctype(StringScanner source, Encoder encoder) {
      MatchResult m;
      if ( (m = source.scan(HEAD)) != null)
      {
         encoder.beginGroup(TokenType.head);
         encoder.textToken(m.group(), TokenType.head);
         encoder.endGroup(TokenType.head);
         return true;
      }
      else if ( (m = source.scan(DOCTYPE)) != null)
      {
         encoder.textToken(m.group(), TokenType.doctype);
         return true;
      }
      return false;
   }

   private boolean delimiter(StringScanner source, Encoder encoder, Context context) {
      MatchResult m;
      int string_indent = 0;
      if ( source.check(STRING) == null && (m = source.scan(DOUBLE_QUOTE)) != null)
      {
         encoder.beginGroup(TokenType.string);
         encoder.textToken(m.group(), TokenType.delimiter);
         if ( (m = source.scan(COMMENT_ONELINE)) != null && !"".equals(m.group()))
         {
            encoder.textToken(m.group(), TokenType.content);
         }
         if ( (m = source.scan(DOUBLE_QUOTE)) != null)
         {
            encoder.textToken(m.group(), TokenType.delimiter);
         }
         encoder.endGroup(TokenType.string);
         return true;
      }
      else if ( (m = source.scan(LINE_CONTINUE)) != null)
      {
         encoder.beginGroup(TokenType.string);
         encoder.textToken(m.group(), TokenType.delimiter);
         string_indent = context.key_indent != null ? context.key_indent:source.column(source.index() - m.group().length())-1;
         if ( (m = source.scan(Pattern.compile("(?:\\n+ {" + (string_indent +1) + "}.*)+"))) != null)
         {
            encoder.textToken(m.group(), TokenType.content);
         }
         encoder.endGroup(TokenType.string);
         return true;
      }
      else if ( (m = source.scan(STRING_ENDLINE)) != null)
      {
         encoder.beginGroup(TokenType.string);
         encoder.textToken(m.group(), TokenType.content);
         string_indent = context.key_indent != null ? context.key_indent:source.column(source.index() - m.group().length())-1;
         if ( (m = source.scan(Pattern.compile("(?:\\n+ {" + (string_indent +1) + "}.*)+"))) != null)
         {
            encoder.textToken(m.group(), TokenType.content);
         }

         encoder.endGroup(TokenType.string);
         return true;
      }
      return false;
   }

   private boolean value(StringScanner source, Encoder encoder, Context context) {
      MatchResult m;

      if ( (m = source.scan(OPERATOR)) != null)
      {
         if (context.state == State.colon && (m.group().equals(":") | m.group().equals("-")))
         {
            context.state = State.value;
         }
         else if (context.state == State.initial && m.group().equals("-"))
         {
            context.state = State.value;
         }
         encoder.textToken(m.group(), TokenType.operator);
         return true;
      }
      else if ( (m = source.scan(OPERATOR_BRACKETS)) != null)
      {
         encoder.textToken(m.group(), TokenType.operator);
         return true;
      }
      else if ( context.state == State.initial && (m = source.scan(KEY)) != null)
      {
         encoder.textToken(m.group(), TokenType.key);
         context.key_indent = source.column(source.index() - m.group().length()) - 1;
         context.state = State.colon;
         return true;
      }
      else if ( (m = source.scan(KEY_2)) != null)
      {
         encoder.beginGroup(TokenType.key);
         String match = m.group();
         encoder.textToken(match.substring(0, 1), TokenType.delimiter);
         if (match.length() > 2)
         {
            encoder.textToken(match.substring(1, match.length()-1), TokenType.content);
         }
         encoder.textToken(match.substring(match.length()-1), TokenType.delimiter);
         encoder.endGroup(TokenType.key);
         context.key_indent = source.column(source.index() - match.length()) - 1;
         context.state = State.colon;
         return true;
      }
      else if ( (m = source.scan(TYPE_EXP)) != null)
      {
         encoder.textToken(m.group(1), TokenType.type);
         if (m.group(2) != null)
         {
            encoder.textToken(":", TokenType.operator);
            encoder.textToken(m.group(3), TokenType.class_);
         }
         return true;
      }
      else if ( (m = source.scan(VARIABLE)) != null)
      {
         encoder.textToken(m.group(), TokenType.variable);
         return true;
      }
      else if ( (m = source.scan(GLOBAL_VARIABLE)) != null)
      {
         encoder.textToken(m.group(), TokenType.global_variable);
         return true;
      }
      else if ( (m = source.scan(CLASS_VARIABLE)) != null)
      {
         encoder.textToken(m.group(), TokenType.class_variable);
         return true;
      }
      else if ( (m = source.scan(OCTAL)) != null)
      {
         encoder.textToken(m.group(), TokenType.octal);
         return true;
      }
      else if ( (m = source.scan(OCTAL_2)) != null)
      {
         encoder.textToken(m.group(), TokenType.octal);
         return true;
      }
      else if ( (m = source.scan(SYMBOL)) != null)
      {
         encoder.textToken(m.group(), TokenType.symbol);
         return true;
      }
      else if ( (m = source.scan(ERROR)) != null)
      {
         encoder.textToken(m.group(), TokenType.error);
         return true;
      }
      else if ( (m = source.scan(ERROR_2)) != null)
      {
         encoder.textToken(m.group(), TokenType.error);
         return true;
      }
      return false;
   }

   private static class Context {
      State state;
      Integer key_indent = null;
   }
}
