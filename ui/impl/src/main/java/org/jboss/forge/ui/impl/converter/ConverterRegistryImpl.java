/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl.converter;

import java.util.HashMap;
import java.util.Map;

import org.jboss.forge.ui.converter.Converter;
import org.jboss.forge.ui.converter.ConverterNotFoundException;
import org.jboss.forge.ui.converter.ConverterRegistry;

public enum ConverterRegistryImpl implements ConverterRegistry
{
   INSTANCE;

   private Map<ClassPairEntry, Converter<?, ?>> customConverters = new HashMap<ClassPairEntry, Converter<?, ?>>();

   @Override
   public <S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<S, T> converter)
   {
      ClassPairEntry key = new ClassPairEntry(sourceType.getName(), targetType.getName());
      customConverters.put(key, converter);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <S, T> Converter<S, T> getConverter(Class<S> source, Class<T> target)
   {
      Converter<S, T> result;
      ClassPairEntry key = new ClassPairEntry(source.getName(), target.getName());
      result = (Converter<S, T>) customConverters.get(key);
      if (result == null)
      {
         // No Custom Converter found. Try valueOf
         try
         {
            result = new MethodConverter<S, T>(null, target.getMethod("valueOf", source));
         }
         catch (NoSuchMethodException noValueOf)
         {
            // No valueOf found. Try Constructor
            try
            {
               result = new ConstructorConverter<S, T>(target.getConstructor(source));
            }
            catch (NoSuchMethodException noConstructor)
            {
               // No Constructor found. Fail.
               throw new ConverterNotFoundException(source, target);
            }
         }
      }
      return result;
   }

   @Override
   public void removeConverter(Class<?> sourceType, Class<?> targetType)
   {
      ClassPairEntry key = new ClassPairEntry(sourceType.getName(), targetType.getName());
      customConverters.remove(key);
   }

   private static class ClassPairEntry
   {
      private final String sourceClassName;
      private final String targetClassName;

      public ClassPairEntry(String source, String target)
      {
         super();
         this.sourceClassName = source;
         this.targetClassName = target;
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((sourceClassName == null) ? 0 : sourceClassName.hashCode());
         result = prime * result + ((targetClassName == null) ? 0 : targetClassName.hashCode());
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
         if (sourceClassName == null)
         {
            if (other.sourceClassName != null)
               return false;
         }
         else if (!sourceClassName.equals(other.sourceClassName))
            return false;
         if (targetClassName == null)
         {
            if (other.targetClassName != null)
               return false;
         }
         else if (!targetClassName.equals(other.targetClassName))
            return false;
         return true;
      }
   }

}