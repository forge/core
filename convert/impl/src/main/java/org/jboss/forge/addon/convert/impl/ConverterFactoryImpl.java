/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.convert.exception.ConverterNotFoundException;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * Default implementation of the {@link ConverterFactory} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class ConverterFactoryImpl implements ConverterFactory
{
   @Inject
   private AddonRegistry registry;

   private Map<Class<?>, Class<?>> primitiveToWrapperMap = new HashMap<>();

   public ConverterFactoryImpl()
   {
      primitiveToWrapperMap.put(Boolean.TYPE, Boolean.class);
      primitiveToWrapperMap.put(Character.TYPE, Character.class);
      primitiveToWrapperMap.put(Byte.TYPE, Byte.class);
      primitiveToWrapperMap.put(Short.TYPE, Short.class);
      primitiveToWrapperMap.put(Integer.TYPE, Integer.class);
      primitiveToWrapperMap.put(Long.TYPE, Long.class);
      primitiveToWrapperMap.put(Float.TYPE, Float.class);
      primitiveToWrapperMap.put(Double.TYPE, Double.class);
      primitiveToWrapperMap.put(Void.TYPE, Void.class);
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <S, T> Converter<S, T> getConverter(Class<S> source, Class<T> target)
   {
      Converter<S, T> result = null;
      for (ConverterGenerator generator : registry.getServices(ConverterGenerator.class))
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
            result = (Converter<S, T>) new StringToImportedInstanceConverter<>(target, registry);
         }
         else if (String.class.equals(target))
         {
            result = (Converter<S, T>) Converters.TO_STRING;
         }
         else if (areTypesAssignable(source, target))
         {
            result = (Converter<S, T>) Converters.NOOP;
         }
         else
         {
            Class<?> targetType = target;
            if (target.isPrimitive())
            {
               targetType = primitiveToWrapperMap.get(target);
            }
            try
            {
               result = (Converter<S, T>) new MethodConverter<>(source, targetType, null, targetType.getMethod(
                        "valueOf", source));
            }
            catch (NoSuchMethodException noValueOf)
            {
               try
               {
                  result = new ConstructorConverter(source, targetType, targetType.getConstructor(source));
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

   /**
    * Check if the parameters are primitive and if they can be assignable
    */
   private boolean areTypesAssignable(Class<?> source, Class<?> target)
   {
      if (target.isAssignableFrom(source))
      {
         return true;
      }
      else if (!source.isPrimitive() && !target.isPrimitive())
      {
         return false;
      }
      else if (source.isPrimitive())
      {
         // source is primitive
         return primitiveToWrapperMap.get(source) == target;
      }
      else
      {
         // target is primitive
         return source == primitiveToWrapperMap.get(target);
      }
   }
}