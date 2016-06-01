/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.convert.impl;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.AbstractConverter;
import org.jboss.forge.addon.convert.exception.ConversionException;

@Vetoed
public class MethodConverter<S, T> extends AbstractConverter<S, T>
{
   private final Object instance;
   private final Method method;

   /**
    * Creates a converter based in a method
    * 
    * @param instance the target object. May be null if the method is static
    * @param method
    * @param sourceType
    * @param targetType
    */
   public MethodConverter(Class<S> sourceType, Class<T> targetType, Object instance, Method method)
   {
      super(sourceType, targetType);
      this.instance = instance;
      this.method = method;
   }

   @Override
   @SuppressWarnings("unchecked")
   public T convert(S source)
   {
      try
      {
         return (T) method.invoke(instance, source);
      }
      catch (Exception e)
      {
         throw new ConversionException("Could not convert [" + source + "] to type [" + getTargetType() + "]", e);
      }
   }
}
