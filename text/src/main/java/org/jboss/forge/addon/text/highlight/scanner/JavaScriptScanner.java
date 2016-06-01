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
import org.jboss.forge.addon.text.highlight.Syntax;
import org.jboss.forge.addon.text.highlight.TokenType;
import org.jboss.forge.addon.text.highlight.WordList;

/*
 * https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/java_script.rb
 * Last update sha: 33f1a738ea85ea40a5fe760ac2591065f3e29033
 *
 */
public class JavaScriptScanner implements Scanner
{

   // The actual JavaScript keywords.
   private static final String[] KEYWORDS = new String[] {
            "break", "case", "catch", "continue", "default", "delete", "do", "else",
            "finally", "for", "function", "if", "in", "instanceof", "new",
            "return", "switch", "throw", "try", "typeof", "var", "void", "while", "with" };

   private static final String[] PREDEFINED_CONSTANTS = new String[] {
            "false", "null", "true", "undefined", "NaN", "Infinity" };

   // arguments was introduced in JavaScript 1.4
   private static final String[] MAGIC_VARIABLES = new String[] {
            "this", "arguments" };

   private static final WordList<Boolean> KEYWORDS_EXPECTING_VALUE = new WordList<Boolean>(false)
            .add(new String[] { "case", "delete", "in", "instanceof", "new", "return", "throw", "typeof", "with" },
                     true);

   // Reserved for future use.
   private static final String[] RESERVED_WORDS = new String[] {
            "abstract", "boolean", "byte", "char", "class", "debugger", "double", "enum", "export", "extends",
            "final", "float", "goto", "implements", "import", "int", "interface", "long", "native", "package",
            "private", "protected", "public", "short", "static", "super", "synchronized", "throws", "transient",
            "volatile" };

   private static final WordList<TokenType> IDENT_KIND = new WordList<TokenType>(TokenType.ident)
            .add(RESERVED_WORDS, TokenType.reserved)
            .add(PREDEFINED_CONSTANTS, TokenType.predefined_constant)
            .add(MAGIC_VARIABLES, TokenType.local_variable)
            .add(KEYWORDS, TokenType.keyword);

   private static final Pattern ESCAPE = Pattern.compile(" [bfnrtv\\n\\\\'\"] | x[a-fA-F0-9]{1,2} | [0-7]{1,3} ",
            Pattern.COMMENTS);
   private static final Pattern UNICODE_ESCAPE = Pattern.compile(" u[a-fA-F0-9]{4} | U[a-fA-F0-9]{8} ",
            Pattern.COMMENTS);
   private static final Pattern REGEXP_ESSCAPE = Pattern.compile(" [bBdDsSwW] ", Pattern.COMMENTS);
   private static final Map<String, Pattern> STRING_CONTENT_PATTERN = new HashMap<String, Pattern>();
   {
      STRING_CONTENT_PATTERN.put("'", Pattern.compile("[^\\\\']+"));
      STRING_CONTENT_PATTERN.put("\"", Pattern.compile("[^\\\\\"]+"));
      STRING_CONTENT_PATTERN.put("/", Pattern.compile("[^\\\\\\/]+"));
   }
   private static final Map<String, Pattern> KEY_CHECK_PATTERN = new HashMap<String, Pattern>();
   {
      KEY_CHECK_PATTERN.put("'",
               Pattern.compile(" (?> [^\\\\']* (?: \\\\. [^\\\\']* )* ) ' \\s* : ", Pattern.COMMENTS | Pattern.DOTALL));
      KEY_CHECK_PATTERN.put(
               "\"",
               Pattern.compile(" (?> [^\\\\\"]* (?: \\\\. [^\\\\\"]* )* ) \" \\s* : ", Pattern.COMMENTS
                        | Pattern.DOTALL));
   }

