/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.io.IOException;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.cl.parser.CommandLineParserException;
import org.jboss.aesh.cl.validator.CommandValidatorException;
import org.jboss.aesh.cl.validator.OptionValidatorException;
import org.jboss.aesh.console.AeshContext;
import org.jboss.aesh.console.InvocationProviders;
import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.container.CommandContainer;
import org.jboss.aesh.console.command.container.CommandContainerResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.aesh.parser.AeshLine;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;

/**
 * {@link CommandContainer} implementation for Forge commands
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("unchecked")
class ForgeCommandContainer implements CommandContainer
{
   private final ShellContextImpl context;
   private final CommandLineParser<?> parser;
   private final Command<CommandInvocation> command;

   ForgeCommandContainer(ShellContextImpl context, CommandLineParser<?> parser,
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

   @Override
   public CommandLineParser<?> getParser()
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

   @Override
   public String printHelp(String childCommandName)
   {
      return parser.getChildParser(childCommandName).printHelp();
   }

   @Override
   public CommandContainerResult executeCommand(AeshLine line, InvocationProviders invocationProviders,
            AeshContext aeshContext, CommandInvocation commandInvocation) throws CommandLineParserException,
                     OptionValidatorException, CommandValidatorException, IOException, InterruptedException
   {
      CommandLine<?> commandLine = parser.parse(line, false);
      commandLine.getParser()
               .getCommandPopulator()
               .populateObject(commandLine, invocationProviders, aeshContext, true);
      if (commandLine.getParser().getProcessedCommand().getValidator() != null
               && !commandLine.hasOptionWithOverrideRequired())
         parser.getProcessedCommand().getValidator().validate(command);
      CommandResult result = command.execute(commandInvocation);

      return new CommandContainerResult(commandLine.getParser().getProcessedCommand().getResultHandler(), result);
   }

}