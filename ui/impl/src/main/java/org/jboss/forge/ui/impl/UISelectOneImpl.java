/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.impl;

import org.jboss.forge.ui.UISelectOne;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class UISelectOneImpl<T> extends UIInputComponentBase<UISelectOne<T>, T> implements UISelectOne<T>
{
   private Iterable<T> choices;

   public UISelectOneImpl(String name, Class<T> type)
   {
      super(name, type);
   }

   @Override
   public Iterable<T> getValueChoices()
   {
      return choices;
   }

   @Override
   public UISelectOne<T> setValueChoices(Iterable<T> values)
   {
      return this;
   }

}
