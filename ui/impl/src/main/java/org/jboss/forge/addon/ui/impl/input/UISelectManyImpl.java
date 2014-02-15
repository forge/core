/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.input;

import java.util.Set;
import java.util.concurrent.Callable;

import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.ValueChangeListener;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Callables;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class UISelectManyImpl<VALUETYPE> extends AbstractUISelectInputComponent<UISelectMany<VALUETYPE>, VALUETYPE>
         implements
         UISelectMany<VALUETYPE>
{
   private Iterable<VALUETYPE> value;
   private Callable<Iterable<VALUETYPE>> defaultValue;

   public UISelectManyImpl(String name, char shortName, Class<VALUETYPE> type)
   {
      super(name, shortName, type);
   }

   @Override
   public UISelectMany<VALUETYPE> setValue(Iterable<VALUETYPE> value)
   {
      // assertChoicesInValueChoices(value);
      Set<ValueChangeListener> listeners = getValueChangeListeners();
      if (!listeners.isEmpty() && !InputComponents.areElementsEqual(getValue(), value))
      {
         fireValueChangeListeners(value);
      }
      this.value = value;
      return this;
   }

   @Override
   public UISelectMany<VALUETYPE> setDefaultValue(Callable<Iterable<VALUETYPE>> callback)
   {
      this.defaultValue = callback;
      return this;
   }

   @Override
   public UISelectMany<VALUETYPE> setDefaultValue(Iterable<VALUETYPE> value)
   {
      this.defaultValue = Callables.returning(value);
      return this;
   }

   @Override
   public Iterable<VALUETYPE> getValue()
   {
      return (value == null) ? Callables.call(defaultValue) : value;
   }

   @Override
   public boolean hasDefaultValue()
   {
      return defaultValue != null;
   }

   @Override
   public boolean hasValue()
   {
      return value != null;
   }

   @Override
   public String toString()
   {
      return "UISelectManyImpl [name=" + getName() + ", shortName='" + getShortName() + "', value=" + value
               + ", defaultValue=" + defaultValue + "]";
   }
}
