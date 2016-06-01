/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.encoder;

import java.io.OutputStream;
import java.util.Map;

import org.jboss.forge.addon.text.highlight.Encoder;
import org.jboss.forge.addon.text.highlight.Theme;
import org.jboss.forge.addon.text.highlight.TokenType;

public class DebugEncoder extends Encoder.AbstractEncoder implements Encoder
{

   public DebugEncoder(OutputStream out, Theme theme, Map<String, Object> options)
   {
      super(out, theme, options);
   }

   @Override
   public void textToken(String text, TokenType type)
   {
      if (type == TokenType.space)
      {
         write(text);
      }
      else
      {
         String output = text;
         if (output.indexOf("\\") != -1)
         {
            output = output.replaceAll("\\\\", "\\\\\\\\");
         }
         if (output.indexOf(")") != -1)
         {
            output = output.replaceAll("\\)", "\\\\)");
         }
         write(fixTokeName(type) + "(" + output + ")");
      }
   }

   @Override
   public void beginGroup(TokenType type)
   {
      write(fixTokeName(type) + "<");
   }

   @Override
   public void endGroup(TokenType type)
   {
      write(">");
   }

   @Override
   public void beginLine(TokenType type)
   {
      write(fixTokeName(type) + "[");
   }

   @Override
   public void endLine(TokenType type)
   {
      write("]");
   }

   public String fixTokeName(TokenType type)
   {
      String name = type.name();
      if (name.endsWith("_"))
      {
         name = name.substring(0, name.length() - 1);
      }
      return name;
   }
}
