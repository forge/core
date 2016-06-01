/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.StringScanner;
import org.jboss.forge.addon.text.highlight.TokenType;
import org.jboss.forge.addon.text.highlight.WordList;

/*
 * https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/groovy.rb
 * Last update sha: e1d5e111d968639fa03a6074cf90535ecc90d0dd
 *
 */
public class GroovyScanner implements Scanner
{
   private static final String[] GROOVY_KEYWORDS = new String[] {
                                "as", "assert", "def", "in"};


   private static final WordList<Boolean> KEYWORDS_EXPECTING_VALUE = new WordList<Boolean>(false)
               .add(new String[] {
                  "case", "instanceof", "new", "return", "throw", "typeof", "while", "as", "assert", "in"
               }, true);

   private static final String[] GROOVY_MAGIC_VARIABLES = new String[] {"it"};

   public static final WordList<TokenType> IDENT_KIND = JavaScanner.IDENT_KIND.clone()
               .add(GROOVY_KEYWORDS, TokenType.keyword)
               .add(GROOVY_MAGIC_VARIABLES, TokenType.local_variable);

   private static final Pattern ESCAPE = Pattern.compile(" [bfnrtv$\\n\\\\'\"] | x[a-fA-F0-9]{1,2} | [0-7]{1,3} ", Pattern.COMMENTS);
   private static final Pattern UNICODE_ESCAPE = Pattern.compile(" u[a-fA-F0-9]{4} ", Pattern.COMMENTS);
   private static final Pattern REGEXP_ESCAPE = Pattern.compile(" [bfnrtv\\n\\\\'\"] | x[a-fA-F0-9]{1,2} | [0-7]{1,3} | \\d | [bBdDsSwW\\/] ", Pattern.COMMENTS);

   private static final Map<String, Pattern> STRING_CONTENT_PATTERN = new HashMap<String, Pattern>();
   {
      STRING_CONTENT_PATTERN.put("'", Pattern.compile("(?>\\\\[^\\\\'\\n]+|[^\\\\'\\n]+)+"));
      STRING_CONTENT_PATTERN.put("\"", Pattern.compile("[^\\\\$\"\\n]+"));
      STRING_CONTENT_PATTERN.put("'''", Pattern.compile("(?>[^\\\\']+|'(?!''))+"));
      STRING_CONTENT_PATTERN.put("\"\"\"", Pattern.compile("(?>[^\\\\$\"]+|\"(?!\"\"))+"));
      STRING_CONTENT_PATTERN.put("/", Pattern.compile("[^\\\\$\\/\\n]+"));
   }

   private static final Pattern SPACE = Pattern.compile(" \\s+ | \\\\\\n ", Pattern.COMMENTS);
   private static final Pattern COMMENT = Pattern.compile("   // [^\\n\\\\]* (?: \\\\. [^\\n\\\\]* )* | /\\* (?: .*? \\*/ | .* ) ", Pattern.DOTALL|Pattern.COMMENTS);
   private static final Pattern DOCTYPE = Pattern.compile("\\#!.*", Pattern.COMMENTS);
   private static final Pattern INCLUDE = Pattern.compile(" (?!as) " + JavaScanner.IDENT.pattern() + " (?: \\. " + JavaScanner.IDENT.pattern() + " )* (?: \\.\\* )? ", Pattern.COMMENTS);
   private static final Pattern IDENT = Pattern.compile(" " + JavaScanner.IDENT.pattern() + " | \\[\\] ", Pattern.COMMENTS);
   private static final Pattern AFTER_DEF = Pattern.compile("\\s*[({]");
   private static final Pattern SEMI_COLON = Pattern.compile(";");
   private static final Pattern START_BRACKET = Pattern.compile("\\{");
   private static final Pattern OPERATOR = Pattern.compile(" \\.\\.<? | \\*?\\.(?!\\d)@? | \\.& | \\?:? | [,?:(\\[] | -[->] | \\+\\+ |\n" + 
         "              && | \\|\\| | \\*\\*=? | ==?~ | <=?>? | [-+*%^~&|>=!]=? | <<<?=? | >>>?=? ", Pattern.COMMENTS);
   private static final Pattern END_BRACKET = Pattern.compile(" [)\\]}] ", Pattern.COMMENTS);
   private static final Pattern NUMBER = Pattern.compile("[\\d.]");
   private static final Pattern HEX = Pattern.compile("0[xX][0-9A-Fa-f]+");
   private static final Pattern OCTAL = Pattern.compile("(?>0[0-7]+)(?![89.eEfF])");
   private static final Pattern FLOAT = Pattern.compile("\\d+[fFdD]|\\d*\\.\\d+(?:[eE][+-]?\\d+)?[fFdD]?|\\d+[eE][+-]?\\d+[fFdD]?");
   private static final Pattern INTEGER = Pattern.compile("\\d+[lLgG]?");

