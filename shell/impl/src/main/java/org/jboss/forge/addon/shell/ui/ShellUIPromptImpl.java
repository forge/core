/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.Buffer;
import org.jboss.aesh.console.command.CommandOperation;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.aesh.terminal.Key;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.ManyValued;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.furnace.util.Strings;

/**
 * Implementation of {@link UIPrompt}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUIPromptImpl implements UIPrompt
{
   private final ShellContext context;
   private final AeshConsole console;
   private final ConverterFactory converterFactory;
   private final CommandInvocation commandInvocation;

   public ShellUIPromptImpl(ShellContext context, ConverterFactory converterFactory)
   {
      this.context = context;
      this.converterFactory = converterFactory;
      this.console = context.getProvider().getConsole();
      this.commandInvocation = (CommandInvocation) context.getAttributeMap()
               .get(CommandInvocation.class);
   }

   @Override
   public String prompt(String message)
   {
      if (isAcceptDefaultsEnabled())
      {
         return null;
      }
      PrintStream out = console.getShell().out();
      out.print(message + " ");
      String output = readInput(out, true);
      out.println();
      return output;
   }

   @Override
   public String promptSecret(String message)
   {
      if (isAcceptDefaultsEnabled())
      {
         return null;
      }
      PrintStream out = console.getShell().out();
      out.print(message + " ");
      String output = readInput(out, false);
      out.println();
      return output;
   }

   @Override
   public boolean promptBoolean(String message)
   {
      return promptBoolean(message, true);
   }

   @Override
   public boolean promptBoolean(String message, boolean defaultValue)
   {
      if (isAcceptDefaultsEnabled())
      {
         return defaultValue;
      }
      String suffix = (defaultValue) ? " [Y/n] " : " [y/N] ";
      String answer = prompt(message + suffix);
      if (Strings.isNullOrEmpty(answer))
      {
         return defaultValue;
      }
      else
      {
         if (defaultValue)
         {
            return !"N".equalsIgnoreCase(answer);
         }
         else
         {
            return "Y".equalsIgnoreCase(answer);
         }
      }
   }

   @SuppressWarnings("rawtypes")
   public Object promptValueFrom(InputComponent<?, ?> input)
   {
      Object value = null;
      if (input instanceof SingleValued)
      {
         if (input instanceof SelectComponent)
         {
            // UISelectOne
            SelectComponent select = (SelectComponent) input;
            value = promptSelectComponent(select, Collections.emptyList());
         }
         else
         {
            // UIInput
            value = promptInputComponent(input);
         }
      }
      else if (input instanceof ManyValued)
      {
         List<Object> inputValues = new ArrayList<>();
         Object promptValue;
         do
         {
            if (input instanceof SelectComponent)
            {
               // UISelectMany
               SelectComponent select = (SelectComponent) input;
               promptValue = promptSelectComponent(select, inputValues);
            }
            else
            {
               // UIInputMany
               promptValue = promptInputComponent(input);
            }
            if (promptValue != null)
            {
               inputValues.add(promptValue);
            }
         }
         while (promptValue != null);
         if (!inputValues.isEmpty())
         {
            value = inputValues;
         }
      }
      InputComponents.setValueFor(converterFactory, input, value);
      return input.getValue();
   }

   private Object promptInputComponent(InputComponent<?, ?> input)
   {
      Object value;
      String label = InputComponents.getLabelFor(input, true);
      String inputType = InputComponents.getInputType(input);
      if (InputType.SECRET.equals(inputType))
      {
         value = promptSecret(label);
      }
      else if (input.getValueType() == Boolean.class)
      {
         value = promptBoolean(label);
      }
      else
      {
         value = prompt(label + " (ESC to exit)");
      }
      return value;

   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private Object promptSelectComponent(SelectComponent select, List<Object> existingItems)
   {
      PrintStream out = console.getShell().out();
      String label = InputComponents.getLabelFor(select, true);
      Object value = null;
      Converter<Object, String> itemLabelConverter = InputComponents.getItemLabelConverter(converterFactory,
               select);
      List<Object> items = new ArrayList<>(Lists.toList(select.getValueChoices()));
      items.removeAll(existingItems);
      if (items.isEmpty())
      {
         return null;
      }
      out.println();
      for (int i = 0; i < items.size(); i++)
      {
         Object item = items.get(i);
         String itemLabel = itemLabelConverter.convert(item);
         out.printf("[%s] - %s", i, itemLabel);
         out.println();
      }
      out.println();
      int idx;
      try
      {
         idx = Integer.parseInt(prompt(label + " [0-" + (items.size() - 1) + "] (ESC to exit)"));
      }
      catch (NumberFormatException nfe)
      {
         idx = -1;
      }
      if (idx > -1 && idx < items.size())
      {
         value = items.get(idx);
      }
      return value;
   }

   private String readInput(PrintStream out, boolean echo)
   {
      String output;
      try
      {
         StringBuilder sb = new StringBuilder();
         Key inputKey;
         do
         {
            CommandOperation input = commandInvocation.getInput();
            inputKey = input.getInputKey();
            if (inputKey == Key.ESC)
            {
               return null;
            }
            else if (inputKey == Key.BACKSPACE && sb.length() > 0)
            {
               sb.setLength(sb.length() - 1);
               if (echo)
               {
                  // move cursor left
                  out.print(Buffer.printAnsi("1D"));
                  out.flush();
                  // overwrite it with space
                  out.print(" ");
                  // move cursor back again
                  out.print(Buffer.printAnsi("1D"));
                  out.flush();
               }
            }
            else if (inputKey.isPrintable())
            {
               if (echo)
                  out.print(inputKey.getAsChar());

               sb.append(inputKey.getAsChar());
            }
         }
         while (inputKey != Key.ENTER && inputKey != Key.ENTER_2);
         output = (sb.length() == 0) ? null : sb.toString();
      }
      catch (InterruptedException e)
      {
         output = null;
      }
      return output;
   }

   private boolean isAcceptDefaultsEnabled()
   {
      Object acceptDefaultsFlag = context.getAttributeMap().get("ACCEPT_DEFAULTS");
      return acceptDefaultsFlag != null && "true".equalsIgnoreCase(acceptDefaultsFlag.toString());
   }

}
