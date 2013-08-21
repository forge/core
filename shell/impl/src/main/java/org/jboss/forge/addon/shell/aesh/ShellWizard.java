/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineCompletionParser;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.ParsedCompleteObject;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.forge.addon.shell.CommandManager;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellValidationContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.util.Strings;

/**
 * Encapsulates a group of {@link ShellSingleCommand} from a {@link UIWizard}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellWizard extends AbstractShellInteraction
{

   private LinkedList<UICommand> steps = new LinkedList<UICommand>();
   private Map<String, InputComponent<?, Object>> inputs = new HashMap<String, InputComponent<?, Object>>();
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
            fullCommandLineParser.parse(line, false, false);
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

   @Override
   public ParameterInt getParameter()
   {
      return fullCommandLineParser.getParameter();
   }

   @Override
   public ParsedCompleteObject parseCompleteObject(String line) throws CommandLineParserException
   {
      // Should populate current inputs to determine which fields will appear
      populateInputs(line, true);
      return new CommandLineCompletionParser(fullCommandLineParser).findCompleteObject(line);
   }

   @SuppressWarnings("unchecked")
   private CommandLineParser populate(UIWizard root, UIWizard current, String line, boolean lenient) throws Exception
   {
      addWizardStep(current);
      CommandLineParser parser = commandLineUtil.generateParser(root, inputs);
      CommandLine cmdLine = parser.parse(line, lenient, lenient);
      commandLineUtil.populateUIInputs(cmdLine, inputs);
      ShellValidationContext validationContext = new ShellValidationContext(getContext());
      List<String> errors = validationContext.getErrors();

      for (InputComponent<?, Object> input : inputs.values())
      {
         String requiredMsg = InputComponents.validateRequired(input);
         if (!Strings.isNullOrEmpty(requiredMsg))
         {
            errors.add(requiredMsg);
         }
      }
      if (errors.isEmpty())
      {
         current.validate(validationContext);
      }
      if (errors.isEmpty())
      {
         NavigationResult next = current.next(getContext());
         // Proceed to next input
         if (next != null && next.getNext() != null)
         {
            // It should always be a UIWizardStep
            Class<? extends UIWizardStep> nextWizardStep = (Class<? extends UIWizardStep>) next.getNext();
            UIWizardStep step = commandManager.lookup(nextWizardStep);
            parser = populate(root, step, line, lenient);
         }
      }
      return parser;
   }

   private void addWizardStep(final UIWizard step)
   {
      Map<String, InputComponent<?, Object>> stepInputs = buildInputs(step);
      inputs.putAll(stepInputs);
      steps.add(step);
   }

   @Override
   public Result execute() throws Exception
   {
      Result result = null;
      for (UICommand cmd : steps)
      {
         result = cmd.execute(getContext());
      }
      return result;
   }
}
