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
import org.jboss.forge.ui.facets.HintsFacet;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.input.UIInputComponent;
import org.jboss.forge.ui.util.Callables;

/**
 * Implementation of a {@link UIInput} object
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <VALUETYPE>
 */
@Vetoed
@SuppressWarnings("unchecked")
public abstract class UIInputComponentBase<IMPLTYPE extends UIInputComponent<IMPLTYPE, VALUETYPE>, VALUETYPE> extends BaseFaceted
         implements UIInputComponent<IMPLTYPE, VALUETYPE>
{
   private final String name;

   private String label;
   private Callable<Boolean> enabled;
   private Callable<Boolean> required;
   private String requiredMessage;

   private Class<VALUETYPE> type;

   public UIInputComponentBase(String name, Class<VALUETYPE> type)
   {
      this.name = name;
      this.type = type;
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

   @Override
   public IMPLTYPE setEnabled(boolean enabled)
   {
      this.enabled = Callables.returning(enabled);
      return (IMPLTYPE) this;
   }

   @Override
   public IMPLTYPE setEnabled(Callable<Boolean> callback)
   {
      enabled = callback;
      return (IMPLTYPE) this;
   }

   @Override
   public IMPLTYPE setLabel(String label)
   {
      this.label = label;
      return (IMPLTYPE) this;
   }

   @Override
   public IMPLTYPE setRequired(boolean required)
   {
      this.required = Callables.returning(required);
      return (IMPLTYPE) this;
   }

   @Override
   public IMPLTYPE setRequired(Callable<Boolean> required)
   {
      this.required = required;
      return (IMPLTYPE) this;
   }

   @Override
   public boolean supports(Class<? extends Facet<?>> type)
   {
      return HintsFacet.class.isAssignableFrom(type);
   }

   @Override
   public String getRequiredMessage()
   {
      return requiredMessage;
   }

   @Override
   public IMPLTYPE setRequiredMessage(String requiredMessage)
   {
      this.requiredMessage = requiredMessage;
      return (IMPLTYPE) this;
   }

}
