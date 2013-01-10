/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;

import java.util.concurrent.Callable;

import org.jboss.forge.container.services.Remote;
import org.jboss.forge.ui.util.Callables;

@Remote
public class UIInput<T>
{
   private final String name;
   private final Class<T> type;

   private String label;
   private Callable<Boolean> enabled;
   private T value;
   private Callable<Boolean> required;
   private Callable<T> defaultValue;

   public UIInput(String name, Class<T> type)
   {
      this.name = name;
      this.type = type;
   }

   public String getLabel()
   {
      return label;
   }

   public String getName()
   {
      return name;
   }

   public T getValue()
   {
      return (value == null) ? Callables.call(defaultValue) : value;
   }

   public Class<T> getValueType()
   {
      return type;
   }

   public boolean isEnabled()
   {
      return Callables.call(enabled);
   }

   public boolean isRequired()
   {
      return Callables.call(required);
   }

   public UIInput<T> setDefaultValue(Callable<T> callback)
   {
      this.defaultValue = callback;
      return this;
   }

   public UIInput<T> setDefaultValue(T value)
   {
      this.defaultValue = Callables.returning(value);
      return this;
   }

   public UIInput<T> setEnabled(boolean enabled)
   {
      this.enabled = Callables.returning(enabled);
      return this;
   }

   public UIInput<T> setEnabled(Callable<Boolean> callback)
   {
      enabled = callback;
      return this;
   }

   public UIInput<T> setLabel(String label)
   {
      this.label = label;
      return this;
   }

   public UIInput<T> setRequired(boolean required)
   {
      this.required = Callables.returning(required);
      return this;
   }

   public UIInput<T> setRequired(Callable<Boolean> required)
   {
      this.required = required;
      return this;
   }

   public UIInput<T> setValue(T value)
   {
      this.value = value;
      return this;
   }

}