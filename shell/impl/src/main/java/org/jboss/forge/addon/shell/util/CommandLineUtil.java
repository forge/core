/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.CommandLineParser;
import org.jboss.aesh.cl.OptionBuilder;
import org.jboss.aesh.cl.ParserBuilder;
import org.jboss.aesh.cl.exception.OptionParserException;
import org.jboss.aesh.cl.internal.ParameterInt;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.shell.ShellContext;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.addons.AddonRegistry;

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
      builder.parameter(parameter);
      return builder.generateParser();
   }

   @SuppressWarnings("unchecked")
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
         else if (input instanceof UIInput)
         {
            setInput(input, commandLine.getOptionValue(input.getName()), registry);
         }
         else if (input instanceof UISelectMany)
         {
            setInputChoices((UISelectMany<Object>) input, commandLine.getOptionValues(input.getName()), registry);
         }
         else if (input instanceof UISelectOne)
         {
            setInputChoice((UISelectOne<Object>) input, commandLine.getOptionValue(input.getName()), registry);
         }
      }
   }

   private static void setInputChoice(UISelectOne<Object> input, String optionValue, AddonRegistry registry)
   {
      Converter<Object, String> labelConverter = input.getItemLabelConverter();
      boolean found = false;
      for (Object choice : input.getValueChoices())
      {
         if (labelConverter.convert(choice).equals(optionValue))
         {
            input.setValue(choice);
            found = true;
            break;
         }
      }

      if (!found)
         logger.warning("Could not find matching value choice for input value [" + optionValue + "]");
   }

   private static void setInputChoices(UISelectMany<Object> input, List<String> optionValues,
            AddonRegistry registry)
   {
      Converter<Object, String> labelConverter = input.getItemLabelConverter();
      List<Object> selected = new ArrayList<Object>();
      for (String optionValue : optionValues)
      {
         boolean found = false;
         for (Object choice : input.getValueChoices())
         {
            if (labelConverter.convert(choice).equals(optionValue))
            {
               selected.add(choice);
               found = true;
               break;
            }
         }

         if (!found)
            logger.warning("Could not find matching value choice for input value [" + optionValue + "]");
      }
      input.setValue(selected);
   }

   public static void setInput(InputComponent<?, Object> input, Object value, AddonRegistry registry)
   {
      if (converterFactory == null)
      {
         converterFactory = registry.getServices(ConverterFactory.class).get();
      }
      InputComponents.setValueFor(converterFactory, input, value);
   }

}
