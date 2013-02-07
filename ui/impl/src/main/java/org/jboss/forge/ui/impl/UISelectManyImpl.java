/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.impl;

import java.util.concurrent.Callable;

import org.jboss.forge.ui.UISelectMany;
import org.jboss.forge.ui.util.Callables;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class UISelectManyImpl<VALUETYPE> extends UIInputComponentBase<UISelectMany<VALUETYPE>, VALUETYPE> implements
         UISelectMany<VALUETYPE>
{
   private Iterable<VALUETYPE> choices;
   private Iterable<VALUETYPE> value;
   private Callable<Iterable<VALUETYPE>> defaultValue;

   public UISelectManyImpl(String name, Class<VALUETYPE> type)
   {
      super(name, type);
   }

   @Override
   public Iterable<VALUETYPE> getValueChoices()
   {
      return choices;
   }

   @Override
   public UISelectMany<VALUETYPE> setValueChoices(Iterable<VALUETYPE> choices)
   {
      this.choices = choices;
      return this;
   }

   @Override
   public UISelectMany<VALUETYPE> setValue(Iterable<VALUETYPE> value)
   {
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
}
