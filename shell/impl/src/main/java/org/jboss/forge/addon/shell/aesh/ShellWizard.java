/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.HashMap;
import java.util.Map;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.input.InputComponent;

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
   public CommandLineParser getParser(ShellContext shellContext, String completeLine) throws Exception
   {
      getController().initialize();
      return populate(shellContext, completeLine, new HashMap<String, InputComponent<?, ?>>(),
               new HashMap<String, InputComponent<?, ?>>());
   }

   private CommandLineParser populate(ShellContext shellContext, String line,
            final Map<String, InputComponent<?, ?>> allInputs, Map<String, InputComponent<?, ?>> lastPage)
            throws Exception
   {
      WizardCommandController controller = getController();
      Map<String, InputComponent<?, ?>> pageInputs = new HashMap<>(controller.getInputs());
      allInputs.putAll(pageInputs);
      CommandLineParser parser = commandLineUtil.generateParser(controller, shellContext, allInputs);
      CommandLine cmdLine = parser.parse(line, true);
      Map<String, InputComponent<?, ?>> populatedInputs = commandLineUtil.populateUIInputs(cmdLine, allInputs);
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
         parser = populate(shellContext, line, allInputs, pageInputs);
      }
      else if (inputsChanged)
      {
         parser = commandLineUtil.generateParser(controller, shellContext, allInputs);
      }
      return parser;
   }
}
