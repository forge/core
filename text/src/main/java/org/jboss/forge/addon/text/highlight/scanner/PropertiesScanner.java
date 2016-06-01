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

public class PropertiesScanner implements Scanner
{

   private static final Pattern COMMENT = Pattern.compile("^(#|!).*");
   private static final Pattern KEY = Pattern.compile("((\\w)|(\\\\\\s)|(\\.))+(?=\\s?+(=|:))");
   private static final Pattern OPERATOR = Pattern.compile("=|:");
   private static final Pattern SPACE = Pattern.compile("\\s+");
   private static final Pattern VALUE = Pattern.compile(".*");
   private static final Pattern BOOLEAN = Pattern.compile("true|false|null");
   private static final Pattern NUMBER = Pattern.compile("-?(?:0|[1-9]\\d*)");
   private static final Pattern FLOAT = Pattern.compile("\\.\\d+(?:[eE][-+]?\\d+)?|[eE][-+]?\\d+");
   private static final Pattern UNICODE_ESCAPE = Pattern.compile("u[a-fA-F0-9]{4}");

   public enum State
   {
      initial, value
   }

   public static final Type TYPE = new Type("PROPERTIES", "\\.(properties)$");

   @Override
   public Type getType()
   {
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
            if ((m = source.scan(COMMENT)) != null)
            {
               encoder.textToken(m.group(), TokenType.comment);
            }
            else if ((m = source.scan(SPACE)) != null)
            {
               encoder.textToken(m.group(), TokenType.space);
            }
            else if ((m = source.scan(KEY)) != null)
            {
               encoder.textToken(m.group(), TokenType.key);
            }
            else if ((m = source.scan(OPERATOR)) != null)
            {
               encoder.textToken(m.group(), TokenType.operator);
               state = State.value;
            }
            else
            {
               encoder.textToken(source.next(), TokenType.error);
            }
            break;
         case value:
            if ((m = source.scan(SPACE)) != null)
            {
               encoder.textToken(m.group(), TokenType.space);
            }
            else if ((m = source.scan(FLOAT)) != null)
            {
               encoder.textToken(m.group(), TokenType.float_);
               state = State.initial;
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
               state = State.initial;
            }
            else if ((m = source.scan(BOOLEAN)) != null)
            {
               encoder.textToken(m.group(), TokenType.value);
               state = State.initial;
            }
            else if ((m = source.scan(UNICODE_ESCAPE)) != null)
            {
               encoder.textToken(m.group(), TokenType.value);
               state = State.initial;
            }
            else if ((m = source.scan(VALUE)) != null)
            {
               encoder.textToken(m.group(), TokenType.value);
               if (!m.group().endsWith("\\"))
               {
                  state = State.initial;
               }
            }
            else
            {
               encoder.textToken(source.next(), TokenType.error);
            }
            break;
         default:
            throw new RuntimeException("Unknown state " + state);
         }
      }
   }
}
