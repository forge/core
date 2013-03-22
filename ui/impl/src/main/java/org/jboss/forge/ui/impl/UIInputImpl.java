/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import java.util.concurrent.Callable;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.container.util.Callables;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.ui.input.UICompleter;
import org.jboss.forge.ui.input.UIInput;

/**
 * Implementation of a {@link UIInput} object
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 * @param <VALUETYPE>
 */
@Vetoed
public class UIInputImpl<VALUETYPE> extends UIInputComponentBase<UIInput<VALUETYPE>, VALUETYPE> implements
         UIInput<VALUETYPE>
{
   private VALUETYPE value;
   private Callable<VALUETYPE> defaultValue;
   private UICompleter<VALUETYPE> completer;
   private Converter<String, VALUETYPE> converter;

   public UIInputImpl(String name, Class<VALUETYPE> type)
   {
      super(name, type);
   }

   @Override
   @SuppressWarnings("unchecked")
   public UICompleter<VALUETYPE> getCompleter()
   {
      return this.completer == null ? new NoopCompleter() : this.completer;
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
}
