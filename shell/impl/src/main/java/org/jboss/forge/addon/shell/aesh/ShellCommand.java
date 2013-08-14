/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.inject.Vetoed;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineCompletionParser;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.ParsedCompleteObject;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.aesh.util.FileLister;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.shell.ui.ShellUIBuilderImpl;
import org.jboss.forge.addon.shell.ui.UICommandDelegate;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.HasCompleter;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.result.Result;

/**
 * Encapsulates a {@link UICommand} to be useful in a Shell context
 * 
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Vetoed
public class ShellCommand implements Completion
{
   private static final Logger logger = Logger.getLogger(ShellCommand.class.getName());

   private final UICommand command;
   private final ShellContextImpl context;
   private final CommandLineParser commandLineParser;
   private Map<String, InputComponent<?, Object>> inputs;
   private CommandLineUtil commandLineUtil;

   /**
    * Creates a new {@link ShellCommand} based on the shell and initial selection
    * 
    * @param command
    * @param shell
    * @param selection
    * @throws Exception
    */
   public ShellCommand(UICommand command, Shell shell, UISelection<?> selection, CommandLineUtil commandLineUtil)
            throws Exception
   {
      this.command = new UICommandDelegate(command);
      this.context = new ShellContextImpl(shell, selection);

      // Initialize UICommand
      ShellUIBuilderImpl builder = new ShellUIBuilderImpl(this.context);
      command.initializeUI(builder);
      this.inputs = builder.getComponentMap();

      this.commandLineParser = commandLineUtil.generateParser(this.command, inputs);
   }

   public Result run(ConsoleOutput consoleOutput, CommandLine commandLine) throws Exception
   {
      commandLineUtil.populateUIInputs(commandLine, inputs);
      context.setConsoleOutput(consoleOutput);
      Result result = command.execute(context);
      if (result != null &&
               result.getMessage() != null && result.getMessage().length() > 0)
         context.getProvider().getConsole().pushToStdOut(result.getMessage() + Config.getLineSeparator());
      return result;
   }

   @Override
   public void complete(CompleteOperation completeOperation)
   {
      try
      {
         ParameterInt param = commandLineParser.getParameters().get(0);
         // complete command names
         if (param.getName().startsWith(completeOperation.getBuffer()))
            completeOperation.addCompletionCandidate(param.getName());
         // display all the options/arguments
         else if (param.getName().equals(completeOperation.getBuffer().trim()))
         {
            defaultCompletion(completeOperation);
         }
         // complete options/arguments
         else if (completeOperation.getBuffer().startsWith(param.getName()))
         {
            ParsedCompleteObject completeObject = null;
            completeObject = new CommandLineCompletionParser(commandLineParser)
                     .findCompleteObject(completeOperation.getBuffer());
            // logger.info("ParsedCompleteObject: " + completeObject);
            if (completeObject.doDisplayOptions())
            {
               // we have a partial/full name
               if (completeObject.getName() != null && completeObject.getName().length() > 0)
               {
                  if (param.findPossibleLongNamesWitdDash(completeObject.getName()).size() > 0)
                  {
                     // only one param
                     if (param.findPossibleLongNamesWitdDash(completeObject.getName()).size() == 1)
                     {
                        completeOperation.addCompletionCandidate(param.findPossibleLongNamesWitdDash(
                                 completeObject.getName()).get(0));
                        completeOperation.setOffset(completeOperation.getCursor() -
                                 completeObject.getOffset());
                     }
                     // multiple params
                     else
                        completeOperation.addCompletionCandidates(param.findPossibleLongNamesWitdDash(completeObject
                                 .getName()));
                  }
               }
               // display all our params
               else
               {
                  if (param.getOptionLongNamesWithDash().size() > 1)
                  {
                     completeOperation.addCompletionCandidates(param.getOptionLongNamesWithDash());
                  }
                  else
                  {
                     completeOperation.addCompletionCandidates(param.getOptionLongNamesWithDash());
                     completeOperation.setOffset(completeOperation.getCursor() -
                              completeObject.getOffset());
                  }
               }
            }
            // try to complete an options value
            else if (completeObject.isOption())
            {
               optionCompletion(completeOperation, completeObject);
            }
            // try to complete a argument value
            else if (completeObject.isArgument())
            {
               argumentCompletion(completeOperation, completeObject);
            }
         }
      }
      catch (CommandLineParserException e)
      {
         logger.warning(e.getMessage());
         return;
      }
   }

   @SuppressWarnings({ "rawtypes" })
   private void defaultCompletion(CompleteOperation completeOperation) throws CommandLineParserException
   {
      // first see if it has an "arguments" option
      InputComponent inputOption = inputs.get("arguments"); // default for arguments

      // use the arguments completor as default if it has any
      if (inputOption != null)
      {
         argumentCompletion(completeOperation, new CommandLineCompletionParser(commandLineParser)
                  .findCompleteObject(completeOperation.getBuffer()));
      }
      else
      {
         completeOperation.addCompletionCandidates(commandLineParser.getParameters().get(0)
                  .getOptionLongNamesWithDash());
      }
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private void optionCompletion(CompleteOperation completeOperation, ParsedCompleteObject completeObject)
   {
      InputComponent inputOption = inputs.get(completeObject.getName());
      // atm the FileLister requires the CompleteOperation object so it need
      // to be handled here and not for each inputcomponents.setCompleter
      if (inputOption != null &&
               ((inputOption.getValueType() == File.class) || (inputOption.getValueType() == FileResource.class)))
      {
         completeOperation.setOffset(completeOperation.getCursor());
         if (completeObject.getValue() == null)
         {
            // use default value if its set
            if (inputOption.getValueType() == SingleValued.class &&
                     ((SingleValued) inputOption).getValue() != null)
            {
               new FileLister("", new File(((SingleValued) inputOption).getValue().toString()))
                        .findMatchingDirectories(completeOperation);
            }
            else
               new FileLister("", new File(System.getProperty("user.dir")))
                        .findMatchingDirectories(completeOperation);
         }
         else
            new FileLister(completeObject.getValue(), new File(System.getProperty("user.dir")))
                     .findMatchingDirectories(completeOperation);
      }
      else if (inputOption != null && inputOption.getValueType() == DirectoryResource.class)
      {
         completeOperation.setOffset(completeOperation.getCursor());
         if (completeObject.getValue() == null)
         {

            if (((SingleValued) inputOption).getValue() != null)
            {
               new FileLister("", new File(((SingleValued) inputOption).getValue().toString()),
                        FileLister.Filter.DIRECTORY).findMatchingDirectories(completeOperation);
            }
            else
               new FileLister("", new File(System.getProperty("user.dir")), FileLister.Filter.DIRECTORY)
                        .findMatchingDirectories(completeOperation);
         }
         else
            new FileLister(completeObject.getValue(), new File(System.getProperty("user.dir")),
                     FileLister.Filter.DIRECTORY).findMatchingDirectories(completeOperation);
      }

      if (inputOption != null && (inputOption instanceof SingleValued &&
               ((SingleValued) inputOption).getValue() != null))
      {
         // need to check if the default matches the complete value
         if (completeObject.getValue().length() == 0)
         {
            completeOperation.addCompletionCandidate(((SingleValued) inputOption).getValue().toString());
            return;
         }
         else
         {
            String defaultValue = ((SingleValued) inputOption).getValue().toString();
            if (defaultValue.startsWith(completeObject.getValue()))
            {
               completeOperation.addCompletionCandidate(defaultValue.substring(completeObject.getValue().length()));
               return;
            }
         }
      }
      if (inputOption != null && inputOption instanceof SelectComponent)
      {
         if (completeObject.getValue() == null || completeObject.getValue().length() == 0)
         {
            for (Object o : ((SelectComponent) inputOption).getValueChoices())
            {
               completeOperation.addCompletionCandidate(o.toString());
            }
         }
         else
         {
            for (Object o : ((SelectComponent) inputOption).getValueChoices())
            {
               if (o.toString().startsWith(completeObject.getValue()))
                  completeOperation.addCompletionCandidate(o.toString());
            }
         }
      }
      if (inputOption != null
               && (inputOption instanceof HasCompleter && ((HasCompleter) inputOption).getCompleter() != null))
      {
         Iterable iter = ((HasCompleter) inputOption).getCompleter().getCompletionProposals(null, inputOption,
                  completeObject.getValue());
         if (iter != null)
         {
            for (Object s : iter)
               completeOperation.addCompletionCandidate(s.toString());
         }
         if (completeOperation.getCompletionCandidates().size() == 1)
         {
            completeOperation.setOffset(completeOperation.getCursor() - completeObject.getOffset());
         }
      }

   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private void argumentCompletion(CompleteOperation completeOperation, ParsedCompleteObject completeObject)
   {
      InputComponent inputOption = inputs.get("arguments"); // default for arguments

      // use the arguments completor as default if it has any
      if (inputOption != null
               && (inputOption instanceof HasCompleter && ((HasCompleter) inputOption).getCompleter() != null))
      {
         if (completeObject.getValue() != null)
            completeOperation.setOffset(completeOperation.getCursor() - completeObject.getValue().length());
         for (Object o : ((HasCompleter) inputOption).getCompleter().getCompletionProposals(null, inputOption,
                  completeObject.getValue()))
         {
            completeOperation.addCompletionCandidate(o.toString());
         }
      }

      else if (inputOption != null && inputOption.getValueType() == File.class)
      {
         completeOperation.setOffset(completeOperation.getCursor());
         if (completeObject.getValue() == null)
            new FileLister("", new File(System.getProperty("user.dir")))
                     .findMatchingDirectories(completeOperation);
         else
            new FileLister(completeObject.getValue(), new File(System.getProperty("user.dir")))
                     .findMatchingDirectories(completeOperation);
      }
      else if (inputOption != null && inputOption.getValueType() == Boolean.class)
      {
         // TODO
      }
      // check if the command actually implements Completion
      else if (command instanceof Completion)
      {
         ((Completion) command).complete(completeOperation);
      }
      else
      {
         // this shouldnt be needed
         if (inputOption != null && inputOption instanceof UIInputMany)
         {
            Iterable<String> iter = ((UIInputMany) inputOption).getCompleter().getCompletionProposals(null,
                     inputOption, completeObject.getValue());
            if (iter != null)
            {
               for (String s : iter)
               {
                  completeOperation.addCompletionCandidate(s);
               }
            }
            if (completeOperation.getCompletionCandidates().size() == 1)
            {
               completeOperation.setOffset(completeOperation.getCursor() -
                        completeObject.getOffset());
            }
         }
      }
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;
      if (!(o instanceof ShellCommand))
         return false;

      ShellCommand that = (ShellCommand) o;

      if (!command.getMetadata().getName().equals(that.command.getMetadata().getName()))
         return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return command.getMetadata().getName().hashCode();
   }

   @Override
   public String toString()
   {
      return command.getMetadata().getName();
   }
}
