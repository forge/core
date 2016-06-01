/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.context;

import java.util.List;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIMessage;

/**
 * Stores information about validation.
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIValidationContext extends UIContextProvider
{
   /**
    * Implementations should call this method to let the UI provider aware of possible errors
    * 
    * @param input
    * @param errorMessage
    */
   public void addValidationError(InputComponent<?, ?> input, String errorMessage);

   /**
    * Implementations should call this method to let the UI provider aware of possible warnings
    * 
    * @param input
    * @param warningMessage
    */
   public void addValidationWarning(InputComponent<?, ?> input, String warningMessage);

   /**
    * Implementations should call this method to let the UI provider aware of possible informations
    * 
    * @param input
    * @param infoMessage
    */
   public void addValidationInformation(InputComponent<?, ?> input, String infoMessage);

   /**
    * @return the current focused {@link InputComponent}.<code>null</code> if cannot be determined by the UI provider.
    */
   public InputComponent<?, ?> getCurrentInputComponent();

   /**
    * @return the {@link UIMessage} objects added to this context
    */
   public List<UIMessage> getMessages();
}
