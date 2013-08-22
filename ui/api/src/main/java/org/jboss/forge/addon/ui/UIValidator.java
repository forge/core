/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui;

import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;

/**
 * Validate UI {@link InputComponent} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIValidator
{
   /**
    * Should validate against the current {@link UIInput} values supplied in the {@link UICommand} implementation class.
    * 
    * @param validator the {@link UIValidationContext} object that holds validation errors
    */
   public abstract void validate(UIValidationContext validator);
}