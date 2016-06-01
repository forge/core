/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Scanner;
import org.jboss.forge.addon.text.highlight.StringScanner;
import org.jboss.forge.addon.text.highlight.TokenType;

/*
 * Based on https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/css.rb
 * Last update sha: 70c9ba896e1bba5ac727fb6fdfc3ba94510e652d
 *
 */
public class CSSScanner implements Scanner
{

   private static final Pattern HEX = Pattern.compile("[0-9a-fA-F]");
   private static final Pattern UNICODE = Pattern.compile("\\\\" + HEX.pattern() + "{1,6}\\b");
   private static final Pattern ESCAPE = Pattern.compile(UNICODE.pattern() + "|\\\\[^\\n0-9a-fA-F]");
   private static final Pattern NM_CHAR = Pattern.compile("[-_a-zA-Z0-9]");
   private static final Pattern NM_START = Pattern.compile("[_a-zA-Z]");

   private static final Pattern STRING1 = Pattern.compile("\"(?:[^\\n\\\\\"]+|\\\\\\n|" + ESCAPE.pattern() + ")*\"?");
   private static final Pattern STRING2 = Pattern.compile("'(?:[^\\n\\\\']+|\\\\\\n|" + ESCAPE.pattern() + ")*'?");
   private static final Pattern STRING = Pattern.compile(STRING1.pattern() + "|" + STRING2.pattern());

   private static final Pattern HEX_COLOR = Pattern.compile("#(?:" + HEX.pattern() + "{6}|" + HEX.pattern() + "{3})");
   private static final Pattern NUM = Pattern.compile("-?(?:[0-9]*\\.[0-9]+|[0-9]+)n?");
   private static final Pattern NAME = Pattern.compile(NM_CHAR.pattern() + "+");
   private static final Pattern IDENT = Pattern.compile("-?" + NM_START.pattern() + NM_CHAR.pattern() + "*");
   private static final Pattern AT_KEYWORD = Pattern.compile("@" + IDENT.pattern());
   private static final Pattern PERCENTAGE = Pattern.compile(NUM.pattern() + "%");

   private static final List<String> REDLDIMENSIONS = Arrays.asList("em", "ex", "px");
   private static final List<String> ABSDIMENSIONS = Arrays.asList("in", "cm", "mm", "pt", "pc");
   private static final List<String> STUFF = Arrays.asList("s", "dpi", "dppx", "deg");

   @SuppressWarnings("unchecked")
   private static final Pattern UNIT = union(REDLDIMENSIONS, ABSDIMENSIONS, STUFF);

   @SuppressWarnings("unchecked")
   static Pattern union(List<String>... strings)
   {
      StringBuilder p = new StringBuilder();
      p.append("(");
      for (List<String> string : strings)
      {
         for (String str : string)
         {
            p.append(str).append("|");
         }
      }
      p.deleteCharAt(p.length() - 1);
      p.append(")");
      return Pattern.compile(p.toString());
   }

   private static final Pattern DIMENSION = Pattern.compile(NUM.pattern() + UNIT.pattern());
   private static final Pattern FUNCTION = Pattern.compile("(?:url|alpha|attr|counters?)\\((?:[^)\\n]|\\\\\\))*\\)?");
   private static final Pattern ID = Pattern.compile("(?!" + HEX_COLOR.pattern() + "\\b(?!-))#" + NAME.pattern());
   private static final Pattern Class = Pattern.compile("\\." + NAME.pattern());
   private static final Pattern PSEUDO_CLASS = Pattern.compile("::?" + IDENT.pattern());
   private static final Pattern ATTRIBUTE_SELECTOR = Pattern.compile("\\[[^\\]]*\\]?");

   private static final Pattern SPACE = Pattern.compile("\\s+");
   private static final Pattern COMMENT = Pattern.compile("\\/\\*(?:.*?\\*\\/|\\z)", Pattern.DOTALL);
   private static final Pattern BRACKET_OPEN = Pattern.compile("\\{");
   private static final Pattern BRACKET_CLOSE = Pattern.compile("\\}");
   private static final Pattern FUNCTION_NAME = Pattern.compile("^\\w+\\(");
   private static final Pattern FLOAT = Pattern.compile("(?:" + DIMENSION.pattern() + "|" + PERCENTAGE.pattern() + "|"
            + NUM.pattern() + ")");
   private static final Pattern IMPORTANT = Pattern.compile("! *important");
   private static final Pattern COLOR = Pattern.compile("(?:rgb|hsl)a?\\([^()\\n]*\\)?");
   private static final Pattern OPERATOR = Pattern.compile("[+>~:;,.=()\\/]");
   private static final Pattern TAG = Pattern.compile("(?>" + IDENT.pattern() + ")(?!\\()|\\*");
   private static final Pattern MEDIA = Pattern.compile("@media");
   private static final Pattern KEY_VALUE = Pattern.compile("(?>" + IDENT.pattern() + ")(?!\\()");
   private static final Pattern PARENTHESES_END = Pattern.compile(".?\\)");
   private static final Pattern SQUARE_END = Pattern.compile(".?\\]");

   public enum State
   {
      initial,
      media,
      media_before_name,
      media_after_name,
      block
   }

   public static final String OPTION_START_STATE = "state";

   public static final Type TYPE = new Type("CSS", "\\.(css)$");

