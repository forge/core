/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.input;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.ValueChangeListener;
import org.jboss.forge.addon.ui.input.events.ValueChangeEvent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Callables;
import org.jboss.forge.furnace.util.Lists;

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
      Iterable<VALUETYPE> iterableValue = (value == null) ? Callables.call(defaultValue) : value;
      return iterableValue == null ? Collections.<VALUETYPE> emptyList() : iterableValue;
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

   @Override
   public int[] getSelectedIndexes()
   {
      return getIndexesFor(getValue());
   }

   private int[] getIndexesFor(Iterable<VALUETYPE> value)
   {
      List<VALUETYPE> valueChoices = Lists.toList(getValueChoices());
      List<VALUETYPE> thisValue = Lists.toList(value);
      int[] indexes = new int[thisValue.size()];
      for (int i = 0; i < thisValue.size(); i++)
      {
         indexes[i] = valueChoices.indexOf(thisValue.get(i));
      }
      return indexes;

   }

   @Override
   @SuppressWarnings("unchecked")
   protected void fireValueChangeListeners(Object newValue)
   {
      int[] oldSelectedIndexes = getSelectedIndexes();
      int[] newSelectedIndexes = getIndexesFor((Iterable<VALUETYPE>) newValue);
      ValueChangeEvent evt = new ValueChangeEvent(this, getValue(), newValue, oldSelectedIndexes, newSelectedIndexes);
      for (ValueChangeListener listener : getValueChangeListeners())
      {
         listener.valueChanged(evt);
      }
   }

   @Override
   public String toString()
   {
      return "UISelectManyImpl [name=" + getName() + ", shortName='" + getShortName() + "', value=" + value
               + ", defaultValue=" + defaultValue + "]";
   }
}
