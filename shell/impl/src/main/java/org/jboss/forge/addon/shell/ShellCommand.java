/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.File;
import java.util.logging.Logger;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineCompletionParser;
import org.jboss.aesh.cl.ParsedCompleteObject;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.aesh.util.FileLister;
import org.jboss.forge.addon.shell.ForgeShell;
import org.jboss.forge.addon.shell.ShellContext;
import org.jboss.forge.addon.shell.util.CommandLineUtil;
import org.jboss.forge.addon.shell.util.UICommandDelegate;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ShellCommand implements Completion
{
   private static final Logger logger = Logger.getLogger(ShellCommand.class.getName());

   private AddonRegistry registry;
   private ForgeShell shell;
   private UICommand command;

   private ShellContext context;

   public ShellCommand(AddonRegistry registry, ForgeShell shell, UICommand command) throws Exception
   {
      this.registry = registry;
      this.command = new UICommandDelegate(command);
      this.context = new ShellContext(shell);
      this.shell = shell;
      command.initializeUI(context);
      generateParser(this.command);
   }

   public Console getConsole()
   {
      return shell.getConsole();
   }

   public ShellContext getContext()
   {
      return context;
   }

   public ForgeShell getForgeShell()
   {
      return shell;
   }

   public UICommand getCommand()
   {
      return command;
   }

   public void generateParser(UICommand command)
   {
      context.setParser(CommandLineUtil.generateParser(command, context));
   }

   public CommandLine parse(String line) throws CommandLineParserException
   {
      return context.getParser().parse(line);
   }

   public void run(ConsoleOutput consoleOutput, CommandLine commandLine) throws Exception
   {
      CommandLineUtil.populateUIInputs(commandLine, context, registry);
      context.setConsoleOutput(consoleOutput);
      Result result = command.execute(context);
      if (result != null &&
               result.getMessage() != null && result.getMessage().length() > 0)
         getConsole().pushToStdOut(result.getMessage() + Config.getLineSeparator());
   }

   public boolean isStandalone()
   {
      return context.isStandalone();
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public void complete(CompleteOperation completeOperation)
   {
      ParameterInt param = context.getParser().getParameters().get(0);
      // complete command names
      if (param.getName().startsWith(completeOperation.getBuffer()))
         completeOperation.addCompletionCandidate(param.getName());
      // display all the options/arguments
      else if (param.getName().equals(completeOperation.getBuffer().trim()))
      {
         completeOperation.addCompletionCandidates(param.getOptionLongNamesWithDash());
      }
      // complete options/arguments
      else if (completeOperation.getBuffer().startsWith(param.getName()))
      {
         ParsedCompleteObject completeObject = null;
         try
         {
            completeObject = new CommandLineCompletionParser(context.getParser())
                     .findCompleteObject(completeOperation.getBuffer());
         }
         catch (CommandLineParserException e)
         {
            logger.info(e.getMessage());
            return;
         }
         logger.info("ParsedCompleteObject: " + completeObject);
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
            InputComponent inputOption = context.findInput(completeObject.getName());
            // option type == File
            if (inputOption != null && inputOption.getValueType() == File.class)
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
            else if (inputOption != null && inputOption.getValueType() == String.class)
            {
               // if it has a default value we can try to auto complete that
               if (inputOption instanceof UIInput)
               {
                  if (completeObject.getValue() == null || ((((UIInput) inputOption).getValue() != null) &&
                           completeObject.getValue().startsWith(((UIInput) inputOption).getValue().toString())))
                  {
                     completeOperation.addCompletionCandidate(((UIInput) inputOption).getValue().toString());
                  }
               }
            }
            // this shouldnt be needed
            if (inputOption != null && inputOption instanceof UIInput)
            {
               Iterable<String> iter = ((UIInput) inputOption).getCompleter().getCompletionProposals(inputOption,
                        completeObject.getValue());
               if (iter != null)
               {
                  for (String s : iter)
                     completeOperation.addCompletionCandidate(s);
               }
               if (completeOperation.getCompletionCandidates().size() == 1)
               {
                  completeOperation.setOffset(completeOperation.getCursor() -
                           completeObject.getOffset());
               }
            }
         }
         // try to complete a argument value
         else if (completeObject.isArgument())
         {
            InputComponent inputOption = context.findInput("arguments"); // default for arguments

            if (inputOption != null && inputOption.getValueType() == File.class)
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
                  Iterable<String> iter = ((UIInputMany) inputOption).getCompleter().getCompletionProposals(
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
