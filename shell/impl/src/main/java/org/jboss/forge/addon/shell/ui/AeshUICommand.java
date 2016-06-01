/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.ui;

import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.internal.ProcessedCommand;
import org.jboss.aesh.cl.parser.CommandLineCompletionParser;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.cl.parser.CommandLineParserException;
import org.jboss.aesh.cl.parser.ParsedCompleteObject;
import org.jboss.aesh.cl.populator.CommandPopulator;
import org.jboss.aesh.cl.validator.OptionValidatorException;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.console.AeshContext;
import org.jboss.aesh.console.InvocationProviders;
import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.container.CommandContainer;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.aesh.parser.AeshLine;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
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
@SuppressWarnings("unchecked")
public class AeshUICommand implements UICommand
{
   private final CommandLineParser commandLineParser;

   public AeshUICommand(CommandContainer container)
   {
      this.commandLineParser = container.getParser();
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
      CommandResult result = commandLineParser.getCommand().execute(commandInvocation);
      if (result == CommandResult.FAILURE)
      {
         return Results.fail("Failure while executing aesh command");
      }
      else
      {
         return Results.success();
      }
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
      public CommandLineCompletionParser getCompletionParser()
      {
         return new CommandLineCompletionParser()
         {
            @Override
            public void injectValuesAndComplete(ParsedCompleteObject completeObject,
                     CompleteOperation completeOperation, InvocationProviders invocationProviders)
            {
               commandLineParser.getCompletionParser().injectValuesAndComplete(completeObject,
                        completeOperation, invocationProviders);
            }

            @Override
            public ParsedCompleteObject findCompleteObject(String line, int cursor) throws CommandLineParserException
            {
               return commandLineParser.getCompletionParser().findCompleteObject(line, cursor);
            }
         };
      }

      @SuppressWarnings("rawtypes")
      @Override
      public CommandPopulator getCommandPopulator()
      {
         return new CommandPopulator()
         {
            @Override
            public void populateObject(CommandLine line, InvocationProviders invocationProviders,
                     AeshContext aeshContext, boolean validate)
                              throws CommandLineParserException, OptionValidatorException
            {
               commandLineParser.getCommandPopulator().populateObject(line, invocationProviders, aeshContext,
                        validate);
            }

            @Override
            public Object getObject()
            {
               return commandLineParser.getCommandPopulator().getObject();
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
      public ProcessedCommand getProcessedCommand()
      {
         return commandLineParser.getProcessedCommand();
      }

      @Override
      public Command getCommand()
      {
         return commandLineParser.getCommand();
      }

      @Override
      public CommandLineParser getChildParser(String name)
      {
         return commandLineParser.getChildParser(name);
      }

      @Override
      public void addChildParser(CommandLineParser childParser)
      {
         commandLineParser.addChildParser(childParser);

      }

      @Override
      public List getAllChildParsers()
      {
         return commandLineParser.getAllChildParsers();
      }

      @Override
      public CommandLine parse(AeshLine line, boolean ignoreRequirements)
      {
         return commandLineParser.parse(line, ignoreRequirements);
      }

      @Override
      public void clear()
      {
         commandLineParser.clear();
      }

      @Override
      public boolean isGroupCommand()
      {
         return commandLineParser.isGroupCommand();
      }

      @Override
      public void setChild(boolean b)
      {
         commandLineParser.setChild(b);
      }

      @Override
      public CommandLine parse(List lines, boolean ignoreRequirements)
      {
         return commandLineParser.parse(lines, ignoreRequirements);
      }

      @Override
      public List getAllNames()
      {
         return commandLineParser.getAllNames();
      }

   }

}
