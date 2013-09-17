/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.convert.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.convert.exception.ConverterNotFoundException;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;

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
      Imported<ConverterGenerator> instances = registry.getServices(ConverterGenerator.class);
      for (ConverterGenerator generator : instances)
      {
         if (generator.handles(source, target))
         {
            result = (Converter<S, T>) generator.generateConverter(source, target);
            break;
         }
      }

      if (result == null)
      {
         if (String.class.equals(source) && !registry.getServices(target).isUnsatisfied())
         {
            result = (Converter<S, T>) new StringToImportedInstanceConverter<T>(target, registry);
         }
         else if (String.class.equals(target))
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