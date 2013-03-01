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
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.convert.ConverterGenerator;
import org.jboss.forge.convert.exception.ConverterNotFoundException;

@Exported
@Singleton
public class ConverterFactoryImpl implements ConverterFactory
{
   @Inject
   private AddonRegistry registry;

   @Override
   @SuppressWarnings({ "unchecked" })
   public <S, T> Converter<S, T> getConverter(Class<S> source, Class<T> target)
   {
      Converter<S, T> result = null;
      for (ExportedInstance<ConverterGenerator> generatorInstance : registry
               .getExportedInstances(ConverterGenerator.class))
      {
         ConverterGenerator generator = generatorInstance.get();
         if (generator.handles(source, target))
         {
            result = (Converter<S, T>) generator.generateConverter(source, target);
            break;
         }
      }
      if (result == null)
      {
         if (String.class.equals(target))
         {
            result = (Converter<S, T>) Converters.TO_STRING;
         }
         else if (target.isAssignableFrom(source))
         {
            result = (Converter<S, T>) Converters.NOOP;
         }
         else
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
      }
      return result;
   }
}