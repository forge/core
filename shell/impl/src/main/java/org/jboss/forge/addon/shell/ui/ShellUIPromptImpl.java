/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.Buffer;
import org.jboss.aesh.console.command.CommandOperation;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.Key;
import org.jboss.aesh.terminal.TerminalColor;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.aesh.terminal.TerminalTextStyle;
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
      try
      {
         return promptInternal(message, true, false);
      }
      catch (InterruptedException e)
      {
         return null;
      }
   }

   @Override
   public String promptSecret(String message)
   {
      try
      {
         return promptInternal(message, false, false);
      }
      catch (InterruptedException e)
      {
         return null;
      }
   }

   @Override
   public boolean promptBoolean(String message)
   {
      return promptBoolean(message, true);
   }

   @Override
   public boolean promptBoolean(String message, boolean defaultValue)
   {
      try
      {
         return promptBooleanInternal(message, defaultValue);
      }
      catch (InterruptedException e)
      {
         return defaultValue;
      }
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public Object promptValueFrom(InputComponent<?, ?> input) throws InterruptedException
   {
      Object value = null;
      if (input instanceof SingleValued)
      {
         if (input instanceof SelectComponent)
         {
            // UISelectOne
            SelectComponent select = (SelectComponent) input;
            value = promptSelectComponent(select, Collections.singleton(select.getValue()));
         }
         else
         {
            // UIInput
            value = promptInputComponent(input, input.getValue());
         }
      }
      else if (input instanceof ManyValued)
      {
         List<Object> inputValues = new ArrayList<>();
         Iterable<Object> currentValues = ((ManyValued<?, Object>) input).getValue();
         if (currentValues != null)
         {
            inputValues.addAll(Lists.toList(currentValues));
         }
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
               promptValue = promptInputComponent(input, inputValues);
            }
            if (promptValue != null)
            {
               if (inputValues.contains(promptValue))
               {
                  inputValues.remove(promptValue);
               }
               else
               {
                  inputValues.add(promptValue);
               }
            }
         }
         while (promptValue != null);
         if (!inputValues.isEmpty())
         {
            value = inputValues;
         }
      }
      if (value != null)
         InputComponents.setValueFor(converterFactory, input, value);
      return input.getValue();
   }

   private String promptInternal(String message, boolean echo, boolean required) throws InterruptedException
   {
      PrintStream out = console.getShell().out();
      // prompts should begin with a blue '?'
      String indicator = (required) ? "*" : "?";
      String promptFlag = new TerminalString(indicator, new TerminalColor(Color.BLUE, Color.DEFAULT),
               new TerminalTextStyle(
                        CharacterType.BOLD)).toString();
      out.print(promptFlag + " " + message + " ");
      String output;
      if (isAcceptDefaultsEnabled())
      {
         output = null;
      }
      else
      {
         output = readInput(out, echo);
      }
      out.println();
      return output;
   }

   private boolean promptBooleanInternal(String message, boolean defaultValue) throws InterruptedException
   {
      String suffix = (defaultValue) ? " [Y/n]:" : " [y/N]:";
      String answer = promptInternal(message + suffix, true, false);
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

   private Object promptInputComponent(InputComponent<?, ?> input, Object componentValue) throws InterruptedException
   {
      Object value;
      String label = InputComponents.getLabelFor(input, false);
      String description = input.getDescription();
      if (!Strings.isNullOrEmpty(description))
      {
         description = " (" + description + ")";
      }
      else
      {
         description = "";
      }
      String defaultValueDescription;
      if (componentValue instanceof Collection)
      {
         defaultValueDescription = " " + componentValue.toString();
      }
      else if (componentValue != null)
      {
         defaultValueDescription = " [" + componentValue + "]";
      }
      else
      {
         defaultValueDescription = "";
      }
      String inputType = InputComponents.getInputType(input);
      if (InputType.SECRET.equals(inputType))
      {
         value = promptInternal(label + description + defaultValueDescription + ": ", false, input.isRequired());
      }
      else if (input.getValueType() == Boolean.class)
      {
         Boolean defaultValue = (Boolean) input.getValue();
         value = promptBooleanInternal(label + description, defaultValue == null || defaultValue.booleanValue());
      }
      else
      {
         value = promptInternal(label + description + defaultValueDescription + ": ", true, input.isRequired());
      }
      return value;

   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private Object promptSelectComponent(SelectComponent select, Collection<Object> existingItems)
            throws InterruptedException
   {
      if (isAcceptDefaultsEnabled())
      {
         return select.hasDefaultValue() ? select.getValue() : null;
      }

      PrintStream out = console.getShell().out();
      String label = InputComponents.getLabelFor(select, false);
      String description = select.getDescription();
      if (!Strings.isNullOrEmpty(description))
      {
         description = " (" + description + "):";
      }
      else
      {
         description = ":";
      }
      Object value = null;
      Converter<Object, String> itemLabelConverter = InputComponents.getItemLabelConverter(converterFactory,
               select);
      List<Object> items = Lists.toList(select.getValueChoices());
      if (items.size() > 0)
      {
         out.println();
         for (int i = 0; i < items.size(); i++)
         {
            Object item = items.get(i);
            String itemLabel = itemLabelConverter.convert(item);
            Object separator = existingItems.contains(item) ? "(x)" : "( )";
            out.printf("[%s] %s %s", i, separator, itemLabel);
            out.println();
         }
         out.println();
         int idx;
         try
         {
            out.println("Press <ENTER> to confirm, or <CTRL>+C to cancel.");
            String limit = items.size() == 1 ? "" : "-" + (items.size() - 1);
            String message = label + description + " [0" + limit + "]";
            idx = Integer.parseInt(promptInternal(message, true, select.isRequired()));
         }
         catch (NumberFormatException nfe)
         {
            idx = -1;
         }
         if (idx > -1 && idx < items.size())
         {
            value = items.get(idx);
         }
      }
      return value;
   }

   /**
    * Performs the hard work
    * 
    * @throws InterruptedException if CTRL+C or CTRL+D is pressed
    */
   private String readInput(PrintStream out, boolean echo) throws InterruptedException
   {
      StringBuilder sb = new StringBuilder();
      Key inputKey;
      do
      {
         CommandOperation input = commandInvocation.getInput();
         inputKey = input.getInputKey();
         if (inputKey == Key.CTRL_C || inputKey == Key.CTRL_D)
         {
            throw new InterruptedException(inputKey.name());
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
      return (sb.length() == 0) ? null : sb.toString();
   }

   private boolean isAcceptDefaultsEnabled()
   {
      if (!context.isInteractive())
      {
         return true;
      }
      Object acceptDefaultsFlag = context.getAttributeMap().get("ACCEPT_DEFAULTS");
      return acceptDefaultsFlag != null && "true".equalsIgnoreCase(acceptDefaultsFlag.toString());
   }
}
