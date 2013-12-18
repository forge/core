/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.input;

import java.util.concurrent.Callable;

import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.furnace.util.Callables;

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
      return "UISelectOneImpl [name=" + getName() + ", shortName='" + getShortName() + "', value=" + value
               + ", defaultValue=" + defaultValue + "]";
   }

}
