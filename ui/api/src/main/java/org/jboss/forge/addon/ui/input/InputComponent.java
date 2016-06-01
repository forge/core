/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import java.util.Set;
import java.util.concurrent.Callable;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.MutableFaceted;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * This is the parent interface of all inputs.
 * 
 * The following facets are supported and automatically installed: {@link HintsFacet}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface InputComponent<IMPLTYPE, VALUETYPE> extends MutableFaceted<HintsFacet>
{
   /**
    * Return the label for this {@link InputComponent}. This is typically used when displaying inputs in more graphical
    * layouts, or when a short description of the input is required.
    */
   String getLabel();

   /**
    * Return the name of this {@link InputComponent}. This is typically used for text-based interactions, and may be
    * canonicalized by the {@link UIProvider} implementation.
    */
   String getName();

   /**
    * Return a few sentences describing the purpose of this {@link InputComponent}.
    */
   String getDescription();

   /**
    * Return the {@link Class} type of this {@link InputComponent}.
    */
   Class<VALUETYPE> getValueType();

   /**
    * Return <code>true</code> if this {@link InputComponent} has been supplied with a default value.
    */
   boolean hasDefaultValue();

   /**
    * Return <code>true</code> if the primary value of this {@link InputComponent} has been set. (If <code>true</code>
    * any default value will be ignored.) If a default value has been provided, this method will still return
    * <code>false</code> if no primary value has been set.
    */
   boolean hasValue();

   /**
    * Return <code>true</code> if this {@link InputComponent} is enabled, and should be displayed in the user interface.
    */
   boolean isEnabled();

   /**
    * Return <code>true</code> if a value or default value of this {@link InputComponent} must be set before the
    * {@link UICommand} to which it belongs may be executed.
    */
   boolean isRequired();

   /**
    * Return the message to be displayed when this {@link InputComponent} is required and no primary or default value
    * has been set.
    */
   String getRequiredMessage();

   /**
    * Return the single character abbreviation representing this {@link InputComponent}. This is typically used for
    * hot-keys or text-based shorthand flags in non-graphical user interfaces.
    */
   char getShortName();

   /**
    * Return the current {@link UIValidator} with which this {@link InputComponent} should be validated.
    */
   Set<UIValidator> getValidators();

   /**
    * Set this {@link InputComponent} to be enabled.
    */
   IMPLTYPE setEnabled(boolean b);

   /**
    * Set the {@link Callable} object to supply the value when {@link InputComponent#isEnabled()} is called.
    */
   IMPLTYPE setEnabled(Callable<Boolean> callable);

   /**
    * Set the label for this {@link InputComponent}. This is typically used when displaying inputs in more graphical
    * layouts, or when a short description of the input is required.
    */
   IMPLTYPE setLabel(String label);

   /**
    * Set a few sentences describing the purpose of this {@link InputComponent}.
    */
   IMPLTYPE setDescription(String description);

   /**
    * Set a few sentences describing the purpose of this {@link InputComponent}.
    */
   IMPLTYPE setDescription(Callable<String> description);

   /**
    * Set this {@link InputComponent} to be a required input.
    */
   IMPLTYPE setRequired(boolean required);

   /**
    * Set the {@link Callable} object to supply the value when {@link InputComponent#isRequired()} is called.
    */
   IMPLTYPE setRequired(Callable<Boolean> required);

   /**
    * Set the message to be displayed when this {@link InputComponent} is required and no primary or default value has
    * been set.
    */
   IMPLTYPE setRequiredMessage(String message);

   /**
    * Set the {@link Callable} object to supply the message to be displayed when this {@link InputComponent} is required
    * and no primary or default value has been set.
    */
   IMPLTYPE setRequiredMessage(Callable<String> message);

   /**
    * Get the current {@link Converter} instance with which this {@link InputComponent} value should be converted to and
    * from {@link String} form.
    */
   Converter<String, VALUETYPE> getValueConverter();

   /**
    * Set the current {@link Converter} instance with which this {@link InputComponent} value should be converted to and
    * from {@link String} form
    */
   IMPLTYPE setValueConverter(Converter<String, VALUETYPE> converter);

   /**
    * Add a new {@link UIValidator} with which this {@link InputComponent} should be validated.
    */
   IMPLTYPE addValidator(UIValidator validator);

   /**
    * Returns the value associated with this component. If the value is not set, the default value is returned. If the
    * default value is not set, <code>null</code> is returned
    */
   Object getValue();

   /**
    * Listens for changes in the value of this input
    */
   ListenerRegistration<ValueChangeListener> addValueChangeListener(ValueChangeListener listener);

   /**
    * Validate the current {@link UIInput}.
    * 
    * @param validator the {@link UIValidationContext} object that holds validation errors
    */
   void validate(UIValidationContext context);

   /**
    * A note is a description about the value of this input. in a GUI environment, it is displayed below the input.
    * 
    * @param note to be displayed below the input in GUIs
    */
   IMPLTYPE setNote(String note);

   /**
    * A note is a description about the value of this input. in a GUI environment, it is displayed below the input.
    * 
    * @param note to be displayed below the input in GUIs
    */
   IMPLTYPE setNote(Callable<String> note);

   /**
    * A note is a description about the value of this input. in a GUI environment, it is displayed below the input.
    * 
    * @return the note to be displayed below the input in GUIs
    */
   String getNote();

   /**
    * @return if this input is discouraged from using, typically because it is dangerous, or because a better
    *         alternative exists
    */
   boolean isDeprecated();

   /**
    * A deprecated input is discouraged from using, typically because it is dangerous, or because a better alternative
    * exists
    * 
    * @param deprecated the flag indicating that this {@link InputComponent} is deprecated
    */
   IMPLTYPE setDeprecated(boolean deprecated);

   /**
    * @return a deprecated message to be displayed when {@link InputComponent#isDeprecated()} returns <code>true</code>.
    */
   String getDeprecatedMessage();

   /**
    * @param message The deprecated message to be displayed when {@link InputComponent#isDeprecated()} returns
    *           <code>true</code>.
    */
   IMPLTYPE setDeprecatedMessage(String message);
}
