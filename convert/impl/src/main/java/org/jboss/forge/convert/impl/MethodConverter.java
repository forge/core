/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.convert.impl;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.convert.Converter;

@Vetoed
public class MethodConverter<S, T> implements Converter<S, T>
{
   private final Object targetObject;
   private final Method method;

   /**
    * Creates a converter based in a method
    *
    * @param obj the target object. May be null if the method is static
    * @param method
    */
   public MethodConverter(Object obj, Method method)
   {
      super();
      this.targetObject = obj;
      this.method = method;
   }

   @Override
   @SuppressWarnings("unchecked")
   public T convert(S source) throws Exception
   {
      return (T) method.invoke(targetObject, source);
   }
}
