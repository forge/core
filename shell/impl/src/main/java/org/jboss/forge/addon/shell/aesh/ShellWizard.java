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
      return populate(shellContext, completeLine, new HashMap<String, InputComponent<?, ?>>());
   }

   private CommandLineParser populate(ShellContext shellContext, String line,
            final Map<String, InputComponent<?, ?>> inputs)
            throws Exception
   {
      inputs.putAll(getController().getInputs());
      CommandLineParser parser = commandLineUtil.generateParser(getController(), shellContext, inputs);
      CommandLine cmdLine = parser.parse(line, true);
      Map<String, InputComponent<?, ?>> populatedInputs = commandLineUtil.populateUIInputs(cmdLine, inputs);
      if (getController().isValid())
      {
         if (getController().canMoveToNextStep())
         {
            getController().next().initialize();
            inputs.keySet().retainAll(populatedInputs.keySet());
            parser = populate(shellContext, line, inputs);
         }
      }
      return parser;
   }
}
