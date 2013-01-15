/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.converter.Converter;

/**
 * Produces UIInput objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class UIInputProducer
{
   @SuppressWarnings("unchecked")
   @Produces
   public <T> UIInput<T> produceInput(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Type type = injectionPoint.getAnnotated().getTypeClosure().iterator().next();
      if (type instanceof ParameterizedType)
      {
         Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
         Class<T> target = (Class<T>) typeArguments[0];
         return new UIInputImpl<T>(name, target);
      }
      else
         throw new IllegalStateException("Cannot inject a generic instance of type " + Converter.class.getName()
                  + "<?,?> without specifying concrete generic types at injection point " + injectionPoint + ".");

   }
}
