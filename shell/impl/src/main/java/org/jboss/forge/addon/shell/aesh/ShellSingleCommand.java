/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.Map;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineCompletionParser;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.ParsedCompleteObject;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.forge.addon.shell.ui.ShellContext;
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
public class ShellSingleCommand extends AbstractShellCommand
{
   private final UICommand command;
   private final CommandLineParser commandLineParser;
   private final Map<String, InputComponent<?, Object>> inputs;

   /**
    * Creates a new {@link ShellSingleCommand} based on the shell and initial selection
    */
   public ShellSingleCommand(UICommand command, ShellContext shellContext, CommandLineUtil commandLineUtil)
   {
      super(command, shellContext, commandLineUtil);
      this.command = command;
      this.inputs = buildInputs(command);
      this.commandLineParser = commandLineUtil.generateParser(this.command, inputs);
   }

   public UICommand getCommand()
   {
      return command;
   }

   @Override
   public Map<String, InputComponent<?, Object>> getInputs()
   {
      return inputs;
   }

   @Override
   public ParameterInt getParameter()
   {
      return commandLineParser.getParameter();
   }

   @Override
   public ParsedCompleteObject parseCompleteObject(String line) throws CommandLineParserException
   {
      return new CommandLineCompletionParser(commandLineParser).findCompleteObject(line);
   }

   @Override
   public void populateInputs(String line, boolean lenient) throws CommandLineParserException
   {
      CommandLine commandLine = this.commandLineParser.parse(line, false, lenient);
      this.commandLineUtil.populateUIInputs(commandLine, inputs);
   }

   @Override
   public Result execute() throws Exception
   {
      return command.execute(getContext());
   }
}
