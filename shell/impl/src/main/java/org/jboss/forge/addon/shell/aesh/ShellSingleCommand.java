/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.Map;

import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellValidationContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.Result;

/**
 * Encapsulates a {@link UICommand} to be useful in a Shell context
 * 
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ShellSingleCommand extends AbstractShellInteraction
{
   private final UICommand command;
   private Map<String, InputComponent<?, Object>> inputs;
   private CommandLineParser commandLineParser;

   /**
    * Creates a new {@link ShellSingleCommand} based on the shell and initial selection
    */
   public ShellSingleCommand(UICommand command, ShellContext shellContext, CommandLineUtil commandLineUtil)
   {
      super(command, shellContext, commandLineUtil);
      this.command = command;
   }

   @Override
   public CommandLineParser getParser(ShellContext shellContext, String completeLine) throws Exception
   {
      if (this.commandLineParser == null)
      {
         this.commandLineParser = commandLineUtil.generateParser(this.command, shellContext, getInputs());
      }
      return this.commandLineParser;
   }

   public UICommand getCommand()
   {
      return command;
   }

   @Override
   public Map<String, InputComponent<?, Object>> getInputs()
   {
      if (inputs == null)
      {
         inputs = buildInputs(command);
      }
      return inputs;
   }

   @Override
   public Result execute() throws Exception
   {
      return command.execute(getExecutionContext());
   }

   @Override
   public ShellValidationContext validate()
   {
      ShellValidationContext validationContext = new ShellValidationContext(getExecutionContext().getUIContext());
      for (InputComponent<?, Object> input : getInputs().values())
      {
         input.validate(validationContext);
      }
      command.validate(validationContext);

      return validationContext;
   }
}
