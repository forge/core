/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.console.command.CommandNotFoundException;
import org.jboss.aesh.console.command.container.CommandContainer;
import org.jboss.aesh.console.command.registry.AeshCommandRegistryBuilder;
import org.jboss.aesh.console.command.registry.CommandRegistry;
import org.jboss.aesh.console.man.Man;
import org.jboss.aesh.extensions.grep.Grep;
import org.jboss.aesh.extensions.less.aesh.Less;
import org.jboss.aesh.extensions.more.aesh.More;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.AeshUICommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandControllerFactory;
import org.jboss.forge.addon.ui.controller.SingleCommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.exception.ContainerException;

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
   private Furnace furnace;
   private final ShellImpl shell;

   private final CommandFactory commandFactory;
   private final CommandRegistry aeshCommandRegistry;

   private CommandLineUtil commandLineUtil;
   private final CommandControllerFactory commandControllerFactory;

   public ForgeCommandRegistry(Furnace furnace, ShellImpl shell, AddonRegistry addonRegistry)
   {
      this.furnace = furnace;
      this.shell = shell;
      this.commandFactory = addonRegistry.getServices(CommandFactory.class).get();
      this.commandControllerFactory = addonRegistry.getServices(CommandControllerFactory.class).get();
      this.commandLineUtil = new CommandLineUtil(addonRegistry);
      ConverterFactory converterFactory = addonRegistry.getServices(ConverterFactory.class).get();
      InputComponentFactory inputComponentFactory = addonRegistry.getServices(InputComponentFactory.class).get();

      // Use Aesh commands
      Man manCommand = new Man(
               new ForgeManProvider(shell, commandFactory, converterFactory, inputComponentFactory, commandLineUtil));
      this.aeshCommandRegistry = new AeshCommandRegistryBuilder()
               .command(Grep.class)
               .command(Less.class)
               .command(More.class)
               .command(manCommand)
               .create();
      manCommand.setRegistry(this);
   }

   @Override
   public CommandContainer<?> getCommand(String name, String completeLine) throws CommandNotFoundException
   {
      waitUntilStarted();

      ShellContextImpl shellContext = shell.createUIContext();
      try
      {
         return getForgeCommand(shellContext, name, completeLine);
      }
      catch (CommandNotFoundException cnfe)
      {
         // Not a forge command, fallback to aesh command
         CommandContainer<?> nativeCommand = aeshCommandRegistry.getCommand(name, completeLine);
         AeshUICommand aeshCommand = new AeshUICommand(nativeCommand);
         SingleCommandController controller = commandControllerFactory.createSingleController(shellContext, shell,
                  aeshCommand);
         try
         {
            controller.initialize();
         }
         catch (Exception e)
         {
            // Do nothing
         }
         ShellSingleCommand cmd = new ShellSingleCommand(controller, shellContext, commandLineUtil);
         CommandAdapter commandAdapter = new CommandAdapter(shell, shellContext, cmd);
         return new ForgeCommandContainer(shellContext, aeshCommand.getCommandLineParser(), commandAdapter);
      }
   }

   private CommandContainer<?> getForgeCommand(ShellContextImpl shellContext, String name, String completeLine)
            throws CommandNotFoundException
   {
      AbstractShellInteraction cmd = findCommand(shellContext, name);
      if (cmd == null || !cmd.getController().isEnabled())
      {
         throw new CommandNotFoundException(name);
      }
      try
      {
         CommandAdapter command = new CommandAdapter(shell, shellContext, cmd);
         CommandLineParser<?> parser = cmd.getParser(shellContext, completeLine == null ? name : completeLine, command);
         return new ForgeCommandContainer(shellContext, parser, command);
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error while creating parser: " + e.getMessage(), e);
      }
   }

   private AbstractShellInteraction findCommand(ShellContext shellContext, String commandName)
   {
      AbstractShellInteraction result = null;
      UICommand cmd = commandFactory.getNewCommandByName(shellContext, commandName);
      if (cmd != null && cmd.isEnabled(shellContext))
      {
         CommandController controller = commandControllerFactory.createController(shellContext, shell, cmd);
         if (controller instanceof WizardCommandController)
         {
            result = new ShellWizard((WizardCommandController) controller, shellContext, commandLineUtil, this);
         }
         else
         {
            result = new ShellSingleCommand(controller, shellContext, commandLineUtil);
         }
      }
      return result;
   }

   @Override
   public Set<String> getAllCommandNames()
   {
      if (!furnace.getStatus().isStarted())
         return Collections.emptySet();
      Set<String> allCommands = new TreeSet<>();
      allCommands.addAll(getForgeCommandNames());
      allCommands.addAll(aeshCommandRegistry.getAllCommandNames());
      return allCommands;
   }

   private void waitUntilStarted()
   {
      while (furnace.getStatus().isStarting())
      {
         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new ContainerException("Interrputed while waiting for STARTED state.", e);
         }
      }
   }

   private Set<String> getForgeCommandNames()
   {
      try (ShellContextImpl newShellContext = shell.createUIContext())
      {
         return commandFactory.getEnabledCommandNames(newShellContext);
      }
   }

   @Override
   public void removeCommand(String name)
   {
      try
      {
         if (aeshCommandRegistry.getCommand(name, null) != null)
            aeshCommandRegistry.removeCommand(name);
      }
      catch (CommandNotFoundException e)
      {
         throw new RuntimeException("Error while removing command: " + e.getMessage(), e);
      }

   }

   @Override
   public void completeCommandName(CompleteOperation completeOperation)
   {
      List<String> names = new ArrayList<>();
      for (String commandName : getAllCommandNames())
      {
         if (commandName.startsWith(completeOperation.getBuffer()))
         {
            names.add(commandName);
         }
      }
      completeOperation.addCompletionCandidates(names);
   }

}