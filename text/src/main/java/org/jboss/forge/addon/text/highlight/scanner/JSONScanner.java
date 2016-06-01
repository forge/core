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
 * Based on https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/json.rb
 * Last update sha: b89caf96d1cfc304c2114d8734ebe8b91337c799
 */
public class JSONScanner implements Scanner
{

   public static final Pattern ESCAPE = Pattern.compile("[bfnrt\\\\\"\\/]");
   public static final Pattern UNICODE_ESCAPE = Pattern.compile("u[a-fA-F0-9]{4}");
   public static final Pattern KEY = Pattern.compile("(?>(?:[^\\\\\"]+|\\\\.)*)\"\\s*:");
   public static final Pattern SPACE = Pattern.compile("\\s+");
   public static final Pattern DOUBLE_QUOTE = Pattern.compile("\"");
   public static final Pattern OPERATOR = Pattern.compile("[:,\\[{\\]}]");
   public static final Pattern BOOLEAN = Pattern.compile("true|false|null");
   public static final Pattern NUMBER = Pattern.compile("-?(?:0|[1-9]\\d*)");
   public static final Pattern FLOAT = Pattern.compile("\\.\\d+(?:[eE][-+]?\\d+)?|[eE][-+]?\\d+");
   public static final Pattern CONTENT = Pattern.compile("[^\\\\\"]+");
   public static final Pattern CONTENT_2 = Pattern.compile("\\\\.", Pattern.DOTALL);
   public static final Pattern CHAR = Pattern.compile("\\\\(?:" + ESCAPE.pattern() + "|" + UNICODE_ESCAPE.pattern()
            + ")", Pattern.DOTALL);
   public static final Pattern END = Pattern.compile("\\\\|$");

   public enum State
   {
      initial,
      key,
      string
   }

   public static final Type TYPE = new Type("JSON", "\\.(json|template)$");

   @Override
   public Type getType() {
      return TYPE;
   }

   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options)
   {
      State state = State.initial;

      while (source.hasMore())
      {
         MatchResult m = null;

         switch (state)
         {

         case initial:
            if ((m = source.scan(SPACE)) != null)
            {
               encoder.textToken(m.group(), TokenType.space);
            }
            else if ((m = source.scan(DOUBLE_QUOTE)) != null)
            {
               state = source.check(KEY) != null ? State.key : State.string;
               encoder.beginGroup(TokenType.valueOf(state.name()));
               encoder.textToken(m.group(), TokenType.delimiter);
            }
            else if ((m = source.scan(OPERATOR)) != null)
            {
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if ((m = source.scan(BOOLEAN)) != null)
            {
               encoder.textToken(m.group(), TokenType.value);
            }
            else if ((m = source.scan(NUMBER)) != null)
            {
               String match = m.group();
               if ((m = source.scan(FLOAT)) != null)
               {
                  match = match + m.group();
                  encoder.textToken(match, TokenType.float_);
               }
               else
               {
                  encoder.textToken(match, TokenType.integer);
               }
            }
            else
            {
               encoder.textToken(source.next(), TokenType.error);
            }
            break;
         case key:
         case string:

            if ((m = source.scan(CONTENT)) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(DOUBLE_QUOTE)) != null)
            {
               encoder.textToken(m.group(), TokenType.delimiter);
               encoder.endGroup(TokenType.valueOf(state.name()));
               state = State.initial;
            }
            else if ((m = source.scan(CHAR)) != null)
            {
               encoder.textToken(m.group(), TokenType.char_);
            }
            else if ((m = source.scan(CONTENT_2)) != null)
            {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if ((m = source.scan(END)) != null)
            {
               encoder.endGroup(TokenType.valueOf(state.name()));
               if (!m.group().isEmpty())
               {
                  encoder.textToken(m.group(), TokenType.error);
               }
               state = State.initial;
            }
            else
            {
               throw new RuntimeException("else case \" reached " + source.peek(1) + " not handled");
            }
            break;
         default:
            throw new RuntimeException("Unknown state " + state);
         }
      }
      if (state == State.key || state == State.string)
      {
         encoder.endGroup(TokenType.valueOf(state.name()));
      }
   }

}
