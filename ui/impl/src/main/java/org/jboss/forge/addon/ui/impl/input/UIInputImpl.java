/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.input;

import java.util.Set;
import java.util.concurrent.Callable;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.ValueChangeListener;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Callables;
import org.jboss.forge.furnace.util.Strings;

/**
 * Implementation of a {@link UIInput} object
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 * @param <VALUETYPE>
 */
@Vetoed
public class UIInputImpl<VALUETYPE> extends AbstractInputComponent<UIInput<VALUETYPE>, VALUETYPE> implements
         UIInput<VALUETYPE>
{
   private VALUETYPE value;
   private Callable<VALUETYPE> defaultValue;
   private UICompleter<VALUETYPE> completer;
   private Converter<String, VALUETYPE> converter;

   public UIInputImpl(String name, char shortName, Class<VALUETYPE> type)
   {
      super(name, shortName, type);
   }

   @Override
   public UICompleter<VALUETYPE> getCompleter()
   {
      return this.completer;
   }

   @Override
   public UIInput<VALUETYPE> setCompleter(UICompleter<VALUETYPE> completer)
   {
      this.completer = completer;
      return this;
   }

   @Override
   public UIInput<VALUETYPE> setValue(VALUETYPE value)
   {
      Set<ValueChangeListener> listeners = getValueChangeListeners();
      if (!listeners.isEmpty() && !InputComponents.areEqual(getValue(), value))
      {
         fireValueChangeListeners(value);
      }
      this.value = value;
      return this;
   }

   @Override
   public UIInput<VALUETYPE> setDefaultValue(Callable<VALUETYPE> callback)
   {
      this.defaultValue = callback;
      return this;
   }

   @Override
   public UIInput<VALUETYPE> setDefaultValue(VALUETYPE value)
   {
      this.defaultValue = Callables.returning(value);
      return this;
   }

   @Override
   public VALUETYPE getValue()
   {
      return (value == null) ? Callables.call(defaultValue) : value;
   }

   @Override
   public Converter<String, VALUETYPE> getValueConverter()
   {
      return converter;
   }

   @Override
   public UIInput<VALUETYPE> setValueConverter(Converter<String, VALUETYPE> converter)
   {
      this.converter = converter;
      return this;
   }

   @Override
   public String toString()
   {
      return "UIInputImpl [name=" + getName() + ", shortName='" + getShortName() + "', value=" + value
               + ", defaultValue=" + defaultValue
               + "]";
   }

   @Override
   public boolean hasDefaultValue()
   {
      VALUETYPE defaultValueValue = Callables.call(defaultValue);
      if (defaultValueValue instanceof String)
      {
         return !Strings.isNullOrEmpty((String) defaultValueValue);
      }
      return defaultValueValue != null;
   }

   @Override
   public boolean hasValue()
   {
      if (value instanceof String)
      {
         return !Strings.isNullOrEmpty((String) value);
      }
      return value != null;
   }
}
