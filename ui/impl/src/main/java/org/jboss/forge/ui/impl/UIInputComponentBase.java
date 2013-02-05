/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import java.util.concurrent.Callable;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.facets.BaseFaceted;
import org.jboss.forge.facets.Facet;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIInputComponent;
import org.jboss.forge.ui.facets.HintsFacet;
import org.jboss.forge.ui.util.Callables;

/**
 * Implementation of a {@link UIInput} object
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 * @param <VALUETYPE>
 */
@Vetoed
public abstract class UIInputComponentBase<IMPLTYPE extends UIInputComponent<IMPLTYPE, VALUETYPE>, VALUETYPE> extends BaseFaceted
         implements UIInputComponent<IMPLTYPE, VALUETYPE>
{
   private final String name;
   private final Class<VALUETYPE> type;

   private String label;
   private Callable<Boolean> enabled;
   private VALUETYPE value;
   private Callable<Boolean> required;
   private Callable<VALUETYPE> defaultValue;

   @SuppressWarnings("unchecked")
   public UIInputComponentBase(String name, Class<?> type)
   {
      this.name = name;
      this.type = (Class<VALUETYPE>) type;
   }

   @Override
   public String getLabel()
   {
      return label;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public VALUETYPE getValue()
   {
      return (value == null) ? Callables.call(defaultValue) : value;
   }

   @Override
   public Class<VALUETYPE> getValueType()
   {
      return type;
   }

   @Override
   public boolean isEnabled()
   {
      return Callables.call(enabled);
   }

   @Override
   public boolean isRequired()
   {
      return Callables.call(required);
   }

   @SuppressWarnings("unchecked")
   @Override
   public IMPLTYPE setDefaultValue(Callable<VALUETYPE> callback)
   {
      this.defaultValue = callback;
      return (IMPLTYPE) this;
   }

   @SuppressWarnings("unchecked")
   @Override
   public IMPLTYPE setDefaultValue(VALUETYPE value)
   {
      this.defaultValue = Callables.returning(value);
      return (IMPLTYPE) this;
   }

   @SuppressWarnings("unchecked")
   @Override
   public IMPLTYPE setEnabled(boolean enabled)
   {
      this.enabled = Callables.returning(enabled);
      return (IMPLTYPE) this;
   }

   @SuppressWarnings("unchecked")
   @Override
   public IMPLTYPE setEnabled(Callable<Boolean> callback)
   {
      enabled = callback;
      return (IMPLTYPE) this;
   }

   @SuppressWarnings("unchecked")
   @Override
   public IMPLTYPE setLabel(String label)
   {
      this.label = label;
      return (IMPLTYPE) this;
   }

   @SuppressWarnings("unchecked")
   @Override
   public IMPLTYPE setRequired(boolean required)
   {
      this.required = Callables.returning(required);
      return (IMPLTYPE) this;
   }

   @SuppressWarnings("unchecked")
   @Override
   public IMPLTYPE setRequired(Callable<Boolean> required)
   {
      this.required = required;
      return (IMPLTYPE) this;
   }

   @SuppressWarnings("unchecked")
   @Override
   public IMPLTYPE setValue(VALUETYPE value)
   {
      this.value = value;
      return (IMPLTYPE) this;
   }

   @Override
   public boolean supports(Class<? extends Facet<?>> type)
   {
      return HintsFacet.class.isAssignableFrom(type);
   }

}
