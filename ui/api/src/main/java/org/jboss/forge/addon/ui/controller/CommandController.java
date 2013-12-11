/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import java.util.List;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.validation.UIValidationMessage;

/**
 * A Controller for a specific {@link UICommand}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface CommandController
{
   /**
    * @return the command metadata
    */
   public UICommandMetadata getMetadata();

   /**
    * Is the wizard allowed to execute ?
    */
   public boolean canExecute();

   /**
    * Finish clicked
    * 
    * @throws Exception if anything wrong happens
    */
   public Result execute() throws Exception;

   /**
    * Call validate for the current page + inputs
    */
   public List<UIValidationMessage> validate();

   /**
    * Sets the value for a specific input named by the specified name
    */
   public CommandController setValueFor(String inputName, Object value);

   /**
    * Gets the value for a specific input named by the specified name
    */
   public Object getValueFor(String inputName);

   /**
    * @return if the command is enabled
    */
   public boolean isEnabled();

   /**
    * Returns the inputs for this command
    */
   public List<InputComponent<?, Object>> getInputs();

   /**
    * @param inputName the input name
    * @return the {@link InputComponent} in this command
    */
   public InputComponent<?, Object> getInput(String inputName);
}