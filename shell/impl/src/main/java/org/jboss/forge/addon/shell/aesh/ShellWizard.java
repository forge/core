/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
      return populate(
               shellContext,
               completeLine,
               new HashMap<String, InputComponent<?, ?>>(),
               new HashMap<String, InputComponent<?, ?>>());
   }

   private CommandLineParser populate(ShellContext shellContext, String line,
            final Map<String, InputComponent<?, ?>> inputs, Map<String, InputComponent<?, ?>> lastUnpopulatedInputs
            ) throws Exception
   {
      inputs.putAll(getController().getInputs());

      CommandLineParser parser = commandLineUtil.generateParser(getController(), shellContext, inputs);
      CommandLine cmdLine = parser.parse(line, true);
      inputs.keySet().retainAll(commandLineUtil.populateUIInputs(cmdLine, inputs).keySet());

      Map<String, InputComponent<?, ?>> currentCommandPopulatedInputs = commandLineUtil.populateUIInputs(cmdLine,
               getController().getInputs());
      if (currentCommandPopulatedInputs.isEmpty() && !lastUnpopulatedInputs.isEmpty())
      {
         inputs.putAll(getUnpopulatedInputs(getController()));
         inputs.putAll(lastUnpopulatedInputs);
         parser = commandLineUtil.generateParser(getController(), shellContext, inputs);
         parser.parse(line, true);
      }

      if (getController().isValid())
      {
         if (getController().canMoveToNextStep())
         {
            Map<String, InputComponent<?, ?>> unpopulatedInputs = getUnpopulatedInputs(getController());

            getController().next().initialize();
            parser = populate(shellContext, line, inputs, unpopulatedInputs);
         }
      }
      return parser;
   }

   private Map<String, InputComponent<?, ?>> getUnpopulatedInputs(WizardCommandController controller)
   {
      Map<String, InputComponent<?, ?>> result = new HashMap<>();
      Map<String, InputComponent<?, ?>> inputs = controller.getInputs();
      for (Entry<String, InputComponent<?, ?>> entry : inputs.entrySet())
      {
         String name = entry.getKey();
         InputComponent<?, ?> input = entry.getValue();
         if (!input.hasValue())
         {
            result.put(name, input);
         }
      }
      return result;
   }
}
