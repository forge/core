/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl.converter;

import org.jboss.forge.ui.converter.Converter;
import org.jboss.forge.ui.converter.ConverterRegistry;

public class ConverterRegistryService implements ConverterRegistry
{

   @Override
   public <S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<S, T> converter)
   {
      ConverterRegistryImpl.INSTANCE.addConverter(sourceType, targetType, converter);
   }

   @Override
   public void removeConverter(Class<?> sourceType, Class<?> targetType)
   {
      ConverterRegistryImpl.INSTANCE.removeConverter(sourceType, targetType);
   }

   @Override
   public <S, T> Converter<S, T> getConverter(Class<S> source, Class<T> target)
   {
      return ConverterRegistryImpl.INSTANCE.getConverter(source, target);
   }
}