   private static final Pattern MULTI_LINE_DELIMITER = Pattern.compile("'''|\"\"\"");
   private static final Pattern STRING_DELIMITER = Pattern.compile("[\"']");
   private static final Pattern START_REGEXP = Pattern.compile("\\/");
   private static final Pattern ANNOTATION = Pattern.compile(" @ " + JavaScanner.IDENT.pattern() + " ", Pattern.COMMENTS);
   private static final Pattern END_OPERATOR = Pattern.compile("\\/");
   private static final Pattern CONTENT = Pattern.compile(" \\\\ (?: " + ESCAPE.pattern() + " | " + UNICODE_ESCAPE.pattern() + " ) ", Pattern.DOTALL|Pattern.COMMENTS);

   private static final Pattern REGEXP_CONTENT = Pattern.compile(" \\\\ (?: " + REGEXP_ESCAPE.pattern() + " | " + UNICODE_ESCAPE.pattern() + " ) ", Pattern.DOTALL|Pattern.COMMENTS);
   private static final Pattern INLINE_IDENT = Pattern.compile("\\$ " + JavaScanner.IDENT.pattern() +" ", Pattern.DOTALL|Pattern.COMMENTS);
   private static final Pattern INLINE_DELIMITER = Pattern.compile(" \\$ \\{ ", Pattern.COMMENTS);
   private static final Pattern CONTENT_2 = Pattern.compile(" \\$ ", Pattern.DOTALL|Pattern.COMMENTS);
   private static final Pattern CONTENT_3 = Pattern.compile("\\\\. ", Pattern.DOTALL|Pattern.COMMENTS);
   private static final Pattern END_NEWLINE = Pattern.compile(" \\\\ | \\n ", Pattern.COMMENTS);

   public enum State
   {
      initial,
      multiline_string,
      regexp,
      string
   }

   public static final String OPTION_START_STATE = "state";

   public static final Type TYPE = new Type("GROOVY", "\\.(groovy|gvy|gradle)$");

