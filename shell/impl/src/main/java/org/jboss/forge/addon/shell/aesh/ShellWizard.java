/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * Encapsulates the {@link WizardCommandController}.
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ShellWizard extends AbstractShellInteraction
{

   public ShellWizard(WizardCommandController wizardCommandController, ShellContext shellContext,
            CommandLineUtil commandLineUtil, ForgeCommandRegistry forgeCommandRegistry)
   {
      super(wizardCommandController, shellContext, commandLineUtil);
   }

   @Override
   public WizardCommandController getController()
   {
      return (WizardCommandController) super.getController();
   }

   @Override
   public CommandLineParser<?> getParser(ShellContext shellContext, String completeLine, CommandAdapter commandAdapter)
            throws Exception
   {
      getController().initialize();
      return populate(commandAdapter, shellContext, completeLine, new LinkedHashMap<String, InputComponent<?, ?>>(),
               new LinkedHashMap<String, InputComponent<?, ?>>());
   }

   private CommandLineParser<?> populate(CommandAdapter commandAdapter, ShellContext shellContext, String line,
            final Map<String, InputComponent<?, ?>> allInputs, Map<String, InputComponent<?, ?>> lastPage)
                     throws Exception
   {
      WizardCommandController controller = getController();
      Map<String, InputComponent<?, ?>> pageInputs = new LinkedHashMap<>(controller.getInputs());
      allInputs.putAll(pageInputs);
      CommandLineParser<?> parser = commandLineUtil.generateParser(commandAdapter, controller, shellContext, allInputs);
      CommandLine<?> cmdLine = parser.parse(line, true);
      Map<String, InputComponent<?, ?>> populatedInputs = commandLineUtil.populateUIInputs(cmdLine, allInputs,
               shellContext);

      // Second pass to ensure disabled fields are set
      parser = commandLineUtil.generateParser(commandAdapter, controller, shellContext, allInputs);
      cmdLine = parser.parse(line, true);
      populatedInputs = commandLineUtil.populateUIInputs(cmdLine, allInputs, shellContext);

      boolean inputsChanged = false;
      for (String input : pageInputs.keySet())
      {
         // TODO: May not work correctly with Subflows
         if (populatedInputs.containsKey(input))
         {
            // Trim inputs from last page, because information from the current page was provided
            lastPage.keySet().removeAll(populatedInputs.keySet());
            allInputs.keySet().removeAll(lastPage.keySet());
            inputsChanged = true;
            break;
         }
      }

      if (controller.canMoveToNextStep())
      {
         controller.next().initialize();
         parser = populate(commandAdapter, shellContext, line, allInputs, pageInputs);
      }
      else if (inputsChanged)
      {
         parser = commandLineUtil.generateParser(commandAdapter, controller, shellContext, allInputs);
      }
      return parser;
   }

   @Override
   public boolean promptRequiredMissingValues(ShellImpl shell) throws InterruptedException
   {
      WizardCommandController controller = getController();
      boolean interactiveModeEnabled = false;
      UIOutput output = shell.getOutput();
      if (hasMissingRequiredInputValues(controller.getInputs().values()))
      {
         if (!getContext().isInteractive())
         {
            output.error(output.err(), NON_INTERACTIVE_MODE_MESSAGE);
            return false;
         }
         output.info(output.out(), INTERACTIVE_MODE_MESSAGE);
         interactiveModeEnabled = true;
         promptRequiredMissingValues(shell, controller.getInputs().values());
      }
      while (controller.canMoveToNextStep())
      {
         try
         {
            controller.next().initialize();
         }
         catch (Exception e)
         {
            // TODO: Log this
            break;
         }
         if (hasMissingRequiredInputValues(controller.getInputs().values()))
         {
            if (!interactiveModeEnabled)
            {
               if (!getContext().isInteractive())
               {
                  output.error(output.out(), NON_INTERACTIVE_MODE_MESSAGE);
                  return false;
               }
               output.info(output.out(), INTERACTIVE_MODE_MESSAGE);
               interactiveModeEnabled = true;
            }
            promptRequiredMissingValues(shell, controller.getInputs().values());
         }
      }
      return true;
   }
}
