/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh.completion;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.ParsedCompleteObject;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.util.FileLister;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.aesh.ShellCommand;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.HasCompleter;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.input.UIInputMany;

/**
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeOptionCompletion implements Completion
{
   private static final Logger logger = Logger.getLogger(ForgeOptionCompletion.class.getName());

   private ShellImpl shell;

   public ForgeOptionCompletion(ShellImpl shellImpl)
   {
      this.shell = shellImpl;
   }

   @Override
   public void complete(CompleteOperation completeOperation)
   {
      String line = completeOperation.getBuffer();
      ShellCommand cmd = shell.findCommand(line);
      if (cmd != null)
      {
         try
         {
            // We are dealing with one-level commands atm.
            // Eg. new-project-type --named ... instead of new-project-type setup --named ...
            ParameterInt param = cmd.getParameter();
            ParsedCompleteObject completeObject = cmd.parseCompleteObject(line);
            List<String> optionNames = param.getOptionLongNamesWithDash();

            try
            {
               CommandLine commandLine = cmd.parse(line);
               removeExistingOptions(commandLine, optionNames);
               cmd.populateInputs(commandLine);
            }
            catch (CommandLineParserException parseException)
            {
               // ignore for now
            }

            if (completeObject.doDisplayOptions())
            {
               // we have a partial/full name
               if (completeObject.getName() != null && !completeObject.getName().isEmpty())
               {
                  List<String> possibleOptions = param.findPossibleLongNamesWitdDash(completeObject.getName());
                  if (!possibleOptions.isEmpty())
                  {
                     // only one param
                     if (possibleOptions.size() == 1)
                     {
                        completeOperation.addCompletionCandidate(possibleOptions.get(0));
                        completeOperation.setOffset(completeOperation.getCursor() -
                                 completeObject.getOffset());
                     }
                     // multiple params
                     else
                     {
                        completeOperation.addCompletionCandidates(possibleOptions);
                     }
                  }
               }
               // display all our params
               else
               {
                  completeOperation.addCompletionCandidates(optionNames);
                  if (optionNames.size() == 1)
                  {
                     completeOperation.setOffset(completeOperation.getCursor() - completeObject.getOffset());
                  }
               }
            }
            // try to complete an options value "--xxx"
            else if (completeObject.isOption())
            {
               optionCompletion(completeOperation, completeObject, cmd);
            }
            // try to complete a argument value Eg: ls . (. is the argument)
            else if (completeObject.isArgument())
            {
               argumentCompletion(completeOperation, completeObject, cmd);
            }
         }
         catch (CommandLineParserException e)
         {
            logger.warning(e.getMessage());
            return;
         }
      }
   }

   private void removeExistingOptions(CommandLine commandLine, List<String> availableOptions)
   {
      Iterator<String> it = availableOptions.iterator();
      while (it.hasNext())
      {
         if (commandLine.hasOption(it.next()))
         {
            it.remove();
         }
      }
   }

   @SuppressWarnings({ "rawtypes" })
   private void defaultCompletion(CompleteOperation completeOperation, ShellCommand shellCommand)
            throws CommandLineParserException
   {
      // first see if it has an "arguments" option
      InputComponent inputOption = shellCommand.getInputs().get("arguments"); // default for arguments

      // use the arguments completor as default if it has any
      if (inputOption != null)
      {
         argumentCompletion(completeOperation, shellCommand.parseCompleteObject(completeOperation.getBuffer()),
                  shellCommand);
      }
      else
      {
         completeOperation.addCompletionCandidates(shellCommand.getParameter().getOptionLongNamesWithDash());
      }
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private void optionCompletion(CompleteOperation completeOperation, ParsedCompleteObject completeObject,
            ShellCommand shellCommand)
   {
      InputComponent inputOption = shellCommand.getInputs().get(completeObject.getName());
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
   private void argumentCompletion(CompleteOperation completeOperation, ParsedCompleteObject completeObject,
            ShellCommand shellCommand)
   {
      InputComponent inputOption = shellCommand.getInputs().get("arguments"); // default for arguments
//      InputType inputType = ((HintsFacet) inputOption.getFacet(HintsFacet.class)).getInputType();
      InputType inputType = InputType.FILE_PICKER;
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

      else if (inputType == InputType.FILE_PICKER)
      {
         completeOperation.setOffset(completeOperation.getCursor());
         if (completeObject.getValue() == null)
            new FileLister("", new File(System.getProperty("user.dir")))
                     .findMatchingDirectories(completeOperation);
         else
            new FileLister(completeObject.getValue(), new File(System.getProperty("user.dir")))
                     .findMatchingDirectories(completeOperation);
      }
      else if (inputType == InputType.CHECKBOX)
      {
         // TODO
      }
      // check if the command actually implements Completion
      else if (shellCommand.getCommand() instanceof Completion)
      {
         ((Completion) shellCommand.getCommand()).complete(completeOperation);
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
}
