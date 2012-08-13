/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.command.parser.CommandParser;
import org.jboss.forge.shell.command.parser.CommandParserContext;
import org.jboss.forge.shell.command.parser.CompositeCommandParser;
import org.jboss.forge.shell.command.parser.NamedBooleanOptionParser;
import org.jboss.forge.shell.command.parser.NamedValueOptionParser;
import org.jboss.forge.shell.command.parser.NamedValueVarargsOptionParser;
import org.jboss.forge.shell.command.parser.NullTokenOptionParser;
import org.jboss.forge.shell.command.parser.OrderedValueOptionParser;
import org.jboss.forge.shell.command.parser.OrderedValueVarargsOptionParser;
import org.jboss.forge.shell.exceptions.PluginExecutionException;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.util.Enums;
import org.jboss.forge.shell.util.GeneralUtils;
import org.mvel2.util.ParseTools;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ExecutionParser
{
   private final PluginRegistry registry;
   private final Instance<Execution> executionInstance;
   private final Shell shell;
   private final PromptTypeConverter promptTypeConverter;

   @Inject
   public ExecutionParser(final PluginRegistry registry, final Instance<Execution> executionInstance,
                          final Shell shell, final PromptTypeConverter promptTypeConverter)
   {
      this.registry = registry;
      this.executionInstance = executionInstance;
      this.shell = shell;
      this.promptTypeConverter = promptTypeConverter;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public Execution parse(final Queue<String> tokens, final String pipeIn, final PipeOut pipeOut)
   {
      Execution execution = executionInstance.get();
      // execution.setOriginalStatement(line);
      CommandMetadata command = null;

      if (!tokens.isEmpty())
      {
         String first = tokens.remove();
         execution.setOriginalStatement(first);
         PluginMetadata plugin = registry.getPluginMetadataForScopeAndConstraints(first, shell);

         if (plugin != null)
         {
            if (!tokens.isEmpty())
            {
               String second = tokens.peek();
               command = plugin.getCommand(second, shell);

               if (command != null)
               {
                  if (!command.isDefault())
                  {
                     tokens.remove();
                  }
               }
               else if (plugin.hasDefaultCommand())
               {
                  command = plugin.getDefaultCommand();
               }
            }
            else if (plugin.hasDefaultCommand())
            {
               command = plugin.getDefaultCommand();
            }

            if (command != null)
            {
               if (!command.usableWithResource(shell.getCurrentResource().getClass()))
               {
                  // noinspection unchecked
                  throw new PluginExecutionException(plugin, "command '"
                           + command.getName()
                           + "' is not usable in current scope ["
                           + shell.getCurrentResource().getClass().getSimpleName()
                           + "]"
                           + " -- usable scopes: "
                           + GeneralUtils.elementSetSimpleTypesToString((Set) command.getResourceScopes()));
               }

               execution.setCommand(command);

               // parse parameters and set order / nulls for command invocation

               Object[] parameters = parseParameters(command, tokens, pipeIn, pipeOut);
               execution.setParameterArray(parameters);
            }
            else
            {
               throw new PluginExecutionException(plugin, "Missing command for plugin [" + plugin.getName()
                        + "], available commands: " + plugin.getCommands(shell));
            }
         }
         else
         {
            List<PluginMetadata> pluginMetadata = registry.getPluginMetadata(first);
            if ((pluginMetadata != null) && !pluginMetadata.isEmpty())
            {
               Set<Class<? extends Resource<?>>> aggregate = new HashSet<Class<? extends Resource<?>>>();
               for (PluginMetadata meta : pluginMetadata)
               {
                  Set<Class<? extends Resource<?>>> scopes = meta.getResourceScopes();
                  aggregate.addAll(scopes);
               }

               throw new PluginExecutionException(pluginMetadata.get(0),
                        "Plugin is not usable in current scope or project.");
            }
         }
      }

      return execution;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private Object[] parseParameters(final CommandMetadata command, final Queue<String> tokens, final String pipeIn,
            final PipeOut pipeOut)
   {
      CommandParser commandParser = new CompositeCommandParser(new NamedBooleanOptionParser(),
               new NamedValueOptionParser(), new NamedValueVarargsOptionParser(), new OrderedValueOptionParser(),
               new OrderedValueVarargsOptionParser(), new NullTokenOptionParser());

      CommandParserContext context = new CommandParserContext();
      Map<OptionMetadata, Object> valueMap = commandParser.parse(command, tokens, context)
               .getValueMap();

      for (String warning : context.getWarnings())
      {
         ShellMessages.info(shell, warning);
      }

      Object[] parameters = new Object[command.getOptions().size()];
      for (OptionMetadata option : command.getOptions())
      {

         PromptType promptType = option.getPromptType();
         String defaultValue = option.getDefaultValue();
         Class<?> optionType = option.getBoxedType();
         String optionDescriptor = option.getOptionDescriptor() + ": ";

         Object value;
         if (option.isPipeOut())
         {
            value = pipeOut;
         }
         else if (option.isPipeIn())
         {
            value = pipeIn;

            if (pipeIn != null)
            {
               if (InputStream.class.isAssignableFrom(option.getBoxedType()))
               {
                  value = new InputStream()
                  {
                     int cursor = 0;
                     int len = pipeIn.length();

                     @Override
                     public int read() throws IOException
                     {
                        return cursor != len ? pipeIn.charAt(cursor++) : -1;
                     }
                  };
               }
            }

         }
         else
         {
            value = valueMap.get(option);
         }

         if (!option.isPipeOut() && !option.isPipeIn())
         {
            // TODO Is this really where we want to do PromptType conversion?
            value = doPromptTypeConversions(value, promptType);

            if ((value != null) && option.getBoxedType().isEnum() && !Enums.hasValue(option.getType(), value))
            {
               ShellMessages.info(shell, "Could not parse [" + value + "]... please try again...");
               if (!option.hasCustomCompleter())
               {
                  value = shell.promptEnum(optionDescriptor, (Class<Enum>) option.getType());
               }
               else
               {
                  value = shell.promptCompleter(optionDescriptor, option.getCompleterType());
               }
            }
            else if (((value != null) && (promptType != null)) && !promptType.matches(value.toString()))
            {
               // make sure the current option value is OK
               ShellMessages.info(shell, "Could not parse [" + value + "]... please try again...");
               value = shell.promptCommon(optionDescriptor, promptType);
            }
            else if (option.isRequired() && (value == null) && (!option.hasDefaultValue()))
            {
               while (value == null)
               {
                  if (option.isEnum())
                  {
                     value = shell.promptEnum(optionDescriptor, (Class<Enum>) option.getType());
                  }
                  else if (isBooleanOption(option))
                  {
                     value = shell.promptBoolean(optionDescriptor);
                  }
                  else if (isFileOption(optionType))
                  {
                     value = shell.promptFile(optionDescriptor);
                  }
                  else if ((promptType != null) && !PromptType.ANY.equals(promptType))
                  {
                     // make sure an omitted required option value is OK
                     value = shell.promptCommon(optionDescriptor, promptType);
                  }
                  else
                  {
                     value = shell.prompt(optionDescriptor);
                  }

                  if (String.valueOf(value).trim().length() == 0)
                  {
                     ShellMessages.info(shell, "The option is required to execute this command.");
                     value = null;
                  }
               }
            }
            else if ((value == null) && (option.hasDefaultValue()))
            {
               value = defaultValue;
            }
         }

         parameters[option.getIndex()] = value;
         // Default values seem to be ignored for Enums
      }

      return parameters;
   }

   private Object doPromptTypeConversions(Object value, final PromptType promptType)
   {
      if ((value != null) && value.getClass().isArray())
      {
         Object[] values = (Object[]) value;
         for (int i = 0; i < values.length; i++)
         {
            values[i] = promptTypeConverter.convert(promptType, (String) values[i]);
         }

         value = values;
      }
      else
      {
         value = promptTypeConverter.convert(promptType, (String) value);
      }
      return value;
   }

   public boolean isFileOption(final Class<?> optionType)
   {
      return File.class.isAssignableFrom(optionType);
   }

   private static boolean isBooleanOption(final OptionMetadata option)
   {
      return ParseTools.unboxPrimitive(option.getType()) == boolean.class;
   }
}
