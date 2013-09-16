/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh.completion;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.aesh.cl.exception.ArgumentParserException;
import org.jboss.aesh.cl.parser.ParsedCompleteObject;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.parser.Parser;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.aesh.AbstractShellInteraction;
import org.jboss.forge.addon.shell.ui.ShellContextImpl;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeCompletion implements Completion
{
   private static final Logger logger = Logger.getLogger(ForgeCompletion.class.getName());

   /**
    * the name of the arguments {@link InputComponent} (if exists)
    */
   public static final String ARGUMENTS_INPUT_NAME = "arguments";

   private ShellImpl shell;

   public ForgeCompletion(ShellImpl shellImpl)
   {
      this.shell = shellImpl;
   }

   @Override
   public void complete(CompleteOperation completeOperation)
   {
      String line = completeOperation.getBuffer();
      // TODO: ConsoleOperation is not set
      ShellContextImpl context = null;
      try
      {
         context = shell.newShellContext(null);
         final AbstractShellInteraction cmd = shell.findCommand(context, line);
         if (cmd == null)
         {
            Collection<AbstractShellInteraction> commands = shell.findMatchingCommands(context, line);
            for (AbstractShellInteraction command : commands)
            {
               completeOperation.addCompletionCandidate(command.getName());
            }
         }
         else if (line.equals(cmd.getName()))
         {
            completeOperation.addCompletionCandidate(" ");
         }
         else
         {
            try
            {
               // We are dealing with one-level commands only.
               // Eg. new-project-type --named ... instead of new-project-type setup --named ...
               // cmd.populateInputs(line, true);
               ParsedCompleteObject completeObject = cmd.parseCompleteObject(line);

               // completing an option name
               if (completeObject.doDisplayOptions())
               {
                  // display all possible options names
                  List<String> options = cmd.getCompletionOptions(completeObject.getName(), line);
                  completeOperation.addCompletionCandidates(options);
                  completeOperation.setOffset(completeOperation.getCursor() - completeObject.getOffset());
               }
               // completing an option value or argument
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
                  String typedValue = completeObject.getValue();
                  if (typedValue == null)
                  {
                     typedValue = "";
                  }
                  if (input != null)
                  {
                     ConverterFactory converterFactory = shell.getConverterFactory();
                     CompletionStrategy completionObj = CompletionStrategyFactory.getCompletionFor(input);
                     completionObj.complete(completeOperation, input, context, typedValue, converterFactory);
                  }
                  // if we only have one complete candidate, leave the escaped space be
                  List<String> candidates = completeOperation.getCompletionCandidates();
                  completeOperation.addCompletionCandidates(candidates);
                  completeOperation.setOffset(completeOperation.getCursor() - completeObject.getOffset());
                  if (candidates.size() > 1)
                  {
                     completeOperation.removeEscapedSpacesFromCompletionCandidates();
                  }
                  else if (candidates.size() == 1)
                  {
                     if (completeObject.getValue().contains(" "))
                     {
                        completeOperation.setOffset(completeOperation.getCursor()
                                 -
                                 (completeObject.getValue().length() + Parser.findNumberOfSpacesInWord(completeObject
                                          .getValue())));
                     }
                     else
                        completeOperation.setOffset(completeOperation.getCursor() - completeObject.getValue().length());
                  }
               }
            }
            catch (ArgumentParserException e)
            {
               if (!cmd.getInputs().isEmpty())
               {
                  completeOperation.doAppendSeparator(false);
                  completeOperation.addCompletionCandidate(line + "--");
               }
            }
            catch (Exception e)
            {
               logger.log(Level.WARNING, "Failed to complete.", e);
               return;
            }
         }
      }
      finally
      {
         if (context != null)
         {
            context.destroy();
         }
      }
   }
}
