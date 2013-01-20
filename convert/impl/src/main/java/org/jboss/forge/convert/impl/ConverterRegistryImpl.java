/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.convert.impl;

import java.util.HashMap;
import java.util.Map;

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
      Map<ClassPairEntry, Converter<?, ?>> customConverters = new HashMap<ClassPairEntry, Converter<?, ?>>();

      for (ExportedInstance<Converter> converterInstance : registry.getExportedInstances(Converter.class))
      {
         Converter<?, ?> converter = converterInstance.get();
         customConverters.put(
                  new ClassPairEntry(converter.getSourceType(), converter.getTargetType()),
                  converter);
      }

      Converter<S, T> result;
      ClassPairEntry key = new ClassPairEntry(source, target);
      result = (Converter<S, T>) customConverters.get(key);
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

   // TODO: Do comparison based on the class hierarchy
   private static class ClassPairEntry
   {
      private final Class<?> sourceType;
      private final Class<?> targetType;

      public ClassPairEntry(Class<?> sourceType, Class<?> targetType)
      {
         super();
         this.sourceType = sourceType;
         this.targetType = targetType;
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
         result = prime * result + ((targetType == null) ? 0 : targetType.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         ClassPairEntry other = (ClassPairEntry) obj;
         if (sourceType == null)
         {
            if (other.sourceType != null)
               return false;
         }
         else if (!sourceType.equals(other.sourceType))
            return false;
         if (targetType == null)
         {
            if (other.targetType != null)
               return false;
         }
         else if (!targetType.equals(other.targetType))
            return false;
         return true;
      }
   }

}