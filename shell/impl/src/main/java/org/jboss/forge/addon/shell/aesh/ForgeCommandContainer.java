/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.command.container.CommandContainer;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;

/**
 * {@link CommandContainer} implementation for Forge commands
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class ForgeCommandContainer implements CommandContainer
{
   private final ShellContextImpl context;
   private final CommandLineParser parser;
   private final Command<CommandInvocation> command;

   ForgeCommandContainer(ShellContextImpl context, CommandLineParser parser,
            Command<CommandInvocation> command)
   {
      this.context = context;
      this.parser = parser;
      this.command = command;
   }

   @Override
   public void close() throws Exception
   {
      context.close();
   }

   @SuppressWarnings("rawtypes")
   @Override
   public Command getCommand()
   {
      return command;
   }

   @Override
   public CommandLineParser getParser()
   {
      return parser;
   }

   @Override
   public boolean haveBuildError()
   {
      return false;
   }

   @Override
   public String getBuildErrorMessage()
   {
      return null;
   }

}
