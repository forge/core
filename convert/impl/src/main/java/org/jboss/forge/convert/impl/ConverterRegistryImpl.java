/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.convert.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.Exported;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterRegistry;
import org.jboss.forge.convert.exception.ConverterNotFoundException;

@Exported
@Singleton
public class ConverterRegistryImpl implements ConverterRegistry
{
   @Inject
   private AddonRegistry registry;

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <S, T> Converter<S, T> getConverter(Class<S> source, Class<T> target)
   {
      Converter<S, T> result = null;
      for (ExportedInstance<Converter> converterInstance : registry.getExportedInstances(Converter.class))
      {
         Converter<?, ?> converter = converterInstance.get();
         if (converter.handles(source, target))
         {
            result = (Converter<S, T>) converter;
            break;
         }
      }
      if (result == null && String.class.equals(target))
      {
         result = (Converter<S, T>) new ToStringConverter<S>((Class<S>) source.getClass());
      }
      if (result == null)
      {
         try
         {
            result = new MethodConverter<S, T>(source, target, null, target.getMethod("valueOf", source));
         }
         catch (NoSuchMethodException noValueOf)
         {
            try
            {
               result = new ConstructorConverter<S, T>(source, target, target.getConstructor(source));
            }
            catch (NoSuchMethodException noConstructor)
            {
               throw new ConverterNotFoundException(source, target);
            }
         }
      }
      return result;
   }
}