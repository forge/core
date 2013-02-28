/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.impl;

import java.util.concurrent.Callable;

import org.jboss.forge.convert.Converter;
import org.jboss.forge.ui.input.UISelectOne;
import org.jboss.forge.ui.util.Callables;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class UISelectOneImpl<VALUETYPE> extends UIInputComponentBase<UISelectOne<VALUETYPE>, VALUETYPE> implements
         UISelectOne<VALUETYPE>
{
   private Iterable<VALUETYPE> choices;
   private VALUETYPE value;
   private Callable<VALUETYPE> defaultValue;
   private Converter<String, VALUETYPE> valueConverter;
   private Converter<VALUETYPE, String> itemLabelConverter;

   public UISelectOneImpl(String name, Class<VALUETYPE> type)
   {
      super(name, type);
   }

   @Override
   public Iterable<VALUETYPE> getValueChoices()
   {
      return choices;
   }

   @Override
   public UISelectOne<VALUETYPE> setValueChoices(Iterable<VALUETYPE> values)
   {
      return this;
   }

   @Override
   public UISelectOne<VALUETYPE> setValue(VALUETYPE value)
   {
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
   public Converter<String, VALUETYPE> getValueConverter()
   {
      return valueConverter;
   }

   @Override
   public UISelectOne<VALUETYPE> setValueConverter(Converter<String, VALUETYPE> converter)
   {
      this.valueConverter = converter;
      return this;
   }

   @Override
   public Converter<VALUETYPE, String> getItemLabelConverter()
   {
      return itemLabelConverter;
   }

   @Override
   public UISelectOne<VALUETYPE> setItemLabelConverter(Converter<VALUETYPE, String> converter)
   {
      this.itemLabelConverter = converter;
      return this;
   }

}
