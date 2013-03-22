/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.impl;

import java.util.concurrent.Callable;

import org.jboss.forge.container.util.Callables;
import org.jboss.forge.ui.input.UISelectOne;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class UISelectOneImpl<VALUETYPE> extends UISelectInputComponentBase<UISelectOne<VALUETYPE>, VALUETYPE> implements
         UISelectOne<VALUETYPE>
{
   private VALUETYPE value;
   private Callable<VALUETYPE> defaultValue;

   public UISelectOneImpl(String name, Class<VALUETYPE> type)
   {
      super(name, type);
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

}
