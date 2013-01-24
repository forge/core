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
import javax.inject.Inject;

import org.jboss.forge.environment.Environment;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.hints.HintsLookup;
import org.jboss.forge.ui.hints.InputType;

/**
 * Produces UIInput objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class UIInputProducer
{
   @Inject
   private Environment environment;

   @Produces
   @SuppressWarnings("unchecked")
   public <T> UIInput<T> produceInput(InjectionPoint injectionPoint)
   {
      String name = injectionPoint.getMember().getName();
      Type type = injectionPoint.getAnnotated().getTypeClosure().iterator().next();
      if (type instanceof ParameterizedType)
      {
         Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
         Class<T> valueType = (Class<T>) typeArguments[0];
         UIInputImpl<T> input = new UIInputImpl<T>(name, valueType);

         HintsLookup hints = new HintsLookup(environment);
         input.getMetadata().set(InputType.class, hints.getInputType(valueType));

         return input;
      }
      else
      {
         throw new IllegalStateException("Cannot inject a generic instance of type " + UIInput.class.getName()
                  + "<?,?> without specifying concrete generic types at injection point " + injectionPoint + ".");
      }

   }
}
