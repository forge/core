/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.builder.OptionBuilder;
import org.jboss.aesh.cl.completer.OptionCompleter;
import org.jboss.aesh.cl.converter.CLConverter;
import org.jboss.aesh.cl.exception.OptionParserException;
import org.jboss.aesh.cl.internal.ProcessedCommand;
import org.jboss.aesh.cl.internal.ProcessedOption;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.cl.validator.OptionValidator;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.aesh.completion.OptionCompleterFactory;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.util.ShellUtil;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.ManyValued;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.InputComponents;

/**
 * Contains utility methods to parse command lines
 * 
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CommandLineUtil
{
   private static final Logger logger = Logger.getLogger(CommandLineUtil.class.getName());

   private static final String ARGUMENTS_INPUT_NAME = "arguments";

   private ConverterFactory converterFactory;

   public CommandLineUtil(ConverterFactory converterFactory)
   {
      this.converterFactory = converterFactory;
   }

   public CommandLineParser generateParser(UICommand command, ShellContext shellContext,
            Map<String, InputComponent<?, Object>> inputs)
   {
      ProcessedCommand processedCommand = generateCommand(command, shellContext, inputs);
      return new ForgeCommandLineParser(processedCommand, this, inputs);
   }

   private ProcessedCommand generateCommand(UICommand command, ShellContext shellContext,
            Map<String, InputComponent<?, Object>> inputs)
   {
      UICommandMetadata metadata = command.getMetadata(shellContext);
      ProcessedCommand parameter = new ProcessedCommand(ShellUtil.shellifyName(metadata.getName()),
               metadata.getDescription());

      for (final InputComponent<?, Object> input : inputs.values())
      {
         final Object defaultValue = InputComponents.getValueFor(input);
         final boolean isMultiple = input instanceof ManyValued;
         final boolean hasValue = (InputComponents.getInputType(input) != InputType.CHECKBOX && !Boolean.class
                  .isAssignableFrom(input.getValueType()));
         try
         {
            OptionBuilder optionBuilder = new OptionBuilder();

            optionBuilder.name(input.getName())
                     .addDefaultValue(defaultValue == null ? null : defaultValue.toString())
                     .description(input.getLabel())
                     .hasMultipleValues(isMultiple)
                     .hasValue(hasValue)
                     .type(input.getValueType())
                     .required(input.isRequired());

            OptionCompleter completer = OptionCompleterFactory.getCompletionFor(input, shellContext, converterFactory);
            optionBuilder.completer(completer);
            optionBuilder.validator(new OptionValidator<Object>()
            {
               @Override
               public boolean isEnabled(ProcessedCommand command)
               {
                  return input.isEnabled();
               }

               @Override
               public void validate(Object value) throws org.jboss.aesh.cl.validator.OptionValidatorException
               {
                  // TODO: Call input.validate?
               }
            });
            optionBuilder.converter(new CLConverter<Object>()
            {
               @SuppressWarnings("unchecked")
               @Override
               public Object convert(String value)
               {
                  Object inputValue = InputComponents.convertToUIInputValue(converterFactory, input, value);
                  // Many valued elements are called one at a time, so it needs to add to the existing values
                  if (isMultiple)
                  {
                     Iterable<Object> iterable = (Iterable<Object>) InputComponents.getValueFor(input);
                     List<Object> items = new ArrayList<Object>();
                     if (iterable != null)
                     {
                        for (Object item : iterable)
                        {
                           items.add(item);
                        }
                     }
                     items.add(inputValue);
                     InputComponents.setValueFor(converterFactory, input, items);
                  }
                  else
                  {
                     InputComponents.setValueFor(converterFactory, input, inputValue);
                  }
                  return inputValue;
               }
            });
            if (input.getShortName() != InputComponents.DEFAULT_SHORT_NAME)
            {
               optionBuilder.shortName(input.getShortName());
            }
            ProcessedOption option = optionBuilder.create();
            if (ARGUMENTS_INPUT_NAME.equals(input.getName()))
            {
               parameter.setArgument(option);
            }
            else
            {
               parameter.addOption(option);
            }
         }
         catch (OptionParserException e)
         {
            logger.log(Level.SEVERE, "Error while parsing command option", e);
         }
      }
      return parameter;
   }

   public Map<String, InputComponent<?, Object>> populateUIInputs(CommandLine commandLine,
            Map<String, InputComponent<?, Object>> inputs)
   {
      Map<String, InputComponent<?, Object>> populatedInputs = new HashMap<String, InputComponent<?, Object>>();
      for (Entry<String, InputComponent<?, Object>> entry : inputs.entrySet())
      {
         String name = entry.getKey();
         InputComponent<?, Object> input = entry.getValue();
         if (ARGUMENTS_INPUT_NAME.equals(name))
         {
            InputComponents.setValueFor(converterFactory, input, commandLine.getArgument().getValue());
            populatedInputs.put(name, input);
         }
         if (commandLine.hasOption(name))
         {
            if (input instanceof ManyValued)
            {
               InputComponents.setValueFor(converterFactory, input, commandLine.getOptionValues(input.getName()));
               populatedInputs.put(name, input);
            }
            else if (input instanceof SingleValued)
            {
               InputComponents.setValueFor(converterFactory, input, commandLine.getOptionValue(input.getName()));
               populatedInputs.put(name, input);
            }
         }
      }
      return populatedInputs;
   }
}
