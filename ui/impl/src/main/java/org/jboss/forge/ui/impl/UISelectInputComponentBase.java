/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.convert.Converter;
import org.jboss.forge.ui.input.UISelectComponent;

/**
 * Implementation of a {@link UISelectComponent} object
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <VALUETYPE>
 */
@Vetoed
@SuppressWarnings("unchecked")
public abstract class UISelectInputComponentBase<IMPLTYPE extends UISelectComponent<IMPLTYPE, VALUETYPE>, VALUETYPE> extends UIInputComponentBase<IMPLTYPE, VALUETYPE>
         implements UISelectComponent<IMPLTYPE, VALUETYPE>
{
   private Iterable<VALUETYPE> choices;
   private Converter<VALUETYPE, String> itemLabelConverter;

   public UISelectInputComponentBase(String name, Class<VALUETYPE> type)
   {
      super(name, type);
   }

   @Override
   public Converter<VALUETYPE, String> getItemLabelConverter()
   {
      return itemLabelConverter;
   }

   @Override
   public IMPLTYPE setItemLabelConverter(Converter<VALUETYPE, String> converter)
   {
      this.itemLabelConverter = converter;
      return (IMPLTYPE) this;
   }

   @Override
   public Iterable<VALUETYPE> getValueChoices()
   {
      return choices;
   }

   @Override
   public IMPLTYPE setValueChoices(Iterable<VALUETYPE> choices)
   {
      this.choices = choices;
      return (IMPLTYPE) this;
   }

}
