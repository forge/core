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
import org.jboss.aesh.console.command.container.AeshCommandContainer;
import org.jboss.aesh.console.command.container.CommandContainer;
import org.jboss.aesh.console.command.registry.AeshCommandRegistryBuilder;
import org.jboss.aesh.console.command.registry.CommandRegistry;
import org.jboss.aesh.extensions.grep.Grep;
import org.jboss.aesh.extensions.less.aesh.Less;
import org.jboss.aesh.extensions.more.aesh.More;
import org.jboss.forge.addon.shell.CommandManager;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;

/**
 * Forge implementation of {@link CommandRegistry}.
 * 
 * It delegates to the Aesh commands if no command is found
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeCommandRegistry implements CommandRegistry
{
   private final CommandManager commandManager;
   private final ShellImpl shell;
   private final CommandRegistry aeshCommandRegistry;

   public ForgeCommandRegistry(ShellImpl shell, CommandManager commandManager)
   {
      this.shell = shell;
      this.commandManager = commandManager;

      // Use Aesh commands
      this.aeshCommandRegistry = new AeshCommandRegistryBuilder()
               .command(Grep.class)
               .command(Less.class)
               .command(More.class)
               .create();
   }

   @Override
   public CommandContainer getCommand(String name, String completeLine) throws CommandNotFoundException
   {
      try
      {
         return getForgeCommand(name, completeLine);
      }
      catch (CommandNotFoundException cnfe)
      {
         // Not a forge command, fallback to aesh command
         return aeshCommandRegistry.getCommand(name, completeLine);
      }
   }

   private CommandContainer getForgeCommand(String name, String completeLine) throws CommandNotFoundException
   {
      ShellContextImpl shellContext = shell.newShellContext();
      AbstractShellInteraction cmd = commandManager.findCommand(shellContext, name);
      if (cmd == null)
      {
         throw new CommandNotFoundException(name);
      }
      try
      {
         CommandLineParser parser = cmd.getParser(shellContext, completeLine == null ? name : completeLine);
         CommandAdapter command = new CommandAdapter(shell, cmd);
         return new AeshCommandContainer(parser, command);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error while creating parser: " + e.getMessage(), e);
      }
      finally
      {
         shellContext.destroy();
      }
   }

   @Override
   public Set<String> getAllCommandNames()
   {
      Set<String> allCommands = new TreeSet<String>();
      allCommands.addAll(getForgeCommandNames());
      allCommands.addAll(aeshCommandRegistry.getAllCommandNames());
      return allCommands;
   }

   private Set<String> getForgeCommandNames()
   {
      ShellContextImpl newShellContext = shell.newShellContext();
      try
      {
         return commandManager.getAllCommandNames(newShellContext);
      }
      finally
      {
         newShellContext.destroy();
      }
   }

}
