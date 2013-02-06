/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.ui.UICompleter;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIInputMany;

/**
 * Implementation of a {@link UIInput} object
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 * @param <VALUETYPE>
 */
@Vetoed
public class UIInputManyImpl<VALUETYPE> extends UIInputComponentBase<UIInputMany<VALUETYPE>, Iterable<VALUETYPE>>
         implements
         UIInputMany<VALUETYPE>
{
   public UIInputManyImpl(String name, Class<?> type)
   {
      super(name, type);
   }

   private UICompleter<VALUETYPE> completer;

   @Override
   @SuppressWarnings("unchecked")
   public UICompleter<VALUETYPE> getCompleter()
   {
      return this.completer == null ? new NoopCompleter() : this.completer;
   }

   @Override
   public UIInputMany<VALUETYPE> setCompleter(UICompleter<VALUETYPE> completer)
   {
      this.completer = completer;
      return this;
   }
}
