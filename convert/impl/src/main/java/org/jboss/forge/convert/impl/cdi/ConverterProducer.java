/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.convert.impl.cdi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.convert.Converter;
import org.jboss.forge.convert.ConverterRegistry;

public class ConverterProducer
{
   @Produces
   @SuppressWarnings("unchecked")
   public <S, T> Converter<S, T> produceConverter(InjectionPoint injectionPoint, ConverterRegistry registry)
   {
      Type type = injectionPoint.getAnnotated().getTypeClosure().iterator().next();
      if (type instanceof ParameterizedType)
      {
         Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
         Class<S> source = (Class<S>) typeArguments[0];
         Class<T> target = (Class<T>) typeArguments[1];
         return registry.getConverter(source, target);
      }
      else
         throw new IllegalStateException("Cannot inject a generic instance of type " + Converter.class.getName()
                  + "<?,?> without specifying concrete generic types at injection point " + injectionPoint + ".");
   }
}
