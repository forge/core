/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.validate;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.controller.CommandController;

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
    * Perform validation with the given {@link UIValidationContext} and the {@link UICommand}
    * 
    * @param context the {@link UIValidationContext} object that holds validation errors
    * @param command the {@link UICommand} instance that was
    */
   void preValidate(UIValidationContext context, UICommand command);

   /**
    * Perform validation with the given {@link UIValidationContext} and the {@link UICommand} after the validation in
    * the command was performed
    * 
    * @param context the {@link UIValidationContext} object that holds validation errors
    * @param command the {@link UICommand} instance that was
    */
   void postValidate(UIValidationContext context, UICommand command);
}
