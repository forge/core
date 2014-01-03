/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.internal.ProcessedCommand;
import org.jboss.aesh.cl.parser.CommandLineCompletionParser;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.cl.parser.CommandPopulator;
import org.jboss.aesh.cl.validator.OptionValidatorException;
import org.jboss.aesh.console.InvocationProviders;
import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.container.CommandContainer;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.controller.CommandExecutionListener;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * This class acts as an adapter for native Aesh commands to ensure that {@link CommandExecutionListener} objects are
 * fired
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Vetoed
public class AeshUICommand implements UICommand
{

   private final Command<CommandInvocation> command;
   private final CommandPopulator commandPopulator;
   private final CommandLineParser commandLineParser;

   @SuppressWarnings("unchecked")
   public AeshUICommand(CommandContainer container)
   {
      this.command = container.getCommand();
      this.commandLineParser = container.getParser();
      this.commandPopulator = commandLineParser.getCommandPopulator();
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(AeshUICommand.class);
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // Do Nothing
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Map<Object, Object> attributeMap = context.getUIContext().getAttributeMap();
      CommandInvocation commandInvocation = (CommandInvocation) attributeMap.get(CommandInvocation.class);
      CommandResult result = command.execute(commandInvocation);
      return result == CommandResult.SUCCESS ? Results.success() : Results.fail("Failure while executing aesh command");
   }

   public CommandLineParser getCommandLineParser()
   {
      return new DelegateCommandLineParser();
   }

   /**
    * Returns a {@link CommandLineParser} implementation that delegates to the correct populator
    * 
    * @author <a href="ggastald@redhat.com">George Gastaldi</a>
    */
   class DelegateCommandLineParser implements CommandLineParser
   {
      @Override
      public ProcessedCommand getCommand()
      {
         return commandLineParser.getCommand();
      }

      @Override
      public CommandLineCompletionParser getCompletionParser()
      {
         return commandLineParser.getCompletionParser();
      }

      @Override
      public CommandPopulator getCommandPopulator()
      {
         return new CommandPopulator()
         {
            @Override
            public void populateObject(Object instance, CommandLine line, InvocationProviders invocationProviders,
                     boolean validate) throws CommandLineParserException, OptionValidatorException
            {
               commandPopulator.populateObject(command, line, invocationProviders, validate);
            }
         };
      }

      @Override
      public String printHelp()
      {
         return commandLineParser.printHelp();
      }

      @Override
      public CommandLine parse(String line)
      {
         return commandLineParser.parse(line);
      }

      @Override
      public CommandLine parse(String line, boolean ignoreRequirements)
      {
         return commandLineParser.parse(line, ignoreRequirements);
      }

      @Override
      public CommandLine parse(List<String> lines, boolean ignoreRequirements)
      {
         return commandLineParser.parse(lines, ignoreRequirements);
      }

   }

}