   private static final Pattern SPACE = Pattern.compile(" \\s+ | \\\\\\n ", Pattern.COMMENTS);
   private static final Pattern COMMENT = Pattern
            .compile(" // [^\\n\\\\]* (?: \\\\. [^\\n\\\\]* )* | /\\* (?: .*? \\*/ | .*() ) ", Pattern.COMMENTS
                     | Pattern.DOTALL);
   private static final Pattern COMMENT_MULTILINE = Pattern.compile(" .*? \\*", Pattern.COMMENTS | Pattern.DOTALL);
   private static final Pattern COMMENT_MULTILINE_CONTENT = Pattern.compile(" .+ ", Pattern.COMMENTS | Pattern.DOTALL);
   private static final Pattern NUMBER = Pattern.compile("\\.?\\d");
   private static final Pattern HEX = Pattern.compile("0[xX][0-9A-Fa-f]+");
   private static final Pattern OCTAL = Pattern.compile("(?>0[0-7]+)(?![89.eEfF])");
   private static final Pattern FLOAT = Pattern
            .compile("\\d+[fF]|\\d*\\.\\d+(?:[eE][+-]?\\d+)?[fF]?|\\d+[eE][+-]?\\d+[fF]?");
   private static final Pattern INTEGER = Pattern.compile("\\d+");
   private static final Pattern HTML = Pattern.compile("<(\\p{Alpha}\\w*) (?: [^\\/>]*\\/> | .*?<\\/\\1>)",
            Pattern.COMMENTS | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
   private static final Pattern OPERATOR = Pattern.compile(" [-+*=<>?:;,!&^|(\\[{~%]+ | \\.(?!\\d) ", Pattern.COMMENTS);
   private static final Pattern OPERATOR_END = Pattern.compile(" [)\\]}]+ ", Pattern.COMMENTS);
   private static final Pattern IDENT = Pattern.compile(" [$a-zA-Z_][A-Za-z_0-9$]* ", Pattern.COMMENTS);
   private static final Pattern FUNCTION = Pattern.compile("\\s*[=:]\\s*function\\b");
   private static final Pattern KEY = Pattern.compile("\\s*:");
   private static final Pattern ARRAY_KEY = Pattern.compile("[\"']");
   private static final Pattern REGEXP = Pattern.compile("\\/");
   private static final Pattern DELIMITER = Pattern.compile("[\"'\\/]");
   private static final Pattern MODIFIER = Pattern.compile("[gim]+");
   private static final Pattern CONTENT = Pattern
            .compile(" \\\\ (?: " + ESCAPE.pattern() + " | " + UNICODE_ESCAPE.pattern() + ")", Pattern.COMMENTS
                     | Pattern.DOTALL);
   private static final Pattern CONTENT_2 = Pattern.compile("\\\\.", Pattern.DOTALL);
   private static final Pattern CONTENT_END = Pattern.compile(" \\\\ | $ ", Pattern.COMMENTS);
   private static final Pattern CHAR = Pattern.compile(
            " \\\\ (?: " + ESCAPE.pattern() + " | " + REGEXP_ESSCAPE.pattern() + " | " + UNICODE_ESCAPE.pattern()
                     + " ) ", Pattern.COMMENTS | Pattern.DOTALL);

   public enum State
   {
      initial,
      open_multi_line_comment,
      key,
      string,
      regexp
   }

   public static final Type TYPE = new Type("JAVASCRIPT", "\\.(js)$");

   @Override
   public Type getType() {
      return TYPE;
   }

   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options)
   {

      State state = State.initial;
      String string_delimiter = null;
      boolean value_expected = true;
      boolean key_expected = false;
      boolean function_expected = false;

      while (source.hasMore())
      {
         MatchResult m = null;

         switch (state)
         {
         case initial:

            if ((m = source.scan(SPACE)) != null)
            {
               if (!value_expected && m.group().indexOf("\n") != -1)
               {
                  value_expected = true;
               }
               encoder.textToken(m.group(), TokenType.space);
            }
            else if ((m = source.scan(COMMENT)) != null)
            {
               value_expected = true;
               encoder.textToken(m.group(), TokenType.comment);
               if (m.group(1) != null)
               {
                  state = State.open_multi_line_comment;
               }
            }
            else if ((m = source.check(NUMBER)) != null)
            {
               key_expected = value_expected = false;
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
            else if (value_expected && (m = source.scan(HTML)) != null)
            {
               Syntax.Builder.create()
                        .scannerType(HTMLScanner.TYPE.getName())
                        .encoder(encoder)
                        .execute(m.group());
               value_expected = true;
               continue;
            }
            else if ((m = source.scan(OPERATOR)) != null)
            {
               value_expected = true;
               String last_operator = m.group().substring(m.group().length() - 1);
               key_expected = last_operator.equals("{") || last_operator.equals(",");
               function_expected = false;
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if ((m = source.scan(OPERATOR_END)) != null)
            {
               function_expected = key_expected = value_expected = false;
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if ((m = source.scan(IDENT)) != null)
            {
               TokenType kind = IDENT_KIND.lookup(m.group());
               value_expected = (kind == TokenType.keyword) && KEYWORDS_EXPECTING_VALUE.lookup(m.group());
               if (TokenType.ident == kind)
               {
                  if (m.group().indexOf("$") != -1)
                  {
                     kind = TokenType.predefined;
                  }
                  else if (function_expected)
                  {
                     kind = TokenType.function;
                  }
                  else if (source.check(FUNCTION) != null)
                  {
                     kind = TokenType.function;
                  }
                  else if (key_expected && source.check(KEY) != null)
                  {
                     kind = TokenType.key;
                  }
               }
               function_expected = (kind == TokenType.keyword && m.group().equals("function"));
               key_expected = false;
               encoder.textToken(m.group(), kind);
            }
            else if ((m = source.scan(ARRAY_KEY)) != null)
            {
               if (key_expected && source.check(KEY_CHECK_PATTERN.get(m.group())) != null)
               {
                  state = State.key;
               }
               else
               {
                  state = State.string;
               }
               encoder.beginGroup(TokenType.valueOf(state.name()));
               string_delimiter = m.group();
               encoder.textToken(m.group(), TokenType.delimiter);
            }
            else if (value_expected && (m = source.scan(REGEXP)) != null)
            {
               encoder.beginGroup(TokenType.regexp);
               state = State.regexp;
               string_delimiter = "/";
               encoder.textToken(m.group(), TokenType.delimiter);
            }
            else if ((m = source.scan(REGEXP)) != null)
            {
               value_expected = true;
               key_expected = false;
               encoder.textToken(m.group(), TokenType.operator);
            }
            else
            {
               encoder.textToken(source.next(), TokenType.error);
            }
            break;

         case string:
         case regexp:
         case key:

            if ((m = source.scan(STRING_CONTENT_PATTERN.get(string_delimiter))) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(DELIMITER)) != null)
            {
               encoder.textToken(m.group(), TokenType.delimiter);
               if (State.regexp == state)
               {
                  MatchResult modifiers;
                  if ((modifiers = source.scan(MODIFIER)) != null)
                  {
                     encoder.textToken(modifiers.group(), TokenType.modifier);
                  }
               }
               encoder.endGroup(TokenType.valueOf(state.name()));
               string_delimiter = null;
               key_expected = value_expected = false;
               state = State.initial;
            }
            else if (State.regexp != state && (m = source.scan(CONTENT)) != null)
            {
               if (string_delimiter.equals("'") && !(m.group().equals("\\\\") || m.group().equals("\\'")))
               {
                  encoder.textToken(m.group(), TokenType.content);
               }
               else
               {
                  encoder.textToken(m.group(), TokenType.char_);
               }
            }
            else if (State.regexp == state && (m = source.scan(CHAR)) != null)
            {
               encoder.textToken(m.group(), TokenType.char_);
            }
            else if ((m = source.scan(CONTENT_2)) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(CONTENT_END)) != null)
            {
               encoder.endGroup(TokenType.valueOf(state.name()));
               if (!m.group().isEmpty())
               {
                  encoder.textToken(m.group(), TokenType.error);
               }
               string_delimiter = null;
               key_expected = value_expected = false;
               state = State.initial;
            }
            else
            {
               throw new RuntimeException("else case " + string_delimiter + " reached; " + source.peek(1)
                        + " not handled");
            }
            break;

         case open_multi_line_comment:

            if ((m = source.scan(COMMENT_MULTILINE)) != null)
            {
               state = State.initial;
            }
            else
            {
               m = source.scan(COMMENT_MULTILINE_CONTENT);
            }
            value_expected = true;
            if (m != null)
            {
               encoder.textToken(m.group(), TokenType.comment);
            }

            break;
         default:
            throw new RuntimeException("Unknown state " + state);
         }
      }
   }

}
