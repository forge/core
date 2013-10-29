/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.List;

import org.jboss.aesh.cl.validator.CommandValidator;
import org.jboss.aesh.cl.validator.CommandValidatorException;
import org.jboss.aesh.console.command.Command;
import org.jboss.forge.addon.shell.ui.ShellValidationContext;

/**
 * Forge {@link CommandValidator} implementation
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public enum ForgeCommandValidator implements CommandValidator
{
   INSTANCE;

   @SuppressWarnings("rawtypes")
   @Override
   public void validate(Command command) throws CommandValidatorException
   {
      if (command instanceof CommandAdapter)
      {
         ShellValidationContext context = ((CommandAdapter) command).validate();
         List<String> errors = context.getErrors();
         if (!errors.isEmpty())
         {
            throw new CommandValidatorException(errors.get(0));
         }
      }
   }
}
