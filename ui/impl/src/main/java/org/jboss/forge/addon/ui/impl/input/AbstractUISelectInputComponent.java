/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.input;

import java.util.Collections;
import java.util.concurrent.Callable;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.furnace.util.Callables;

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
   private Callable<Iterable<VALUETYPE>> choices;
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
      Iterable<VALUETYPE> valueChoices = Callables.call(choices);
      return valueChoices == null ? Collections.<VALUETYPE> emptyList() : valueChoices;
   }

   @Override
   public IMPLTYPE setValueChoices(Iterable<VALUETYPE> choices)
   {
      this.choices = Callables.returning(choices);
      return (IMPLTYPE) this;
   }

   @Override
   public IMPLTYPE setValueChoices(Callable<Iterable<VALUETYPE>> choices)
   {
      this.choices = choices;
      return (IMPLTYPE) this;
   }

   protected void assertChoicesInValueChoices(Iterable<VALUETYPE> choices)
   {
      if (choices != null)
      {
         for (VALUETYPE choice : choices)
         {
            assertChoiceInValueChoices(choice);
         }
      }
   }

   protected void assertChoiceInValueChoices(VALUETYPE choice)
   {
      if (choice != null)
      {
         for (VALUETYPE type : getValueChoices())
         {
            if (type.equals(choice))
            {
               return;
            }
         }
         throw new IllegalArgumentException(choice + " is not a valid value for " + getName());
      }
   }
}
