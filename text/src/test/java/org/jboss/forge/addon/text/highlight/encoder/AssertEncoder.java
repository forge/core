/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.encoder;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.forge.addon.text.highlight.Theme;
import org.jboss.forge.addon.text.highlight.TokenType;
import org.junit.Assert;

public class AssertEncoder extends DebugEncoder
{

   public AssertEncoder(OutputStream out, Theme theme, Map<String, Object> options)
   {
      super(out, theme, options);
      textTokens.clear();
   }

   @Override
   public void textToken(String text, TokenType type)
   {
      textTokens.add(new TokenPair(text, type));
      super.textToken(text, type);
   }

   private static List<TokenPair> textTokens = new ArrayList<TokenPair>();

   private static class TokenPair
   {
      private String text;
      private TokenType type;

      public TokenPair(String text, TokenType type)
      {
         this.text = text;
         this.type = type;
      }

      @Override
      public String toString()
      {
         return "[text=" + text + ", type=" + type + "]";
      }
   }

   public static void assertTextToken(TokenType type, String... texts)
   {
      for (String text : texts)
      {
         boolean found = false;
         List<TokenPair> textMatches = new ArrayList<TokenPair>();
         for (TokenPair pair : textTokens)
         {
            if (pair.text.equals(text))
            {
               textMatches.add(pair);
               if (pair.type == type)
               {
                  found = true;
                  break;
               }
            }
         }
         if (!found)
         {
            Assert.fail("Expected [" + text + "] of type [" + type + "]: Found matches: " + textMatches);
         }
      }
   }
}
