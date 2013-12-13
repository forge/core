/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import java.util.List;
import java.util.Set;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.validation.UIValidationMessage;

/**
 * A Controller for a specific {@link UICommand}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CommandController extends AutoCloseable
{
   /**
    * Initializes this controller by calling {@link UICommand#initializeUI(org.jboss.forge.addon.ui.context.UIBuilder)}
    * on the underlying {@link UICommand} instance.
    * 
    * @throws Exception if problems occurred during initialization
    */
   void initialize() throws Exception;

   /**
    * Returns <code>true</code> if {@link CommandController#initialize()} has been called
    */
   boolean isInitialized();

   /**
    * Calls {@link UICommand#execute(org.jboss.forge.addon.ui.context.UIExecutionContext)}. Causes available
    * {@link CommandExecutionListener} instances to be called.
    * 
    * @throws Exception if problems occurred during initialization
    */
   Result execute() throws Exception;

   /**
    * Calls {@link UICommand#validate(org.jboss.forge.addon.ui.context.UIValidationContext)}, and
    * {@link InputComponent#validate(org.jboss.forge.addon.ui.context.UIValidationContext)} for each enabled
    * {@link InputComponent}.
    */
   List<UIValidationMessage> validate();

   /**
    * Returns <code>true</code> if the {@link CommandController#validate()} method contains no
    * {@link UIValidationMessage} instances with an ERROR severity
    */
   boolean isValid();

   /**
    * Sets the value of {@link InputComponent} with the given name.
    * 
    * @throws IllegalArgumentException if no input with the given name exists
    */
   CommandController setValueFor(String inputName, Object value) throws IllegalArgumentException;

   /**
    * Gets the value of {@link InputComponent} with the given name.
    * 
    * @throws IllegalArgumentException if no input with the given name exists
    */
   Object getValueFor(String inputName) throws IllegalArgumentException;

   /**
    * Returns a {@link List} of {@link InputComponent} instances for this command
    */
   List<InputComponent<?, ?>> getInputs();

   /**
    * Returns a {@link Set} of the input names for this command
    */
   Set<String> getInputNames();

   /**
    * @param inputName the input name
    * @return the {@link InputComponent} in this command
    * @throws IllegalArgumentException if no input with the given name exists
    */
   InputComponent<?, ?> getInput(String inputName) throws IllegalArgumentException;

   /**
    * Returns <code>true</code> if the {@link InputComponent} exists in the underlying {@link UICommand}
    * 
    * @param inputName the input name
    */
   boolean hasInput(String inputName);

   /**
    * @return the command metadata
    */
   UICommandMetadata getMetadata();

   /**
    * @return if the command is enabled
    */
   boolean isEnabled();

   /**
    * @return the underlying command
    */
   UICommand getCommand();

   /**
    * @return the underlying {@link UIContext} object
    */
   UIContext getContext();
}