/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.Map;

import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * Encapsulates a {@link UICommand} to be useful in a Shell context
 *
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ShellSingleCommand extends AbstractShellInteraction
{
   private final CommandController controller;
   private CommandLineParser<?> commandLineParser;

   /**
    * Creates a new {@link ShellSingleCommand} based on the shell and initial selection
    */
   public ShellSingleCommand(CommandController controller, ShellContext shellContext, CommandLineUtil commandLineUtil)
   {
      super(controller, shellContext, commandLineUtil);
      this.controller = controller;
   }

   @Override
   public CommandLineParser<?> getParser(ShellContext shellContext, String completeLine, CommandAdapter command)
            throws Exception
   {
      if (this.commandLineParser == null)
      {
         controller.initialize();
         this.commandLineParser = commandLineUtil.generateParser(command, this.controller, shellContext,
                  controller.getInputs());
      }
      return this.commandLineParser;
   }

   /**
    * Prompts for required missing values
    */
   @Override
   public boolean promptRequiredMissingValues(ShellImpl shell) throws InterruptedException
   {
      Map<String, InputComponent<?, ?>> inputs = getController().getInputs();
      if (hasMissingRequiredInputValues(inputs.values()))
      {
         UIOutput output = shell.getOutput();
         if (!getContext().isInteractive())
         {
            output.error(output.out(), NON_INTERACTIVE_MODE_MESSAGE);
            return false;
         }
         output.info(output.out(), INTERACTIVE_MODE_MESSAGE);
         promptRequiredMissingValues(shell, inputs.values());
      }
      return true;
   }

}
