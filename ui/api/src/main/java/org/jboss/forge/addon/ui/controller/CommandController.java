/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.controller;

import java.util.List;
import java.util.Map;

import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.result.Result;

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
    * @throws Exception if problems occurred during initialization.
    */
   void initialize() throws Exception;

   /**
    * Returns <code>true</code> if {@link CommandController#initialize()} has been called.
    */
   boolean isInitialized();

   /**
    * Calls {@link UICommand#execute(org.jboss.forge.addon.ui.context.UIExecutionContext)}. Causes available
    * {@link CommandExecutionListener} instances to be called.
    * 
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    * @throws Exception if problems occurred during initialization
    */
   Result execute() throws Exception;

   /**
    * Calls {@link UICommand#validate(org.jboss.forge.addon.ui.context.UIValidationContext)}, and
    * {@link InputComponent#validate(org.jboss.forge.addon.ui.context.UIValidationContext)} for each enabled
    * {@link InputComponent}.
    * 
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   List<UIMessage> validate();

   /**
    * Calls {@link InputComponent#validate(org.jboss.forge.addon.ui.context.UIValidationContext)} for the given
    * {@link InputComponent} belonging to the underlying {@link UICommand}.
    * 
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   List<UIMessage> validate(InputComponent<?, ?> input);

   /**
    * Returns <code>true</code> if the {@link CommandController#validate()} method contains no {@link UIMessage}
    * instances with an ERROR severity
    * 
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   boolean isValid();

   /**
    * Sets the value of {@link InputComponent} with the given name.
    * 
    * @throws IllegalArgumentException if no {@link InputComponent} with the given name exists
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   CommandController setValueFor(String inputName, Object value) throws IllegalArgumentException;

   /**
    * Gets the value of {@link InputComponent} with the given name.
    * 
    * @throws IllegalArgumentException if no {@link InputComponent} with the given name exists
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   Object getValueFor(String inputName) throws IllegalArgumentException;

   /**
    * Returns a {@link Map} of {@link InputComponent} instances for this command.
    * 
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   Map<String, InputComponent<?, ?>> getInputs();

   /**
    * Returns a {@link InputComponent} instances for this command given an input name. May be <code>null</code>.
    *
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   InputComponent<?, ?> getInput(String inputName);

   /**
    * Returns true if the {@link InputComponent} instances exists for this command given an input name.
    *
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   boolean hasInput(String inputName);

   /**
    * @return the result of the current {@link UICommand#getMetadata(UIContext)}. Does not require initialization.
    */
   UICommandMetadata getMetadata();

   /**
    * @return if the current {@link UICommand#isEnabled(UIContext)} is <code>true</code>. Does not require
    *         initialization.
    */
   boolean isEnabled();

   /**
    * @return the underlying {@link UICommand}.
    */
   UICommand getCommand();

   /**
    * @return the underlying {@link UIContext} object.
    */
   UIContext getContext();

   /**
    * Returns <code>true</code> if {@link UICommand#execute(org.jboss.forge.addon.ui.context.UIExecutionContext)} can be
    * called.
    * 
    * @throws IllegalStateException if {@link #initialize()} has not been called before invoking this method.
    */
   boolean canExecute();

}