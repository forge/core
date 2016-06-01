/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jboss.forge.addon.text.highlight.scanner.PlainScanner;

public interface Scanner
{
   public Type getType();

   void scan(StringScanner source, Encoder encoder, Map<String, Object> options);

   public class Type
   {
      private String name;
      private Pattern pattern;

      public Type(String name, String pattern)
      {
         this(name, Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
      }
      public Type(String name, Pattern pattern)
      {
         this.name = name;
         this.pattern = pattern;
      }

      public String getName()
      {
         return name;
      }

      public boolean supports(String fileName) {
         if(pattern == null)
         {
            return false;
         }
         return pattern.matcher(fileName).find();
      }
   }

   public static class Factory
   {
      private static Factory factory;

      private Map<String, Scanner> registry;

      private Factory()
      {
         this.registry = new LinkedHashMap<String, Scanner>();
      }

      private static Factory instance()
      {
         if (factory == null)
         {
            factory = new Factory();
         }
         return factory;
      }

      public static void registrer(Class<? extends Scanner> scanner)
      {
         Scanner scannerInst = create(scanner);
         instance().registry.put(scannerInst.getType().getName(), scannerInst);
      }

      public static Scanner byType(String typeName)
      {
         for (Scanner scanner : instance().registry.values())
         {
            if (scanner.getType().getName().equalsIgnoreCase(typeName))
            {
               return scanner;
            }
         }
         return null;
      }

      public static Scanner byFileName(String fileName)
      {
         for (Scanner scanner : instance().registry.values())
         {
            if (scanner.getType().supports(fileName))
            {
               return scanner;
            }
         }
         return instance().registry.get(PlainScanner.TYPE);
      }

      private static Scanner create(Class<? extends Scanner> scanner)
      {
         if (scanner != null)
         {
            try
            {
               return scanner.newInstance();
            }
            catch (Exception e)
            {
               throw new RuntimeException("Could not create new instance of " + scanner);
            }
         }
         return null;
      }
   }
}
