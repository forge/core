/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.Map;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.parser.CommandPopulator;
import org.jboss.aesh.cl.validator.OptionValidatorException;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * Implementation of the {@link CommandPopulator} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeCommandPopulator implements CommandPopulator
{
   private final Map<String, InputComponent<?, Object>> inputs;
   private final CommandLineUtil commandLineUtil;

   public ForgeCommandPopulator(CommandLineUtil commandLineUtil, Map<String, InputComponent<?, Object>> inputs)
   {
      this.commandLineUtil = commandLineUtil;
      this.inputs = inputs;
   }

   @Override
   public void populateObject(Object instance, CommandLine line) throws CommandLineParserException,
            OptionValidatorException
   {
      populateObject(instance, line, true);
   }

   @Override
   public void populateObject(Object instance, CommandLine line, boolean validate) throws CommandLineParserException,
            OptionValidatorException
   {
      commandLineUtil.populateUIInputs(line, inputs);
   }

}
