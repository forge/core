/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.forge.addon.shell.CommandManager;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellValidationContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;

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
   public CommandLineParser getParser(ShellContext shellContext, String completeLine) throws Exception
   {
      UIWizard command = getSourceCommand();
      return populate(command, command, shellContext, completeLine);
   }

   private CommandLineParser populate(UICommand root, UICommand current, ShellContext shellContext, String line)
            throws Exception
   {
      addWizardStep(current);
      Map<String, InputComponent<?, Object>> inputs = getInputs();
      CommandLineParser parser = commandLineUtil.generateParser(root, shellContext, inputs);
      CommandLine cmdLine = parser.parse(line, true);
      Map<String, InputComponent<?, Object>> populatedInputs = commandLineUtil.populateUIInputs(cmdLine, inputs);
      ShellValidationContext validationContext = validate();
      List<String> errors = validationContext.getErrors();
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
               // Retain only the populated values
               inputs.keySet().retainAll(populatedInputs.keySet());
               parser = populate(root, step, shellContext, line);
            }
         }
      }
      return parser;
   }

   private ShellWizardStep addWizardStep(final UICommand step)
   {
      Map<String, InputComponent<?, Object>> inputs = buildInputs(step);
      ShellWizardStep cmdStep = new ShellWizardStep(step, inputs);
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
   public ShellValidationContext validate()
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
      return validationContext;
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