   @Override
   public Type getType() {
      return TYPE;
   }

   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options)
   {
      boolean value_expected = false;
      Stack<State> state = new Stack<State>();

      State initialState = State.initial;
      if (options.containsKey(OPTION_START_STATE))
      {
         initialState = State.valueOf(String.valueOf(options.get(OPTION_START_STATE)));
      }
      state.push(initialState);

      while (source.hasMore())
      {
         MatchResult m = null;

         if ((m = source.scan(SPACE)) != null)
         {
            encoder.textToken(m.group(), TokenType.space);
         }
         else if (media_blocks(source, encoder, value_expected, state))
         {

         }
         else if ((m = source.scan(COMMENT)) != null)
         {
            encoder.textToken(m.group(), TokenType.comment);
         }
         else if ((m = source.scan(BRACKET_OPEN)) != null)
         {
            value_expected = false;
            encoder.textToken(m.group(), TokenType.operator);
            state.push(State.block);
         }
         else if ((m = source.scan(BRACKET_CLOSE)) != null)
         {
            value_expected = false;
            encoder.textToken(m.group(), TokenType.operator);
            if (state.peek() == State.block || state.peek() == State.media)
            {
               state.pop();
            }
         }
         else if ((m = source.scan(STRING)) != null)
         {
            encoder.beginGroup(TokenType.string);
            encoder.textToken(m.group().substring(0, 1), TokenType.delimiter);
            if (m.group().length() > 2)
            {
               encoder.textToken(m.group().substring(1, m.group().length() - 1), TokenType.content);
            }
            if (m.group().length() >= 2)
            {
               encoder.textToken(m.group().substring(m.group().length() - 1), TokenType.delimiter);
            }
            encoder.endGroup(TokenType.string);
         }
         else if ((m = source.scan(FUNCTION)) != null)
         {
            encoder.beginGroup(TokenType.function);
            Matcher functionMatcher = FUNCTION_NAME.matcher(m.group());
            functionMatcher.lookingAt();
            String start = functionMatcher.group();
            encoder.textToken(start, TokenType.delimiter);
            if (PARENTHESES_END.matcher(m.group().substring(m.group().length() - 1)).matches())
            {
               if (m.group().length() > start.length() + 1)
               {
                  encoder.textToken(m.group().substring(start.length(), m.group().length() - 1), TokenType.content);
                  encoder.textToken(")", TokenType.delimiter);
               }
            }
            else if (m.group().length() > start.length())
            {
               encoder.textToken(m.group().substring(start.length(), m.group().length() - 1), TokenType.content);
            }
            encoder.endGroup(TokenType.function);
         }
         else if ((m = source.scan(FLOAT)) != null)
         {
            encoder.textToken(m.group(), TokenType.float_);
         }
         else if ((m = source.scan(HEX_COLOR)) != null)
         {
            encoder.textToken(m.group(), TokenType.color);
         }
         else if ((m = source.scan(IMPORTANT)) != null)
         {
            encoder.textToken(m.group(), TokenType.important);
         }
         else if ((m = source.scan(COLOR)) != null)
         {
            encoder.textToken(m.group(), TokenType.color);
         }
         else if ((m = source.scan(AT_KEYWORD)) != null)
         {
            encoder.textToken(m.group(), TokenType.directive);
         }
         else if ((m = source.scan(OPERATOR)) != null)
         {
            if (":".equals(m.group()))
            {
               value_expected = true;
            }
            else if (";".equals(m.group()))
            {
               value_expected = false;
            }
            encoder.textToken(m.group(), TokenType.operator);
         }
         else
         {
            encoder.textToken(source.next(), TokenType.error);
         }
      }
   }

   private boolean media_blocks(StringScanner source, Encoder encoder, boolean value_expected, Stack<State> state)
   {
      MatchResult m;
      switch (state.peek())
      {

      case initial:
      case media:
         if ((m = source.scan(TAG)) != null)
         {
            encoder.textToken(m.group(), TokenType.tag);
            return true;
         }
         else if ((m = source.scan(Class)) != null)
         {
            encoder.textToken(m.group(), TokenType.class_);
            return true;
         }
         else if ((m = source.scan(ID)) != null)
         {
            encoder.textToken(m.group(), TokenType.id);
            return true;
         }
         else if ((m = source.scan(PSEUDO_CLASS)) != null)
         {
            encoder.textToken(m.group(), TokenType.pseudo_class);
            return true;
         }
         else if ((m = source.scan(ATTRIBUTE_SELECTOR)) != null)
         {
            encoder.textToken(m.group().substring(0, 1), TokenType.operator);
            if (m.group().length() > 2)
            {
               encoder.textToken(m.group().substring(1, m.group().length() - 1), TokenType.attribute_name);
            }
            if (SQUARE_END.matcher(m.group().substring(m.group().length() - 1)).matches())
            {
               encoder.textToken(m.group().substring(m.group().length() - 1), TokenType.operator);
            }
            return true;
         }
         else if ((m = source.scan(MEDIA)) != null)
         {
            encoder.textToken(m.group(), TokenType.directive);
            state.push(State.media_before_name);
            return true;
         }
         break;

      case block:
         if ((m = source.scan(KEY_VALUE)) != null)
         {
            if (value_expected)
            {
               encoder.textToken(m.group(), TokenType.value);
            }
            else
            {
               encoder.textToken(m.group(), TokenType.key);
            }
            return true;
         }
         break;

      case media_before_name:
         if ((m = source.scan(IDENT)) != null)
         {
            encoder.textToken(m.group(), TokenType.type);
            state.pop();
            state.push(State.media_after_name);
            return true;
         }
         break;

      case media_after_name:
         if ((m = source.scan(BRACKET_OPEN)) != null)
         {
            encoder.textToken(m.group(), TokenType.operator);
            state.pop();
            state.push(State.media);
            return true;
         }
         break;

      default:
         throw new RuntimeException("Unknown state " + state);
      }
      return false;
   }
}
