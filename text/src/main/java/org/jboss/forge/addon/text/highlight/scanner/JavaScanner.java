/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.StringScanner;
import org.jboss.forge.addon.text.highlight.TokenType;
import org.jboss.forge.addon.text.highlight.WordList;
import org.jboss.forge.addon.text.highlight.scanner.java.BuiltInTypes;

/*
 * Based on https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/java.rb
 * Last update sha: 1cdf0e17af6c280dc12130a9200d8196b056bbe9
 */
public class JavaScanner implements Scanner
{

   public static final Pattern SPACE = Pattern.compile("\\s+|\\n");
   public static final Pattern COMMENT = Pattern.compile(
            "// [^\\n\\\\]* (?: \\\\. [^\\n\\\\]* )* | /\\* (?: .*? \\*/ | .* )", Pattern.DOTALL | Pattern.COMMENTS);
   public static final Pattern IDENT = Pattern.compile("[a-zA-Z_][A-Za-z_0-9]*");
   public static final Pattern OPERATORS = Pattern
            .compile("\\.(?!\\d)|[,?:()\\[\\]}]|--|\\+\\+|&&|\\|\\||\\*\\*=?|[-+*\\/%^~&|<>=!]=?|<<<?=?|>>>?=?");
   public static final Pattern STRING_CONTENT_PATTERN_SINGLE = Pattern.compile("[^\\\\']+");
   public static final Pattern STRING_CONTENT_PATTERN_DOUBLE = Pattern.compile("[^\\\\\"]+");
   public static final Pattern STRING_CONTENT_PATTERN_MULTI_LINE = Pattern.compile("[^\\\\\\/]+");
   public static final Pattern ESCAPE = Pattern.compile("[bfnrtv\\n\\\\'\"]|x[a-fA-F0-9]{1,2}|[0-7]{1,3}");
   public static final Pattern UNICODE_ESCAPE = Pattern.compile("u[a-fA-F0-9]{4}|U[a-fA-F0-9]{8}");
   public static final Pattern ANNOTATION = Pattern.compile("@" + IDENT.pattern());
   public static final Pattern PACKAGE = Pattern.compile(IDENT.pattern() + "(?:\\." + IDENT.pattern() + ")*");
   public static final Pattern IDENT_OR_ARRAY_TYPE = Pattern.compile(IDENT.pattern() + "|\\[\\]");
   public static final Pattern SEMI_COLON = Pattern.compile(";");
   public static final Pattern OPEN_BRAKCET = Pattern.compile("\\{");
   public static final Pattern ANY_WORD = Pattern.compile("[\\d.]");
   public static final Pattern HEX = Pattern.compile("0[xX][0-9A-Fa-f]+");
   public static final Pattern OCTAL = Pattern.compile("(?>0[0-7]+)(?![89.eEfF])");
   public static final Pattern FLOAT = Pattern
            .compile("\\d+[fFdD]|\\d*\\.\\d+(?:[eE][+-]?\\d+)?[fFdD]?|\\d+[eE][+-]?\\d+[fFdD]?");
   public static final Pattern INTEGER = Pattern.compile("\\d+[lL]?");
   public static final Pattern START_STRING = Pattern.compile("[\"']");
   public static final Pattern END_STRING = Pattern.compile("[\"'\\/]");
   public static final Pattern STRING_CONTENT = Pattern.compile(
            "\\\\(?:" + ESCAPE.pattern() + "|" + UNICODE_ESCAPE.pattern() + ")", Pattern.DOTALL);
   public static final Pattern STRING_CONTENT_2 = Pattern.compile("\\\\.", Pattern.DOTALL);
   public static final Pattern END_GROUP = Pattern.compile("\\\\|$");

   public enum State
   {
      initial,
      string
   }

   public static final String[] KEYWORDS = new String[] {
            "assert", "break", "case", "catch", "continue", "default", "do", "else",
            "finally", "for", "if", "instanceof", "import", "new", "package",
            "return", "switch", "throw", "try", "typeof", "while", "debugger", "export" };
   public static final String[] RESERVED = new String[] {
            "const", "goto" };
   public static final String[] CONSTANTS = new String[] {
            "false", "null", "true" };
   public static final String[] MAGIC_VARIABLES = new String[] {
            "this", "super" };
   public static final String[] TYPES = new String[] {
            "boolean", "byte", "char", "class", "double", "enum", "float", "int",
            "interface", "long", "short", "void", "[]" };
   public static final String[] DIRECTIVES = new String[] {
            "abstract", "extends", "final", "implements", "native", "private",
            "protected", "public", "static", "strictfp", "synchronized", "throws",
            "transient", "volatile" };

   public static final WordList<TokenType> IDENT_KIND = new WordList<TokenType>(TokenType.ident)
            .add(KEYWORDS, TokenType.keyword)
            .add(RESERVED, TokenType.reserved)
            .add(CONSTANTS, TokenType.predefined_constant)
            .add(MAGIC_VARIABLES, TokenType.local_variable)
            .add(TYPES, TokenType.type)
            .add(BuiltInTypes.PREDEFINED_TYPES, TokenType.predefined_type)
            .add(BuiltInTypes.EXCEPTION_TYPES, TokenType.exception)
            .add(DIRECTIVES, TokenType.directive);

