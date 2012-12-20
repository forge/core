/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import java.util.concurrent.Callable;

import org.jboss.forge.ui.UIInput;

/**
 * Implementation of a {@link UIInput} object
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <T>
 */
public class UIInputImpl<T> implements UIInput<T>
{
   private final String name;
   private final Class<T> type;

   private T value;
   private Callable<Boolean> required;
   private Callable<T> defaultValue;

   public UIInputImpl(String name, Class<T> type)
   {
      this.name = name;
      this.type = type;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public Class<T> getType()
   {
      return type;
   }

   @Override
   public T getValue()
   {
      return (value == null) ? Callables.call(defaultValue) : value;
   }

   @Override
   public boolean isRequired()
   {
      return Callables.call(required);
   }

   @Override
   public UIInput<T> setDefaultValue(T value)
   {
      this.defaultValue = Callables.constant(value);
      return this;
   }

   @Override
   public UIInput<T> setDefaultValue(Callable<T> callback)
   {
      this.defaultValue = callback;
      return this;
   }

   @Override
   public UIInput<T> setRequired(boolean required)
   {
      this.required = Callables.constant(required);
      return this;
   }

   @Override
   public UIInput<T> setRequired(Callable<Boolean> required)
   {
      this.required = required;
      return this;
   }

   @Override
   public UIInput<T> setValue(T value)
   {
      this.value = value;
      return this;
   }

}
