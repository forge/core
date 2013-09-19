/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.List;
import java.util.Map;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.parser.CommandLineCompletionParser;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.cl.parser.ParsedCompleteObject;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellValidationContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.util.Strings;

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

   private CommandLineParser getParser()
   {
      if (this.commandLineParser == null)
      {
         this.commandLineParser = commandLineUtil.generateParser(this.command, getContext(), getInputs());
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
   public List<String> getCompletionOptions(String typed, String line)
   {
      List<String> result;
      if (Strings.isNullOrEmpty(typed))
      {
         result = getParser().getCommand().getOptionLongNamesWithDash();
      }
      else
      {
         result = getParser().getCommand().findPossibleLongNamesWitdDash(typed);
      }
      removeExistingOptions(line, result);
      return result;
   }

   @Override
   public ParsedCompleteObject parseCompleteObject(String line) throws CommandLineParserException
   {
      return new CommandLineCompletionParser(getParser()).findCompleteObject(line);
   }

   @Override
   public void populateInputs(String line, boolean lenient) throws CommandLineParserException
   {
      CommandLine commandLine = getParser().parse(line, lenient);
      this.commandLineUtil.populateUIInputs(commandLine, getInputs());
   }

   @Override
   public Result execute() throws Exception
   {
      return command.execute(getContext());
   }

   @Override
   public List<String> validate()
   {
      ShellValidationContext validationContext = new ShellValidationContext(getContext());
      for (InputComponent<?, Object> input : getInputs().values())
      {
         input.validate(validationContext);
      }
      command.validate(validationContext);

      return validationContext.getErrors();
   }
}
