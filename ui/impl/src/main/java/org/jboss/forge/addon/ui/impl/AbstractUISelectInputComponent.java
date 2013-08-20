/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.SelectComponent;

/**
 * Implementation of a {@link SelectComponent} object
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <VALUETYPE>
 */
@Vetoed
@SuppressWarnings("unchecked")
public abstract class AbstractUISelectInputComponent<IMPLTYPE extends SelectComponent<IMPLTYPE, VALUETYPE>, VALUETYPE> extends AbstractInputComponent<IMPLTYPE, VALUETYPE>
         implements SelectComponent<IMPLTYPE, VALUETYPE>
{
   private Iterable<VALUETYPE> choices;
   private Converter<VALUETYPE, String> itemLabelConverter;

   public AbstractUISelectInputComponent(String name, char shortName, Class<VALUETYPE> type)
   {
      super(name, shortName, type);
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
