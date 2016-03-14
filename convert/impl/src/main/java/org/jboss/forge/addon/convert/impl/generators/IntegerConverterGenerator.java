/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.convert.impl.generators;

import javax.inject.Singleton;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.furnace.util.Strings;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class IntegerConverterGenerator implements ConverterGenerator, Converter<String, Integer>
{

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return source == String.class && target == Integer.class;
   }

   @Override
   public Converter<?, ?> generateConverter(Class<?> source, Class<?> target)
   {
      return this;
   }

   @Override
   public Class<? extends Converter<?, ?>> getConverterType()
   {
      return IntegerConverterGenerator.class;
   }

   @Override
   public Integer convert(String source)
   {
      return (Strings.isNullOrEmpty(source)) ? null : Integer.valueOf(source);
   }

}
