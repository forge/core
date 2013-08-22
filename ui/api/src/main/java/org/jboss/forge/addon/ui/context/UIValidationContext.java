/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.context;

import org.jboss.forge.addon.ui.input.InputComponent;

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
}