   public static final Map<String, Pattern> STRING_CONTENT_PATTERN = new HashMap<String, Pattern>();
   {
      STRING_CONTENT_PATTERN.put("'", STRING_CONTENT_PATTERN_SINGLE);
      STRING_CONTENT_PATTERN.put("\"", STRING_CONTENT_PATTERN_DOUBLE);
      STRING_CONTENT_PATTERN.put("/", STRING_CONTENT_PATTERN_MULTI_LINE);
   }

   public static final Type TYPE = new Type("JAVA", "\\.(java)$");

   @Override
   public Type getType() {
      return TYPE;
   }

   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options)
   {
      State state = State.initial;
      String string_delimiter = null;
      TokenType package_name_expected = null;
      boolean class_name_follows = false;
      boolean last_token_dot = false;

      while (source.hasMore())
      {
         MatchResult m = null;

         switch (state)
         {
         case initial:
            if ((m = source.scan(SPACE)) != null)
            {
               encoder.textToken(m.group(), TokenType.space);
               continue;
            }
            else if ((m = source.scan(COMMENT)) != null)
            {
               encoder.textToken(m.group(), TokenType.comment);
               continue;
            }
            else if (package_name_expected != null && (m = source.scan(PACKAGE)) != null)
            {
               encoder.textToken(m.group(), package_name_expected);
            }
            else if ((m = source.scan(IDENT_OR_ARRAY_TYPE)) != null)
            {
               String match = m.group();
               TokenType kind = IDENT_KIND.lookup(match);
               if (last_token_dot)
               {
                  kind = TokenType.ident;
               }
               else if (class_name_follows)
               {
                  kind = TokenType.class_;
                  class_name_follows = false;
               }
               else
               {
                  if ("import".equals(match))
                  {
                     package_name_expected = TokenType.include;
                  }
                  else if ("package".equals(match))
                  {
                     package_name_expected = TokenType.namespace;
                  }
                  else if ("class".equals(match) || "interface".equals(match))
                  {
                     class_name_follows = true;
                  }
               }
               encoder.textToken(match, kind);
            }
            else if ((m = source.scan(OPERATORS)) != null)
            {
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if ((m = source.scan(SEMI_COLON)) != null)
            {
               package_name_expected = null;
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if ((m = source.scan(OPEN_BRAKCET)) != null)
            {
               class_name_follows = false;
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if ((m = source.check(ANY_WORD)) != null)
            {
               if ((m = source.scan(HEX)) != null)
               {
                  encoder.textToken(m.group(), TokenType.hex);
               }
               else if ((m = source.scan(OCTAL)) != null)
               {
                  encoder.textToken(m.group(), TokenType.octal);
               }
               else if ((m = source.scan(FLOAT)) != null)
               {
                  encoder.textToken(m.group(), TokenType.float_);
               }
               else if ((m = source.scan(INTEGER)) != null)
               {
                  encoder.textToken(m.group(), TokenType.integer);
               }
            }
            else if ((m = source.scan(START_STRING)) != null)
            {
               state = State.string;
               encoder.beginGroup(TokenType.string);
               string_delimiter = m.group();
               encoder.textToken(m.group(), TokenType.delimiter);
            }
            else if ((m = source.scan(ANNOTATION)) != null)
            {
               encoder.textToken(m.group(), TokenType.annotation);
            }
            else
            {
               encoder.textToken(source.next(), TokenType.error);
            }
            break;
         case string:
            if ((m = source.scan(STRING_CONTENT_PATTERN.get(string_delimiter))) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(END_STRING)) != null)
            {
               encoder.textToken(m.group(), TokenType.delimiter);
               encoder.endGroup(TokenType.string);
               state = State.initial;
               string_delimiter = null;
            }
            else if (state == State.string && (m = source.scan(STRING_CONTENT)) != null)
            {
               if ("'".equals(string_delimiter) && !("\\\\".equals(m.group()) || "\\'".equals(m.group())))
               {
                  encoder.textToken(m.group(), TokenType.content);
               }
               else
               {
                  encoder.textToken(m.group(), TokenType.char_);
               }
            }
            else if ((m = source.scan(STRING_CONTENT_2)) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(END_GROUP)) != null)
            {
               encoder.endGroup(TokenType.string);
               state = State.initial;
               if (!m.group().isEmpty())
               {
                  encoder.textToken(m.group(), TokenType.error);
               }
            }
            else
            {
               throw new RuntimeException("else case \" reached; " + source.peek(1) + " in " + getClass());
            }
            break;
         default:
            throw new RuntimeException("unknown state " + state);
         }
         if (m != null)
         {
            last_token_dot = (".".equals(m.group()));
         }
      }
      if (state == State.string)
      {
         encoder.endGroup(TokenType.string);
      }
   }
}
