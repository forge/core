/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.output.UIMessage;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.CompositeResult;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.furnace.util.Strings;

/**
 * Adapts the current {@link AbstractShellInteraction} to a {@link Command}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class CommandAdapter implements Command<CommandInvocation>
{
   private static final Logger log = Logger.getLogger(CommandAdapter.class.getName());

   private final ShellImpl shell;
   private final ShellContext shellContext;
   private final AbstractShellInteraction interaction;

   public CommandAdapter(ShellImpl shell, ShellContext shellContext, AbstractShellInteraction interaction)
   {
      this.shell = shell;
      this.shellContext = shellContext;
      this.interaction = interaction;
   }

   public List<UIMessage> validate()
   {
      return interaction.getController().validate();
   }

   @Override
   public CommandResult execute(CommandInvocation commandInvocation) throws IOException
   {
      Map<Object, Object> attributeMap = shellContext.getAttributeMap();
      attributeMap.put(CommandInvocation.class, commandInvocation);
      boolean failure = false;
      // FORGE-1668: Prompt for required missing values
      try
      {
         failure = !interaction.promptRequiredMissingValues(shell);
      }
      catch (InterruptedException ie)
      {
         // <CTRL>+C was pressed.
         log.log(Level.FINE, "Caught InterruptedException while prompting in interactive mode", ie);
         failure = true;
      }
      if (!failure)
      {
         UIOutput output = shell.getOutput();
         for (UIMessage message : interaction.getController().validate())
         {
            switch (message.getSeverity())
            {
            case ERROR:
               failure = true;
               output.error(output.err(), message.getDescription());
               break;
            case INFO:
               output.info(output.out(), message.getDescription());
               break;
            case WARN:
               output.warn(output.out(), message.getDescription());
               break;
            }
         }
         if (!failure)
         {
            Result commandResult = null;
            try
            {
               commandResult = interaction.getController().execute();
            }
            catch (Exception e)
            {
               log.log(Level.SEVERE, "Failed to execute [" + interaction.getName() + "] due to exception.", e);
               commandResult = Results.fail(e.getMessage(), e);
            }
            failure = displayResult(commandResult);
            // If Exit was not called
            if (!Boolean.TRUE.equals(attributeMap.get("org.jboss.forge.exit")))
            {
               UISelection<?> selection = interaction.getContext().getSelection();
               if (selection != null && !selection.isEmpty())
               {
                  Object result = selection.get();
                  if (result instanceof Resource<?>)
                  {
                     shell.setCurrentResource((Resource<?>) result);
                  }
               }
            }
         }
      }
      return failure ? CommandResult.FAILURE : CommandResult.SUCCESS;
   }

   private boolean displayResult(Result result)
   {
      boolean failure = false;
      if (result instanceof CompositeResult)
      {
         for (Result thisResult : ((CompositeResult) result).getResults())
         {
            if (!displayResult(thisResult))
            {
               failure = true;
            }
         }
      }
      else if (result != null && !Strings.isNullOrEmpty(result.getMessage()))
      {
         UIOutput output = shell.getOutput();
         if (result instanceof Failed)
         {
            output.error(output.err(), result.getMessage());
            log.log(Level.SEVERE, result.getMessage(), ((Failed) result).getException());
            failure = true;
         }
         else
         {
            output.success(output.out(), result.getMessage());
            failure = false;
         }
      }
      return failure;
   }
}