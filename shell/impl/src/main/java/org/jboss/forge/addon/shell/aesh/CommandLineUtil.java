/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.internal.ProcessedCommand;
import org.jboss.aesh.cl.internal.ProcessedCommandBuilder;
import org.jboss.aesh.cl.internal.ProcessedOption;
import org.jboss.aesh.cl.internal.ProcessedOptionBuilder;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.cl.parser.CommandLineParserException;
import org.jboss.aesh.cl.parser.OptionParserException;
import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.ConfigurationFactory;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.util.ResourcePathResolver;
import org.jboss.forge.addon.shell.aesh.completion.OptionCompleterFactory;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.util.ShellUtil;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.ManyValued;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * Contains utility methods to parse command lines
 * 
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class CommandLineUtil
{
   /**
    * System property to set the option style for the UI
    */
   private static final String OPTION_STYLE_PROPERTY = "org.jboss.forge.ui.shell_option_style";
   private static final String DASHED_OPTION_STYLE = "dashed";

   private static final Logger logger = Logger.getLogger(CommandLineUtil.class.getName());

   private static final String ARGUMENTS_INPUT_NAME = "arguments";

   private final ConverterFactory converterFactory;
   private Configuration userConfig;

   public CommandLineUtil(AddonRegistry addonRegistry)
   {
      this.converterFactory = addonRegistry.getServices(ConverterFactory.class).get();
      ConfigurationFactory configFactory = addonRegistry.getServices(ConfigurationFactory.class).get();
      this.userConfig = configFactory.getUserConfiguration();
   }

   public CommandLineParser<?> generateParser(CommandAdapter command, CommandController commandController,
            ShellContext shellContext,
            Map<String, InputComponent<?, ?>> inputs)
   {
      ProcessedCommand<?> processedCommand = generateCommand(command, commandController, shellContext, inputs);
      return new ForgeCommandLineParser(processedCommand, this, inputs, shellContext);
   }

   private ProcessedCommand<?> generateCommand(final CommandAdapter commandAdapter,
            final CommandController commandController, final ShellContext shellContext,
            final Map<String, InputComponent<?, ?>> inputs)
   {
      UICommandMetadata metadata = (commandController instanceof WizardCommandController)
               ? ((WizardCommandController) commandController)
                        .getInitialMetadata()
               : commandController.getMetadata();
      String cmdName = ShellUtil.shellifyCommandName(metadata.getName());
      String cmdDescription = metadata.getDescription();
      ProcessedCommandBuilder commandBuilder = new ProcessedCommandBuilder()
               .command(commandAdapter)
               .name(cmdName)
               .description(cmdDescription)
               .resultHandler(new ForgeResultHandler(shellContext, cmdName));
      for (Entry<String, InputComponent<?, ?>> entry : inputs.entrySet())
      {
         final String inputName = entry.getKey();
         final InputComponent<?, ?> input = entry.getValue();
         final Object defaultValue = InputComponents.getValueFor(input);
         final boolean isMultiple = input instanceof ManyValued;
         final boolean hasValue = (!InputType.CHECKBOX.equals(InputComponents.getInputType(input)) && !Boolean.class
                  .isAssignableFrom(input.getValueType()) && !boolean.class.isAssignableFrom(input.getValueType()));
         try
         {
            ProcessedOptionBuilder optionBuilder = new ProcessedOptionBuilder();
            optionBuilder.name(toOptionName(inputName))
                     .addDefaultValue(Objects.toString(defaultValue, null))
                     .description(input.getLabel())
                     .hasMultipleValues(isMultiple)
                     .hasValue(hasValue)
                     .type(input.getValueType())
                     .valueSeparator(' ')
                     .activator(cmd -> input.isEnabled())
                     .completer(OptionCompleterFactory.getCompletionFor(
                              input, shellContext, converterFactory));

            if (input.isRequired() && !input.hasDefaultValue() && !input.hasValue())
            {
               optionBuilder.renderer(OptionRenderers.REQUIRED);
            }
            if (input.isDeprecated())
            {
               optionBuilder.renderer(OptionRenderers.DEPRECATED);
            }
            if (input.getShortName() != InputComponents.DEFAULT_SHORT_NAME)
            {
               optionBuilder.shortName(input.getShortName());
            }
            ProcessedOption option = optionBuilder.create();
            if (ARGUMENTS_INPUT_NAME.equals(input.getName()))
            {
               commandBuilder.argument(option);
            }
            else
            {
               commandBuilder.addOption(option);
            }
         }
         catch (OptionParserException e)
         {
            logger.log(Level.SEVERE, "Error while parsing command option", e);
         }
      }
      try
      {
         return commandBuilder.create();
      }
      catch (CommandLineParserException e)
      {
         throw new RuntimeException("Error while parsing command: " + e.getMessage(), e);
      }
   }

   public Map<String, InputComponent<?, ?>> populateUIInputs(CommandLine<?> commandLine,
            Map<String, InputComponent<?, ?>> inputs, UIContext context)
   {
      // Added command line to UIContext.getAttributeMap
      context.getAttributeMap().put(CommandLine.class, commandLine);
      Map<String, InputComponent<?, ?>> populatedInputs = new LinkedHashMap<>();
      for (Entry<String, InputComponent<?, ?>> entry : inputs.entrySet())
      {
         String name = toOptionName(entry.getKey());
         InputComponent<?, ?> input = entry.getValue();
         if (!input.isEnabled())
         {
            // If the input is disabled, carry on
            continue;
         }
         if (ARGUMENTS_INPUT_NAME.equals(name))
         {
            InputComponents.setValueFor(converterFactory, input, commandLine.getArgument().getValues());
            populatedInputs.put(name, input);
         }
         if (commandLine.hasOption(name))
         {
            Resource<?> initialResource = (Resource<?>) context
                     .getInitialSelection().get();
            if (input instanceof ManyValued)
            {
               List<String> resolvedOptionValues = resolveWildcardSelectOptionValues(commandLine, name, input);
               if (Resource.class.isAssignableFrom(input.getValueType()))
               {
                  List<Resource<?>> resources = new ArrayList<>();
                  for (String optionValue : resolvedOptionValues)
                  {
                     List<Resource<?>> resolved = Collections.emptyList();
                     try
                     {
                        resolved = initialResource.resolveChildren(optionValue);
                     }
                     catch (RuntimeException re)
                     {
                        logger.log(Level.FINER, "Error while resolving option value '" + optionValue + "' for "
                                 + initialResource, re);
                     }
                     resources.addAll(resolved);
                  }
                  if (resources.size() > 0)
                  {
                     InputComponents.setValueFor(converterFactory, input, resources);
                  }
                  else
                  {
                     InputComponents.setValueFor(converterFactory, input, resolvedOptionValues);
                  }
               }
               else
               {
                  InputComponents.setValueFor(converterFactory, input, resolvedOptionValues);
               }
               populatedInputs.put(name, input);
            }
            else if (input instanceof SingleValued)
            {
               String optionValue = commandLine.getOptionValue(name);
               if (Resource.class.isAssignableFrom(input.getValueType()))
               {
                  List<Resource<?>> resolved = Collections.emptyList();
                  try
                  {
                     resolved = initialResource.resolveChildren(optionValue);
                  }
                  catch (RuntimeException re)
                  {
                     logger.log(Level.FINER, "Error while resolving option value '" + optionValue + "' for "
                              + initialResource, re);
                  }
                  if (resolved.size() > 0 && resolved.get(0).exists())
                  {
                     InputComponents.setValueFor(converterFactory, input, resolved.get(0));
                  }
                  else
                  {
                     InputComponents.setValueFor(converterFactory, input, optionValue);
                  }
               }
               else
               {
                  InputComponents.setValueFor(converterFactory, input, optionValue);
               }
               populatedInputs.put(name, input);
            }
         }
      }
      return populatedInputs;
   }

   private List<String> resolveWildcardSelectOptionValues(CommandLine<?> commandLine, String name,
            InputComponent<?, ?> input)
   {
      List<String> optionValues = commandLine.getOptionValues(name);
      List<String> resolvedOptionValues = new ArrayList<>();
      if (input instanceof SelectComponent)
      {
         @SuppressWarnings({ "unchecked", "rawtypes" })
         org.jboss.forge.addon.convert.Converter<Object, String> itemLabelConverter = InputComponents
                  .getItemLabelConverter(converterFactory, (SelectComponent) input);
         Iterable<?> valueChoices = ((SelectComponent<?, ?>) input).getValueChoices();

         for (Object choice : valueChoices)
         {
            String itemLabel = itemLabelConverter.convert(choice);
            for (String optionValue : optionValues)
            {
               String optionPattern = ResourcePathResolver.pathspecToRegEx(optionValue);
               if (optionValue != null && optionValue.equals(optionPattern))
               {
                  resolvedOptionValues.add(optionValue);
                  break;
               }
               if (itemLabel.matches(optionPattern))
               {
                  resolvedOptionValues.add(itemLabel);
                  break;
               }
            }
         }
      }
      else
      {
         resolvedOptionValues = optionValues;
      }
      return resolvedOptionValues;
   }

   public String toOptionName(String name)
   {
      String optionNameStyle = userConfig.getString(OPTION_STYLE_PROPERTY, DASHED_OPTION_STYLE);
      return DASHED_OPTION_STYLE.equals(optionNameStyle)
               ? ShellUtil.shellifyOptionNameDashed(name)
               : ShellUtil.shellifyOptionName(name);

   }
}