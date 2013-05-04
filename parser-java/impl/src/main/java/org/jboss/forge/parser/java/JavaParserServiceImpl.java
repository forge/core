/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jboss.forge.parser.JavaParser;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaParserServiceImpl implements JavaParserService
{
   /**
    * Open the given {@link File}, parsing its contents into a new {@link JavaClass} instance.
    */
   @Override
   public JavaSource<?> parse(final File file) throws FileNotFoundException
   {
      return JavaParser.parse(file);
   }

   /**
    * Read the given {@link InputStream} and parse the data into a new {@link JavaClass} instance.
    */
   @Override
   public JavaSource<?> parse(final InputStream data)
   {
      return JavaParser.parse(data);
   }

   /**
    * Parse the given character array into a new {@link JavaClass} instance.
    */
   @Override
   public JavaSource<?> parse(final char[] data)
   {
      return JavaParser.parse(data);
   }

   /**
    * Parse the given String data into a new {@link JavaClass} instance.
    */
   @Override
   public JavaSource<?> parse(final String data)
   {
      return JavaParser.parse(data);
   }

   /**
    * Create a new empty {@link JavaClass} instance.
    */
   @Override
   public <T extends JavaSource<?>> T create(final Class<T> type)
   {
      return JavaParser.create(type);
   }

   /**
    * Read the given {@link File} and parse its data into a new {@link JavaSource} instance of the given type.
    * 
    * @throws FileNotFoundException
    */
   @Override
   public <T extends JavaSource<?>> T parse(final Class<T> type, final File file) throws FileNotFoundException
   {
      return JavaParser.parse(type, file);
   }

   /**
    * Read the given {@link InputStream} and parse its data into a new {@link JavaSource} instance of the given type.
    */
   public <T extends JavaSource<?>> T parse(final Class<T> type, final InputStream data)
   {
      return JavaParser.parse(type, data);
   }

   /**
    * Read the given character array and parse its data into a new {@link JavaSource} instance of the given type.
    */
   @Override
   public <T extends JavaSource<?>> T parse(final Class<T> type, final char[] data)
   {
      return JavaParser.parse(type, data);
   }

   /**
    * Read the given string and parse its data into a new {@link JavaSource} instance of the given type.
    */
   @Override
   public <T extends JavaSource<?>> T parse(final Class<T> type, final String data)
   {
      return JavaParser.parse(type, data);
   }
}
