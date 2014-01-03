/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.Set;
import java.util.TreeSet;

import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.console.command.CommandNotFoundException;
import org.jboss.aesh.console.command.container.CommandContainer;
import org.jboss.aesh.console.command.registry.AeshCommandRegistryBuilder;
import org.jboss.aesh.console.command.registry.CommandRegistry;
import org.jboss.aesh.console.man.Man;
import org.jboss.aesh.extensions.grep.Grep;
import org.jboss.aesh.extensions.less.aesh.Less;
import org.jboss.aesh.extensions.more.aesh.More;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.CommandManager;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.AeshUICommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.controller.SingleCommandController;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * Forge implementation of {@link CommandRegistry}.
 * 
 * It delegates to the Aesh commands if no command is found
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ForgeCommandRegistry implements CommandRegistry
{
   private final CommandManager commandManager;
   private final ShellImpl shell;
   private final CommandRegistry aeshCommandRegistry;
   private final ConverterFactory converterFactory;

   private CommandLineUtil commandLineUtil;
   private final CommandControllerFactory commandFactory;

   public ForgeCommandRegistry(ShellImpl shell, CommandManager commandManager, CommandControllerFactory commandFactory,
            ConverterFactory converterFactory)
   {
      this.shell = shell;
      this.commandManager = commandManager;
      this.commandFactory = commandFactory;
      this.converterFactory = converterFactory;

      // Use Aesh commands
      Man manCommand = new Man(new ForgeManProvider(shell, commandManager));
      this.aeshCommandRegistry = new AeshCommandRegistryBuilder()
               .command(Grep.class)
               .command(Less.class)
               .command(More.class)
               .command(manCommand)
               .create();
      manCommand.setRegistry(this);
   }

   @Override
   public CommandContainer getCommand(String name, String completeLine) throws CommandNotFoundException
   {
      ShellContextImpl shellContext = shell.createUIContext();
      try
      {
         return getForgeCommand(shellContext, name, completeLine);
      }
      catch (CommandNotFoundException cnfe)
      {
         // Not a forge command, fallback to aesh command
         CommandContainer nativeCommand = aeshCommandRegistry.getCommand(name, completeLine);
         AeshUICommand aeshCommand = new AeshUICommand(nativeCommand);
         SingleCommandController controller = commandFactory.createSingleController(shellContext, aeshCommand, shell);
         try
         {
            controller.initialize();
         }
         catch (Exception e)
         {
            // Do nothing
         }
         ShellSingleCommand cmd = new ShellSingleCommand(controller, shellContext, getCommandLineUtil());
         CommandAdapter commandAdapter = new CommandAdapter(shell, shellContext, cmd);
         return new ForgeCommandContainer(shellContext, aeshCommand.getCommandLineParser(), commandAdapter);
      }
   }

   private CommandContainer getForgeCommand(ShellContextImpl shellContext, String name, String completeLine)
            throws CommandNotFoundException
   {
      AbstractShellInteraction cmd = findCommand(shellContext, name);
      if (cmd == null)
      {
         throw new CommandNotFoundException(name);
      }
      try
      {
         CommandLineParser parser = cmd.getParser(shellContext, completeLine == null ? name : completeLine);
         CommandAdapter command = new CommandAdapter(shell, shellContext, cmd);
         return new ForgeCommandContainer(shellContext, parser, command);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error while creating parser: " + e.getMessage(), e);
      }
   }

   private AbstractShellInteraction findCommand(ShellContext shellContext, String commandName)
   {
      AbstractShellInteraction result = null;
      CommandLineUtil cmdLineUtil = getCommandLineUtil();
      for (UICommand cmd : Commands.getEnabledCommands(commandManager.getAllCommands(), shellContext))
      {
         if (commandName.equals(commandManager.getCommandName(shellContext, cmd)))
         {
            if (cmd instanceof UIWizard)
            {
               result = new ShellWizard(commandFactory.createWizardController(shellContext, (UIWizard) cmd, shell),
                        shellContext, cmdLineUtil, this);
            }
            else
            {
               result = new ShellSingleCommand(commandFactory.createSingleController(shellContext, cmd, shell),
                        shellContext, cmdLineUtil);
            }
            break;
         }
      }
      return result;
   }

   private CommandLineUtil getCommandLineUtil()
   {
      if (commandLineUtil == null)
      {
         commandLineUtil = new CommandLineUtil(getConverterFactory());
      }
      return commandLineUtil;
   }

   private ConverterFactory getConverterFactory()
   {
      return converterFactory;
   }

   @Override
   public Set<String> getAllCommandNames()
   {
      Set<String> allCommands = new TreeSet<>();
      allCommands.addAll(getForgeCommandNames());
      allCommands.addAll(aeshCommandRegistry.getAllCommandNames());
      return allCommands;
   }

   private Set<String> getForgeCommandNames()
   {
      try (ShellContextImpl newShellContext = shell.createUIContext())
      {
         return commandManager.getAllCommandNames(newShellContext);
      }
   }

}
