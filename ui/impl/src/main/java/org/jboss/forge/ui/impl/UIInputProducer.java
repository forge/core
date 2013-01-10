/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import java.lang.reflect.ParameterizedType;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.ui.UIInput;

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
      ParameterizedType ptype = (ParameterizedType) injectionPoint.getType();
      Class<T> c = (Class<T>) ptype.getActualTypeArguments()[0];
      return new UIInput<T>(name, c);
   }
}
