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
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.output.UIMessage.Severity;

/**
 * Forge {@link CommandValidator} implementation
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("rawtypes")
public enum ForgeCommandValidator implements CommandValidator
{
   INSTANCE;

   @Override
   public void validate(Command command) throws CommandValidatorException
   {
      if (command instanceof CommandAdapter)
      {
         List<UIMessage> messages = ((CommandAdapter) command).validate();
         for (UIMessage message : messages)
         {
            if (message.getSeverity() == Severity.ERROR)
               throw new CommandValidatorException(message.getDescription());
         }
      }
   }
}
