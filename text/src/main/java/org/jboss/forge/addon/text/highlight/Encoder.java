/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public interface Encoder
{

   public enum Type
   {
      TERMINAL, DEBUG
   }

   void textToken(String text, TokenType type);

   void beginGroup(TokenType type);

   void endGroup(TokenType type);

   void beginLine(TokenType type);

   void endLine(TokenType type);

   public static abstract class AbstractEncoder implements Encoder
   {
      public static final String NEW_LINE = System.getProperty("line.separator");

      protected OutputStream out;
      protected Theme theme;
      protected Map<String, Object> options;

      public AbstractEncoder(OutputStream out, Theme theme, Map<String, Object> options)
      {
         this.out = out;
         this.theme = theme;
         this.options = options;
      }

      protected Color color(TokenType type)
      {
         return this.theme.lookup(type);
      }

      protected void write(String str)
      {
         try
         {
            out.write(str.getBytes());
         }
         catch (IOException e)
         {
            throw new RuntimeException("Could not write to output", e);
         }
      }

      protected void write(byte[] bytes)
      {
         try
         {
            out.write(bytes);
         }
         catch (IOException e)
         {
            throw new RuntimeException("Could not write to output", e);
         }
      }
   }

   public static class Factory
   {
      private static Factory factory;

      private Map<String, Class<? extends Encoder>> registry;

      private Factory()
      {
         this.registry = new HashMap<String, Class<? extends Encoder>>();
      }

      private static Factory instance()
      {
         if (factory == null)
         {
            factory = new Factory();
         }
         return factory;
      }

      public static void registrer(String type, Class<? extends Encoder> encoder)
      {
         instance().registry.put(type, encoder);
      }

      public static Encoder create(String type, OutputStream out, Theme theme, Map<String, Object> options)
      {
         Class<? extends Encoder> encoder = instance().registry.get(type);
         if (encoder != null)
         {
            try
            {
               Constructor<? extends Encoder> constructor = encoder.getConstructor(OutputStream.class, Theme.class,
                        Map.class);
               return constructor.newInstance(out, theme, options);
            }
            catch (Exception e)
            {
               throw new RuntimeException("Could not create new instance of " + encoder);
            }
         }
         throw new RuntimeException("No encoder found for type " + type);
      }
   }
}
