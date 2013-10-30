/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.command.CommandInvocation;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.extensions.manual.ManCommand;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ShellMessages;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellValidationContext;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;

/**
 * Adapts the current {@link AbstractShellInteraction} to a {@link Command}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class CommandAdapter implements Command<CommandInvocation>, ManCommand
{
   private final ShellImpl shell;
   private final AbstractShellInteraction interaction;

   public CommandAdapter(ShellImpl shell, AbstractShellInteraction interaction)
   {
      this.shell = shell;
      this.interaction = interaction;
   }

   @SuppressWarnings("unchecked")
   @Override
   public CommandResult execute(CommandInvocation commandInvocation) throws IOException
   {
      boolean failure;
      ShellValidationContext validationContext = interaction.validate();
      List<String> errors = validationContext.getErrors();
      if (errors.isEmpty())
      {
         Result result = shell.execute(interaction);
         failure = (result instanceof Failed);
         if (result != null && result.getMessage() != null && !result.getMessage().isEmpty())
         {
            if (failure)
            {
               ShellMessages.error(shell.getConsole().getShell().err(), result.getMessage());
            }
            else
            {
               ShellMessages.success(shell.getConsole().getShell().out(), result.getMessage());
            }
         }
         ShellContext context = interaction.getContext();
         Object selection = context.getSelection();
         if (selection != null)
         {
            if (selection instanceof Iterable<?>)
            {
               for (FileResource<?> item : (Iterable<FileResource<?>>) selection)
               {
                  if (item != null)
                  {
                     shell.setCurrentResource(item);
                     break;
                  }
               }
            }
            else
            {
               shell.setCurrentResource((FileResource<?>) selection);
            }
         }

      }
      else
      {
         failure = true;
         // Display the error messages
         for (String error : errors)
         {
            ShellMessages.error(shell.getConsole().getShell().err(), error);
         }
      }
      return failure ? CommandResult.FAILURE : CommandResult.SUCCESS;
   }

   ShellValidationContext validate()
   {
      return interaction.validate();
   }

   @Override
   public File getManLocation()
   {
      return interaction.getManLocation();
   }
}