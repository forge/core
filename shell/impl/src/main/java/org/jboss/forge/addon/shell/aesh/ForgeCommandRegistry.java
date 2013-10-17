/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.Set;

import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.console.command.AeshCommandContainer;
import org.jboss.aesh.console.command.CommandContainer;
import org.jboss.aesh.console.command.CommandNotFoundException;
import org.jboss.aesh.console.command.CommandRegistry;
import org.jboss.forge.addon.shell.CommandManager;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;

/**
 * Forge implementation of {@link CommandRegistry}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeCommandRegistry implements CommandRegistry
{
   private final CommandManager commandManager;
   private final ShellImpl shell;

   public ForgeCommandRegistry(ShellImpl shell, CommandManager commandManager)
   {
      this.shell = shell;
      this.commandManager = commandManager;
   }

   @Override
   public CommandContainer getCommand(String name, String completeLine) throws CommandNotFoundException
   {
      ShellContextImpl shellContext = shell.newShellContext();
      AbstractShellInteraction cmd = commandManager.findCommand(shellContext, name);
      if (cmd == null)
         throw new CommandNotFoundException(name);
      try
      {
         CommandLineParser parser = cmd.getParser(shellContext, completeLine);
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
