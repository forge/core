/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.commands;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleCommand;
import org.jboss.aesh.extensions.manual.Man;
import org.jboss.aesh.parser.Parser;
import org.jboss.forge.addon.shell.CommandManager;
import org.jboss.forge.addon.shell.aesh.AbstractShellInteraction;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ManCommand extends AbstractNativeAeshCommand
{
   private CommandManager commandManager;

   @Inject
   @WithAttributes(label = "Arguments", required = true)
   private UIInputMany<String> arguments;

   @Inject
   public ManCommand(CommandManager commandManager)
   {
      this.commandManager = commandManager;
   }

   @Override
   public Metadata getMetadata()
   {
      return super.getMetadata().name("man")
               .description("man - an interface to the online reference manuals");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      arguments.setDefaultValue(Arrays.asList(getMetadata().getName()));
      arguments.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String value)
         {
            List<String> manCommands = new ArrayList<String>();
            // list all commands
            if (Strings.isNullOrEmpty(value))
            {
               Map<String, AbstractShellInteraction> enabledShellCommands = commandManager
                        .getEnabledShellCommands((ShellContext) context);
               manCommands.addAll(enabledShellCommands.keySet());
            }
            // find the last
            else
            {
               String item = Parser.findEscapedSpaceWordCloseToEnd(value.trim());
               Collection<AbstractShellInteraction> matchingCommands = commandManager.findMatchingCommands(
                        (ShellContext) context, item);
               for (AbstractShellInteraction cmd : matchingCommands)
               {
                  manCommands.add(cmd.getName());
               }
            }

            return manCommands;
         }
      });
      builder.add(arguments);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      URL commandDocLocation = getCommandDocLocation((ShellContext) validator.getUIContext());
      if (commandDocLocation == null)
      {
         String commandName = arguments.getValue().iterator().next();
         validator.addValidationError(arguments, "No manual page found for: " + commandName);
      }
   }

   @Override
   public ConsoleCommand getConsoleCommand(ShellContext context) throws IOException
   {
      Console console = context.getProvider().getConsole();
      Man man = new Man(console);
      return man;
   }

   private URL getCommandDocLocation(ShellContext context)
   {
      final URL result;
      String commandName = arguments.getValue().iterator().next();
      AbstractShellInteraction shellCommand = commandManager.findCommand(context, commandName);
      if (shellCommand != null)
      {
         result = shellCommand.getSourceCommand().getMetadata().getDocLocation();
      }
      else
      {
         result = null;
      }
      return result;
   }
}
