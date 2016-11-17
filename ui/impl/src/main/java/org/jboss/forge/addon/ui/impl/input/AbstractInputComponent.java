/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.input;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.AbstractFaceted;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.ValueChangeListener;
import org.jboss.forge.addon.ui.input.events.ValueChangeEvent;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Callables;
import org.jboss.forge.furnace.util.Sets;
import org.jboss.forge.furnace.util.Strings;

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
   private final Set<UIValidator> validators = Sets.getConcurrentSet();
   private final Set<ValueChangeListener> valueChangeListeners = Sets.getConcurrentSet();

   private String label;
   private Callable<String> description;
   private Callable<String> note;
   private Callable<Boolean> enabled = Callables.returning(Boolean.TRUE);
   private Callable<Boolean> required = Callables.returning(Boolean.FALSE);
   private Callable<String> requiredMessage;
   private Converter<String, VALUETYPE> valueConverter;

   private boolean deprecated;
   private String deprecatedMessage;

   public AbstractInputComponent(String name, char shortName, Class<VALUETYPE> type)
   {
      Assert.notNull(name, "Name is required");
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
      return Callables.call(description);
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
      this.description = Callables.returning(description);
      return (IMPLTYPE) this;
   }

   @Override
   public IMPLTYPE setDescription(Callable<String> description)
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
      return Callables.call(requiredMessage);
   }

   @Override
   public IMPLTYPE setRequiredMessage(String requiredMessage)
   {
      this.requiredMessage = Callables.returning(requiredMessage);
      return (IMPLTYPE) this;
   }

   @Override
   public IMPLTYPE setRequiredMessage(Callable<String> requiredMessage)
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
   public Set<UIValidator> getValidators()
   {
      return Collections.unmodifiableSet(validators);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      String msg = InputComponents.validateRequired(this);
      if (!Strings.isNullOrEmpty(msg))
      {
         // value is required and was not set
         context.addValidationError(this, msg);
      }
      if (hasValue() || hasDefaultValue())
      {
         // value is not required or was set, fire the registered validators
         for (UIValidator validator : validators)
         {
            validator.validate(context);
         }
      }
   }

   @Override
   public ListenerRegistration<ValueChangeListener> addValueChangeListener(final ValueChangeListener listener)
   {
      valueChangeListeners.add(listener);
      return new ListenerRegistration<ValueChangeListener>()
      {
         @Override
         public ValueChangeListener removeListener()
         {
            valueChangeListeners.remove(listener);
            return listener;
         }
      };
   }

   @Override
   public IMPLTYPE setNote(Callable<String> note)
   {
      this.note = note;
      return (IMPLTYPE) this;
   }

   @Override
   public IMPLTYPE setNote(String note)
   {
      this.note = Callables.returning(note);
      return (IMPLTYPE) this;
   }

   @Override
   public String getNote()
   {
      return Callables.call(note);
   }

   @Override
   public boolean isDeprecated()
   {
      return deprecated;
   }

   @Override
   public IMPLTYPE setDeprecated(boolean deprecated)
   {
      this.deprecated = deprecated;
      return (IMPLTYPE) this;
   }

   @Override
   public String getDeprecatedMessage()
   {
      return deprecatedMessage;
   }

   @Override
   public IMPLTYPE setDeprecatedMessage(String message)
   {
      this.deprecatedMessage = message;
      return (IMPLTYPE) this;
   }

   protected Set<ValueChangeListener> getValueChangeListeners()
   {
      return valueChangeListeners;
   }

   protected void fireValueChangeListeners(Object newValue)
   {
      ValueChangeEvent evt = new ValueChangeEvent(this, getValue(), newValue);
      for (ValueChangeListener listener : getValueChangeListeners())
      {
         listener.valueChanged(evt);
      }
   }
}