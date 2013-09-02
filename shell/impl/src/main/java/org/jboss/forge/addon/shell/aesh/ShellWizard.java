/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.parser.CommandLineCompletionParser;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.cl.parser.ParsedCompleteObject;
import org.jboss.forge.addon.shell.CommandManager;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellValidationContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.util.Strings;

/**
 * Encapsulates a group of {@link ShellSingleCommand} from a {@link UIWizard}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellWizard extends AbstractShellInteraction
{
   private LinkedList<ShellWizardStep> steps = new LinkedList<ShellWizardStep>();
   private LinkedList<Class<? extends UICommand>> subflows = new LinkedList<Class<? extends UICommand>>();
   private CommandManager commandManager;
   private CommandLineParser fullCommandLineParser;

   public ShellWizard(UIWizard root, ShellContext shellContext,
            CommandLineUtil commandLineUtil, CommandManager commandManager)
   {
      super(root, shellContext, commandLineUtil);
      this.commandManager = commandManager;
   }

   @Override
   public UIWizard getSourceCommand()
   {
      return (UIWizard) super.getSourceCommand();
   }

   @Override
   public Map<String, InputComponent<?, Object>> getInputs()
   {
      Map<String, InputComponent<?, Object>> inputs = new LinkedHashMap<String, InputComponent<?, Object>>();
      for (ShellWizardStep step : steps)
      {
         inputs.putAll(step.inputs);
      }
      return inputs;
   }

   @Override
   public void populateInputs(String line, boolean lenient) throws CommandLineParserException
   {
      UIWizard command = getSourceCommand();
      try
      {
         fullCommandLineParser = populate(command, command, line, true);
         if (!lenient)
         {
            // Validate required fields
            fullCommandLineParser.parse(line, false);
         }
      }
      catch (CommandLineParserException cpe)
      {
         throw cpe;
      }
      catch (Exception e)
      {
         throw new IllegalStateException(e);
      }
   }

   /**
    * Used for auto-completion of the options only
    */
   @Override
   public List<String> getCompletionOptions(String typed, String line)
   {
      List<String> result = new ArrayList<String>();
      boolean unvalued = Strings.isNullOrEmpty(typed);
      int size = steps.size();

      int idx;

      STEP_LOOP: for (idx = size - 1; idx > 0; idx--)
      {
         for (String option : steps.get(idx).inputs.keySet())
         {
            if (line.contains("--" + option))
            {
               break STEP_LOOP;
            }
         }
      }
      for (int i = idx; i < size; i++)
      {
         Map<String, InputComponent<?, Object>> inputMap = steps.get(i).inputs;
         for (Entry<String, InputComponent<?, Object>> entry : inputMap.entrySet())
         {
            String option = entry.getKey();
            String dashedOption = "--" + option;
            if ((unvalued || option.startsWith(typed)) && !line.contains(dashedOption))
            {
               // if (entry.getValue().isRequired())
               // {
               // dashedOption = new TerminalString(dashedOption, CharacterType.BOLD).toString();
               // }
               result.add(dashedOption);
            }
         }
      }
      Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
      return result;
   }

   @Override
   public ParsedCompleteObject parseCompleteObject(String line) throws CommandLineParserException
   {
      // Should populate current inputs to determine which fields will appear
      populateInputs(line, true);
      return new CommandLineCompletionParser(fullCommandLineParser).findCompleteObject(line);
   }

   private CommandLineParser populate(UICommand root, UICommand current, String line, boolean lenient) throws Exception
   {
      addWizardStep(current);
      Map<String, InputComponent<?, Object>> inputs = getInputs();
      CommandLineParser parser = commandLineUtil.generateParser(root, inputs);
      CommandLine cmdLine = parser.parse(line, lenient);
      commandLineUtil.populateUIInputs(cmdLine, inputs);
      List<String> errors = validate();
      if (errors.isEmpty())
      {
         if (current instanceof UIWizard)
         {
            NavigationResult next = ((UIWizard) current).next(getContext());
            final Class<? extends UICommand> successor;
            // Proceed to next input
            if (next != null && next.getNext() != null)
            {
               Class<? extends UICommand>[] successors = next.getNext();
               successor = successors[0];
               for (int i = 1; i < successors.length; i++)
               {
                  if (successors[i] != null)
                  {
                     subflows.push(successors[i]);
                  }
               }
            }
            else if (!subflows.isEmpty())
            {
               successor = subflows.pop();
            }
            else
            {
               successor = null;
            }
            if (successor != null)
            {
               UICommand step = commandManager.lookup(successor);
               parser = populate(root, step, line, lenient);
            }
         }
      }
      return parser;
   }

   private ShellWizardStep addWizardStep(final UICommand step)
   {
      ShellWizardStep cmdStep = new ShellWizardStep(step, buildInputs(step));
      steps.add(cmdStep);
      return cmdStep;
   }

   @Override
   public Result execute() throws Exception
   {
      Result result = null;
      for (ShellWizardStep step : steps)
      {
         result = step.command.execute(getContext());
      }
      return result;
   }

   @Override
   public List<String> validate()
   {
      ShellValidationContext validationContext = new ShellValidationContext(getContext());
      for (ShellWizardStep step : steps)
      {
         for (InputComponent<?, Object> input : step.inputs.values())
         {
            input.validate(validationContext);
         }
         step.command.validate(validationContext);
      }
      return validationContext.getErrors();
   }

   private static class ShellWizardStep
   {
      public final UICommand command;
      public final Map<String, InputComponent<?, Object>> inputs;

      public ShellWizardStep(UICommand command, Map<String, InputComponent<?, Object>> inputs)
      {
         this.command = command;
         this.inputs = inputs;
      }

   }
}
