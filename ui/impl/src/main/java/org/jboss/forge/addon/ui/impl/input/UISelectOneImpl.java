/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.input;

import java.util.Set;
import java.util.concurrent.Callable;

import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.input.ValueChangeListener;
import org.jboss.forge.addon.ui.input.events.ValueChangeEvent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Callables;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.furnace.util.Strings;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class UISelectOneImpl<VALUETYPE> extends AbstractUISelectInputComponent<UISelectOne<VALUETYPE>, VALUETYPE>
         implements
         UISelectOne<VALUETYPE>
{
   private VALUETYPE value;
   private Callable<VALUETYPE> defaultValue;

   public UISelectOneImpl(String name, char shortName, Class<VALUETYPE> type)
   {
      super(name, shortName, type);
   }

   @Override
   public UISelectOne<VALUETYPE> setValue(VALUETYPE value)
   {
      // assertChoiceInValueChoices(value);
      Set<ValueChangeListener> listeners = getValueChangeListeners();
      if (!listeners.isEmpty() && !InputComponents.areEqual(getValue(), value))
      {
         fireValueChangeListeners(value);
      }
      this.value = value;
      return this;
   }

   @Override
   public UISelectOne<VALUETYPE> setDefaultValue(Callable<VALUETYPE> callback)
   {
      this.defaultValue = callback;
      return this;
   }

   @Override
   public UISelectOne<VALUETYPE> setDefaultValue(VALUETYPE value)
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

   @Override
   public int getSelectedIndex()
   {
      return getIndexFor(getValue());
   }

   private int getIndexFor(Object value)
   {
      return Lists.toList(getValueChoices()).indexOf(value);
   }

   @Override
   protected void fireValueChangeListeners(Object newValue)
   {
      int[] oldSelectedIndexes = new int[] { getSelectedIndex() };
      int[] newSelectedIndexes = new int[] { getIndexFor(newValue) };
      ValueChangeEvent evt = new ValueChangeEvent(this, getValue(), newValue, oldSelectedIndexes, newSelectedIndexes);
      for (ValueChangeListener listener : getValueChangeListeners())
      {
         listener.valueChanged(evt);
      }
   }

   @Override
   public String toString()
   {
      return "UISelectOneImpl [name=" + getName() + ", shortName='" + getShortName() + "', value=" + value
               + ", defaultValue=" + defaultValue + "]";
   }

}
