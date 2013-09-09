/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.AbstractFaceted;
import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Callables;

/**
 * Implementation of a {@link UIInput} object
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 * @param <VALUETYPE>
 */
@Vetoed
@SuppressWarnings("unchecked")
public abstract class AbstractInputComponent<IMPLTYPE extends InputComponent<IMPLTYPE, VALUETYPE>, VALUETYPE> extends AbstractFaceted<HintsFacet>
         implements InputComponent<IMPLTYPE, VALUETYPE>
{
   private final String name;
   private final char shortName;
   private final Class<VALUETYPE> type;

   private String label;
   private String description;
   private Callable<Boolean> enabled = Callables.returning(Boolean.TRUE);
   private Callable<Boolean> required = Callables.returning(Boolean.FALSE);
   private String requiredMessage;

   private Converter<String, VALUETYPE> valueConverter;
   private final List<UIValidator> validators = new ArrayList<UIValidator>();

   public AbstractInputComponent(String name, char shortName, Class<VALUETYPE> type)
   {
      this.name = name;
      this.shortName = shortName;
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
   public char getShortName()
   {
      return shortName;
   }

   @Override
   public String getDescription()
   {
      return description;
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
   public IMPLTYPE setDescription(String description)
   {
      this.description = description;
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
   public boolean supports(HintsFacet type)
   {
      return true;
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

   @Override
   public Converter<String, VALUETYPE> getValueConverter()
   {
      return valueConverter;
   }

   @Override
   public IMPLTYPE setValueConverter(Converter<String, VALUETYPE> converter)
   {
      this.valueConverter = converter;
      return (IMPLTYPE) this;
   }

   @Override
   public IMPLTYPE addValidator(UIValidator validator)
   {
      this.validators.add(validator);
      return (IMPLTYPE) this;
   }

   @Override
   public List<UIValidator> getValidators()
   {
      return Collections.unmodifiableList(validators);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      String msg = InputComponents.validateRequired(this);
      if (msg != null && !msg.isEmpty())
      {
         context.addValidationError(this, msg);
      }
      for (UIValidator validator : validators)
      {
         validator.validate(context);
      }
   }
}
