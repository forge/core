/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.Map;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.cl.parser.CommandLineParserException;
import org.jboss.aesh.cl.populator.CommandPopulator;
import org.jboss.aesh.cl.validator.OptionValidatorException;
import org.jboss.aesh.console.AeshContext;
import org.jboss.aesh.console.InvocationProviders;
import org.jboss.aesh.console.command.Command;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * Implementation of the {@link CommandPopulator} interface
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeCommandPopulator<C extends Command<?>> implements CommandPopulator<Object, C>
{
   private final Map<String, InputComponent<?, ?>> inputs;
   private final CommandLineUtil commandLineUtil;
   private final ShellContext context;
   private CommandLineParser<?> commandLineParser;

   public ForgeCommandPopulator(CommandLineUtil commandLineUtil, Map<String, InputComponent<?, ?>> inputs,
            ShellContext shellContext)
   {
      this.commandLineUtil = commandLineUtil;
      this.inputs = inputs;
      this.context = shellContext;
   }

   @Override
   public void populateObject(CommandLine<C> line, InvocationProviders invocationProviders, AeshContext aeshContext,
            boolean validate) throws CommandLineParserException, OptionValidatorException
   {
      commandLineUtil.populateUIInputs(line, inputs, context);
      commandLineParser = line.getParser();
   }

   @Override
   public Object getObject()
   {
      if (commandLineParser != null)
      {
         return commandLineParser.getCommandPopulator().getObject();
      }
      return null;
   }
}