/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.input;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.ValueChangeListener;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Callables;

/**
 * Implementation of a {@link UIInput} object
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 * @param <VALUETYPE>
 */
@Vetoed
public class UIInputManyImpl<VALUETYPE> extends AbstractInputComponent<UIInputMany<VALUETYPE>, VALUETYPE>
         implements UIInputMany<VALUETYPE>
{

   private Iterable<VALUETYPE> value;
   private Callable<Iterable<VALUETYPE>> defaultValue;
   private UICompleter<VALUETYPE> completer;
   private Converter<String, VALUETYPE> converter;

   public UIInputManyImpl(String name, char shortName, Class<VALUETYPE> type)
   {
      super(name, shortName, type);
   }

   @Override
   public UICompleter<VALUETYPE> getCompleter()
   {
      return this.completer;
   }

   @Override
   public UIInputMany<VALUETYPE> setCompleter(UICompleter<VALUETYPE> completer)
   {
      this.completer = completer;
      return this;
   }

   @Override
   public UIInputMany<VALUETYPE> setValue(Iterable<VALUETYPE> value)
   {
      Set<ValueChangeListener> listeners = getValueChangeListeners();
      if (!listeners.isEmpty() && !InputComponents.areElementsEqual(getValue(), value))
      {
         fireValueChangeListeners(value);
      }
      this.value = value;
      return this;
   }

   @Override
   public UIInputMany<VALUETYPE> setDefaultValue(Callable<Iterable<VALUETYPE>> callback)
   {
      this.defaultValue = callback;
      return this;
   }

   @Override
   public UIInputMany<VALUETYPE> setDefaultValue(Iterable<VALUETYPE> value)
   {
      this.defaultValue = Callables.returning(value);
      return this;
   }

   @Override
   public Iterable<VALUETYPE> getValue()
   {
      Iterable<VALUETYPE> iterableValue = (value == null) ? Callables.call(defaultValue) : value;
      return iterableValue == null ? Collections.<VALUETYPE> emptyList() : iterableValue;
   }

   @Override
   public Converter<String, VALUETYPE> getValueConverter()
   {
      return converter;
   }

   @Override
   public UIInputMany<VALUETYPE> setValueConverter(Converter<String, VALUETYPE> converter)
   {
      this.converter = converter;
      return this;
   }

   @Override
   public String toString()
   {
      return "UIInputManyImpl [name=" + getName() + ", shortName='" + getShortName() + "', value=" + value
               + ", defaultValue=" + defaultValue + "]";
   }

   @Override
   public boolean hasDefaultValue()
   {
      Iterable<VALUETYPE> defaultValueValue = Callables.call(defaultValue);
      return defaultValueValue != null && defaultValueValue.iterator().hasNext();
   }

   @Override
   public boolean hasValue()
   {
      return value != null && value.iterator().hasNext();
   }
}
