/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.util;

import java.util.logging.Logger;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.OptionBuilder;
import org.jboss.aesh.cl.ParserBuilder;
import org.jboss.aesh.cl.exception.OptionParserException;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.forge.aesh.ShellContext;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.convert.ConverterFactory;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.input.InputComponent;
import org.jboss.forge.ui.input.UIInputMany;
import org.jboss.forge.ui.util.InputComponents;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class CommandLineUtil
{
   private static final Logger logger = Logger.getLogger(CommandLineUtil.class.getName());

   private static ConverterFactory converterFactory = null;

   public static CommandLineParser generateParser(UICommand command, ShellContext context)
   {
      ParserBuilder builder = new ParserBuilder();

      ParameterInt parameter =
               new ParameterInt(command.getMetadata().getName(), command.getMetadata().getDescription());
      for (InputComponent<?, ?> input : context.getInputs())
      {
         if (!input.getName().equals("arguments"))
         {
            try
            {
               if (input.getValueType() == Boolean.class)
               {
                  parameter.addOption(
                           new OptionBuilder()
                                    .longName(input.getName())
                                    .hasValue(false)
                                    .description(input.getLabel())
                                    .create());
               }
               else
               {
                  parameter.addOption(
                           new OptionBuilder().longName(input.getName())
                                    .description(input.getLabel())
                                    .required(input.isRequired())
                                    .create());
               }
            }
            catch (OptionParserException e)
            {
               // ignored for now
            }
         }
      }
      builder.addParameter(parameter);
      return builder.generateParser();
   }

   public static void populateUIInputs(CommandLine commandLine,
            ShellContext context, AddonRegistry registry)
   {
      for (InputComponent<?, Object> input : context.getInputs())
      {
         if (input.getName().equals("arguments") &&
                  input instanceof UIInputMany)
         {
            setInput(input, commandLine.getArguments(), registry);
         }
         else if (input instanceof UIInputMany)
         {
            setInput(input, commandLine.getOptionValues(input.getName()), registry);
         }
         else
         {
            setInput(input, commandLine.getOptionValue(input.getName()), registry);
         }
      }
   }

   public static void setInput(InputComponent<?, Object> input, Object value, AddonRegistry registry)
   {
      if (converterFactory == null)
      {
         converterFactory = registry.getExportedInstance(ConverterFactory.class).get();
      }
      InputComponents.setValueFor(converterFactory, input, value);
   }

}
