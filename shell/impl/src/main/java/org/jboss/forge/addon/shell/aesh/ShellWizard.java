/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.Map;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.UICommand;
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
   public WizardCommandController getSourceCommand()
   {
      return (WizardCommandController) super.getSourceCommand();
   }

   @Override
   public CommandLineParser getParser(ShellContext shellContext, String completeLine) throws Exception
   {
      UICommand command = getSourceCommand().getCommand();
      return populate(command, shellContext, completeLine);
   }

   private CommandLineParser populate(UICommand command, ShellContext shellContext, String line)
            throws Exception
   {
      Map<String, InputComponent<?, Object>> inputs = getInputs();
      CommandLineParser parser = commandLineUtil.generateParser(getSourceCommand().getCommand(), shellContext, inputs);
      CommandLine cmdLine = parser.parse(line, true);
      Map<String, InputComponent<?, Object>> populatedInputs = commandLineUtil.populateUIInputs(cmdLine, inputs);
      if (getSourceCommand().isValid())
      {
         if (getSourceCommand().canMoveToNextStep())
         {
            getSourceCommand().next();
            inputs.keySet().retainAll(populatedInputs.keySet());
            parser = populate(command, shellContext, line);
         }
      }
      return parser;
   }
}
