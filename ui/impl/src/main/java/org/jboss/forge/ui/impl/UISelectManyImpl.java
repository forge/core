/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.impl;

import org.jboss.forge.ui.UISelectMany;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class UISelectManyImpl<T> extends UIInputComponentBase<UISelectMany<T>, Iterable<T>> implements
         UISelectMany<T>
{
   private Iterable<T> choices;

   public UISelectManyImpl(String name, Class<T> type)
   {
      super(name, type);
   }

   @Override
   public Iterable<T> getValueChoices()
   {
      return choices;
   }

   @Override
   public UISelectMany<T> setValueChoices(Iterable<T> choices)
   {
      this.choices = choices;
      return this;
   }

}
