/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.validate;

import java.util.Collection;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * Listen for UI commands/inputs validations. Instances of this class will be called by the {@link CommandController}
 * when {@link CommandController#validate()} is invoked.
 * 
 * Implementations are expected to provide info/error/warn messages in a generic way (eg. displaying a warning when a
 * deprecated command is executed).
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIValidationListener
{
   /**
    * Called <b>before</b> {@link UICommand#validate(UIValidationContext)} is invoked
    * 
    * @param context the {@link UIValidationContext} object that holds validation errors
    * @param command the {@link UICommand} instance
    * @param the inputs for the initialized {@link UICommand}
    */
   void preValidate(UIValidationContext context, UICommand command, Collection<InputComponent<?, ?>> inputs);

   /**
    * Called <b>after</b> {@link UICommand#validate(UIValidationContext)} is invoked
    * 
    * @param context the {@link UIValidationContext} object that holds validation errors
    * @param command the {@link UICommand} instance
    * @param the inputs for the initialized {@link UICommand}
    */
   void postValidate(UIValidationContext context, UICommand command, Collection<InputComponent<?, ?>> inputs);
}
