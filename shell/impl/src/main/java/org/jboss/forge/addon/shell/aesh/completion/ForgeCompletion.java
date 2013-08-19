/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh.completion;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.aesh.cl.ParsedCompleteObject;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.util.FileLister;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.aesh.ShellCommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.HasCompleter;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.util.InputComponents;

/**
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeCompletion implements Completion
{
   private static final Logger logger = Logger.getLogger(ForgeCompletion.class.getName());

   /**
    * the name of the arguments {@link InputComponent} (if exists)
    */
   private static final String ARGUMENTS_INPUT_NAME = "arguments";

   private ShellImpl shell;

   public ForgeCompletion(ShellImpl shellImpl)
   {
      this.shell = shellImpl;
   }

   @Override
   public void complete(CompleteOperation completeOperation)
   {
      String line = completeOperation.getBuffer();
      ShellContext shellContext = shell.newShellContext();
      final ShellCommand cmd = shell.findCommand(shellContext, line);
      if (cmd == null)
      {
         Collection<ShellCommand> commands = shell.findMatchingCommands(shellContext, line);
         for (ShellCommand command : commands)
         {
            completeOperation.addCompletionCandidate(command.getName());
         }
      }
      else
      {
         try
         {
            // We are dealing with one-level commands only.
            // Eg. new-project-type --named ... instead of new-project-type setup --named ...
            ParameterInt param = cmd.getParameter();
            ParsedCompleteObject completeObject = cmd.parseCompleteObject(line);
            if (completeObject.doDisplayOptions())
            {
               // we have a partial/full name
               if (completeObject.getName() != null && !completeObject.getName().isEmpty())
               {
                  List<String> possibleOptions = param.findPossibleLongNamesWitdDash(completeObject.getName());
                  completeOperation.addCompletionCandidates(possibleOptions);
               }
               else
               {
                  List<String> optionNames = param.getOptionLongNamesWithDash();
                  removeExistingOptions(line, optionNames);
                  // All the not-informed parameters
                  completeOperation.addCompletionCandidates(optionNames);
               }
            }
            else
            {
               final InputComponent<?, Object> input;
               if (completeObject.isOption())
               {
                  // try to complete an option value. Eg: "--xxx"
                  input = cmd.getInputs().get(completeObject.getName());
               }
               // try to complete a argument value Eg: ls . (. is the argument)
               else if (completeObject.isArgument())
               {
                  input = cmd.getInputs().get(ARGUMENTS_INPUT_NAME); // default for arguments
               }
               else
               {
                  input = null;
               }
               if (input != null)
               {
                  CompletionStrategy completionObj = CompletionStrategyFactory.getCompletionFor(input);
                  completionObj.complete(completeOperation, input, shellContext, completeObject.getValue());
               }
            }
            if (completeOperation.getCompletionCandidates().size() == 1)
            {
               completeOperation.setOffset(completeOperation.getCursor() - completeObject.getOffset());
            }
         }
         catch (Exception e)
         {
            logger.warning(e.getMessage());
            return;
         }
      }
   }

   private void removeExistingOptions(String commandLine, Iterable<String> availableOptions)
   {
      Iterator<String> it = availableOptions.iterator();
      while (it.hasNext())
      {
         if (commandLine.contains(it.next()))
         {
            it.remove();
         }
      }
   }

   // try to complete an option value. Eg: "--xxx"
   @SuppressWarnings({ "rawtypes", "unchecked" })
   private void optionCompletion(ShellContext shellContext, CompleteOperation completeOperation,
            ParsedCompleteObject completeObject,
            ShellCommand shellCommand)
   {
      InputComponent inputOption = shellCommand.getInputs().get(completeObject.getName());
      Object value = InputComponents.getValueFor(inputOption);
      InputType inputType = InputComponents.getInputType(inputOption);
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
}