   @Override
   public Type getType() {
      return TYPE;
   }

   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options) {
      State state = State.initial;
      if (options.containsKey(OPTION_START_STATE))
      {
         state = State.valueOf(String.valueOf(options.get(OPTION_START_STATE)));
      }
      // Object[] {State, stringDelimiter, inlineBlockParenDepth}
      Stack<Object[]> inlineBlockStack = new Stack<Object[]>();
      int inlineBlockParenDepth = 0;
      String stringDelimiter = null;
      String last_token = null;
      boolean import_clause = false;
      boolean class_name_follows = false;
      boolean after_def = false;
      boolean value_expected = true;
      TokenType kind = null;

      while (source.hasMore())
      {
         MatchResult m = null;

         switch (state)
         {
         case initial:

            if ((m = source.scan(SPACE)) != null)
            {
               encoder.textToken(m.group(), TokenType.space);
               if(m.group().indexOf("\n") != -1)
               {
                  import_clause = after_def = false;
                  if(!value_expected)
                  {
                     value_expected = true;
                  }
               }
               continue;
            }
            else if ((m = source.scan(COMMENT)) != null)
            {
               value_expected = true;
               after_def = false;
               encoder.textToken(m.group(), TokenType.comment);
            }
            else if ((m = source.scan(DOCTYPE)) != null)
            {
               encoder.textToken(m.group(), TokenType.doctype);
            }
            else if (import_clause && (m = source.scan(INCLUDE)) != null)
            {
               after_def = value_expected = false;
               encoder.textToken(m.group(), TokenType.include);
            }
            else if ((m = source.scan(IDENT)) != null)
            {
               kind = IDENT_KIND.lookup(m.group());
               value_expected = (kind == TokenType.keyword) && KEYWORDS_EXPECTING_VALUE.lookup(m.group());
               if (".".equals(last_token))
               {
                  kind = TokenType.ident;
               }
               else if (class_name_follows)
               {
                  kind = TokenType.class_;
                  class_name_follows = false;
               }
               else if (after_def && source.check(AFTER_DEF) != null)
               {
                  kind = TokenType.method;
                  after_def = false;
               }
               else if (kind == TokenType.ident && !"?".equals(last_token) && source.check(":") != null)
               {
                  kind = TokenType.key;
               }
               else
               {
                  if (m.group().equals("class") || (import_clause && m.group().equals("as")))
                  {
                     class_name_follows = true;
                  }
                  import_clause = (m.group().equals("import"));
                  if(m.group().equals("def"))
                  {
                     after_def = true;
                  }
               }
               encoder.textToken(m.group(), kind);
            }
            else if ((m = source.scan(SEMI_COLON)) != null)
            {
               import_clause = after_def = false;
               value_expected = true;
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if ((m = source.scan(START_BRACKET)) != null)
            {
               class_name_follows = after_def = false;
               value_expected = true;
               encoder.textToken(m.group(), TokenType.operator);
               if (!inlineBlockStack.isEmpty())
               {
                  inlineBlockParenDepth += 1;
               }
            }
            else if ((m = source.scan(OPERATOR)) != null)
            {
               value_expected = true;
               //value_expected = :regexp if match == '~';
               after_def = false;

               encoder.textToken(m.group(), TokenType.operator);
            }
            else if ((m = source.scan(END_BRACKET)) != null)
            {
               value_expected = after_def = false;
               if (!inlineBlockStack.isEmpty() && m.group().equals("}"))
               {
                  inlineBlockParenDepth -= 1;
                  if (inlineBlockParenDepth == 0 ) // # closing brace of inline block reached
                  {
                     encoder.textToken(m.group(),  TokenType.inline_delimiter);
                     encoder.endGroup(TokenType.inline);
                     Object[] inlineBlock = inlineBlockStack.pop();
                     state = (State)inlineBlock[0];
                     stringDelimiter = (String)inlineBlock[1];
                     inlineBlockParenDepth = (int)inlineBlock[2];
                     continue;
                  }
               }
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if (source.check(NUMBER) != null)
            {
               after_def = value_expected = false;
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
            else if ((m = source.scan(MULTI_LINE_DELIMITER)) != null)
            {
               after_def = value_expected = false;
               state = State.multiline_string;
               encoder.beginGroup(TokenType.string);
               stringDelimiter = m.group();
               encoder.textToken(m.group(), TokenType.delimiter);
            }
            else if ((m = source.scan(STRING_DELIMITER)) != null)
            {
               after_def = value_expected = false;
               state = m.group().equals("/") ? State.regexp :State.string;
               encoder.beginGroup(TokenType.valueOf(state.name()));
               stringDelimiter = m.group();
               encoder.textToken(m.group(), TokenType.delimiter);
            }
            else if (value_expected && (m = source.scan(START_REGEXP)) != null)
            {
               after_def = value_expected = false;
               encoder.beginGroup(TokenType.regexp);
               state = State.regexp;
               stringDelimiter = "/";
               encoder.textToken(m.group(), TokenType.delimiter);
            }
            else if ((m = source.scan(ANNOTATION)) != null)
            {
               after_def = value_expected = false;
               encoder.textToken(m.group(), TokenType.annotation);
            }
            else if ((m = source.scan(END_OPERATOR)) != null)
            {
               after_def = false;
               value_expected = true;
               encoder.textToken(m.group(), TokenType.operator);
            }
            else
            {
               encoder.textToken(source.next(), TokenType.error);
            }
            break;

         case string:
         case regexp:
         case multiline_string:

            if ((m = source.scan(STRING_CONTENT_PATTERN.get(stringDelimiter))) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(state == State.multiline_string ? "'''|\"\"\"":"[\"'\\/]")) != null)
            {
               encoder.textToken(m.group(), TokenType.delimiter);
               if (state == State.regexp)
               {
                  MatchResult modifiers = source.scan("[ix]+");
                  if (modifiers != null && !modifiers.group().equals(""))
                  {
                     encoder.textToken(modifiers.group(), TokenType.modifier);
                  }
               }
               if (state == State.multiline_string)
               {
                  state = State.string;
               }
               encoder.endGroup(TokenType.string);
               stringDelimiter = null;
               after_def = value_expected = false;
               state = State.initial;
               continue;
            }
            else if ((state == State.string || state == State.multiline_string) && (m = source.scan(CONTENT)) != null)
            {
               if (stringDelimiter.charAt(0) == '\'' && !(m.group().equals("\\\\") || m.group().equals("\\'")))
               {
                  encoder.textToken(m.group(), TokenType.content);
               }
               else
               {
                  encoder.textToken(m.group(), TokenType.char_);
               }
            }
            else if (state == State.regexp && (m = source.scan(REGEXP_CONTENT)) != null)
            {
               encoder.textToken(m.group(), TokenType.char_);
            }
            else if ((m = source.scan(INLINE_IDENT)) != null)
            {
               encoder.beginGroup(TokenType.inline);
               encoder.textToken("$", TokenType.inline_delimiter);
               String ident = m.group().substring(1);
               encoder.textToken(ident, IDENT_KIND.lookup(ident));
               encoder.endGroup(TokenType.inline);
            }
            else if ((m = source.scan(INLINE_DELIMITER)) != null)
            {
               encoder.beginGroup(TokenType.inline);
               encoder.textToken(m.group(), TokenType.inline_delimiter);
               inlineBlockStack.push(new Object[] {state, stringDelimiter, inlineBlockParenDepth});
               inlineBlockParenDepth = 1;
               state = State.initial;
            }
            else if ((m = source.scan(CONTENT_2)) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(CONTENT_3)) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(END_NEWLINE)) != null)
            {
               encoder.endGroup(state == State.regexp? TokenType.regexp:TokenType.string);
               encoder.textToken(m.group(), TokenType.error);
               after_def = value_expected = false;
               state = State.initial;
            }
            else
            {
               throw new RuntimeException("Else case reached; unhandled " + source.peek(1));
            }
            break;
         default:
            throw new RuntimeException("Unknown state " + state);
         }
         if (kind != TokenType.space && kind != TokenType.comment && kind != TokenType.doctype)
         {
            last_token = m == null ? null:m.group();
         }
      }
      if (state == State.multiline_string || state == State.string || state == State.regexp)
      {
         encoder.endGroup(state == State.regexp? TokenType.regexp:TokenType.string);
      }

      while (!inlineBlockStack.isEmpty())
      {
         state = (State)inlineBlockStack.pop()[0];
         encoder.endGroup(TokenType.inline);
         encoder.endGroup(state == State.regexp? TokenType.regexp:TokenType.string);
      }
   }

}
