/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.activation.OptionActivator;
import org.jboss.aesh.cl.builder.CommandBuilder;
import org.jboss.aesh.cl.completer.OptionCompleter;
import org.jboss.aesh.cl.internal.ProcessedCommand;
import org.jboss.aesh.cl.internal.ProcessedCommandBuilder;
import org.jboss.aesh.cl.internal.ProcessedOption;
import org.jboss.aesh.cl.internal.ProcessedOptionBuilder;
import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.aesh.cl.parser.CommandLineParserException;
import org.jboss.aesh.cl.parser.OptionParserException;
import org.jboss.aesh.console.command.completer.CompleterInvocation;
import org.jboss.aesh.console.command.container.CommandContainer;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.util.ResourcePathResolver;
import org.jboss.forge.addon.shell.ShellImpl;
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
   private static final Logger logger = Logger.getLogger(CommandLineUtil.class.getName());

   private static final String ARGUMENTS_INPUT_NAME = "arguments";

   private final ConverterFactory converterFactory;

   public CommandLineUtil(AddonRegistry addonRegistry)
   {
      this.converterFactory = addonRegistry.getServices(ConverterFactory.class).get();
   }

   public CommandLineParser generateParser(CommandController command, ShellContext shellContext,
            Map<String, InputComponent<?, ?>> inputs)
   {
      CommandContainer processedCommand = generateCommand(command, shellContext, inputs);
      return processedCommand.getParser();
      //return new ForgeCommandLineParser(processedCommand, this, inputs, shellContext);
   }

   public CommandContainer generateContainer(CommandController command, ShellContext shellContext, ShellImpl shell,
                                             AbstractShellInteraction shellInteraction) throws Exception {
      shellInteraction.getController().initialize();
      return generateCommand(command, shellContext, shellInteraction.getController().getInputs(), shellInteraction, shell);
   }

   private CommandContainer generateCommand(final CommandController command, final ShellContext shellContext,
                                            final Map<String, InputComponent<?, ?>> inputs)
   {
      return generateCommand(command, shellContext, inputs, null, null);
   }

   private CommandContainer generateCommand(final CommandController command, final ShellContext shellContext,
            final Map<String, InputComponent<?, ?>> inputs, AbstractShellInteraction shellInteraction, ShellImpl shell)
   {
      UICommandMetadata metadata = (command instanceof WizardCommandController) ? ((WizardCommandController) command)
               .getInitialMetadata() : command.getMetadata();
      String cmdName = ShellUtil.shellifyCommandName(metadata.getName());
      String cmdDescription = metadata.getDescription();
      //final ProcessedCommand parameter = new ProcessedCommand(cmdName, cmdDescription, null, new ForgeResultHandler(
      //         shellContext, cmdName));
      //final ProcessedCommandBuilder builder = new ProcessedCommandBuilder()
      //        .name(cmdName)
      //        .description(cmdDescription)
      //        .resultHandler(new ForgeResultHandler(shellContext, cmdName));

      final CommandBuilder builder = new CommandBuilder()
              .name(cmdName)
              .description(cmdDescription)
              .resultHandler(new ForgeResultHandler(shellContext, cmdName))
              .populator(new ForgeCommandPopulator(this, inputs, shellContext));


      if(shellInteraction != null && shell != null) {
         CommandAdapter commandAdapter = new CommandAdapter(shell, shellContext, shellInteraction);
         builder.command(commandAdapter);
      }

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

            optionBuilder.name(ShellUtil.shellifyOptionName(inputName))
                     .addDefaultValue(defaultValue == null ? null : defaultValue.toString())
                     .description(input.getLabel())
                     .hasMultipleValues(isMultiple)
                     .hasValue(hasValue)
                     .type(input.getValueType());

            if (input.isRequired() && !input.hasDefaultValue() && !input.hasValue())
            {
               optionBuilder.renderer(OptionRenderers.REQUIRED);
            }
            OptionCompleter<CompleterInvocation> completer = OptionCompleterFactory.getCompletionFor(
                     input, shellContext, converterFactory);
            optionBuilder.completer(completer);
            optionBuilder.activator(new OptionActivator()
            {
               @Override
               public boolean isActivated(ProcessedCommand processedCommand)
               {
                  return input.isEnabled();
               }
            }).valueSeparator(' ');

            if (input.getShortName() != InputComponents.DEFAULT_SHORT_NAME)
            {
               optionBuilder.shortName(input.getShortName());
            }
            ProcessedOption option = optionBuilder.create();
            if (ARGUMENTS_INPUT_NAME.equals(input.getName()))
            {
               builder.argument(option);
            }
            else
            {
               builder.addOption(option);
            }
         }
         catch (OptionParserException e)
         {
            logger.log(Level.SEVERE, "Error while parsing command option", e);
         }
      }
         return builder.create();
   }

   public Map<String, InputComponent<?, ?>> populateUIInputs(CommandLine commandLine,
            Map<String, InputComponent<?, ?>> inputs, UIContext context)
   {
      Map<String, InputComponent<?, ?>> populatedInputs = new LinkedHashMap<>();
      for (Entry<String, InputComponent<?, ?>> entry : inputs.entrySet())
      {
         String name = ShellUtil.shellifyOptionName(entry.getKey());
         InputComponent<?, ?> input = entry.getValue();
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
                  if (resolved.size() > 0)
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

   private List<String> resolveWildcardSelectOptionValues(CommandLine commandLine, String name,
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
}
