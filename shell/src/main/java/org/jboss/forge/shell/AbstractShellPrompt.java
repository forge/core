/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.inject.spi.BeanManager;

import jline.console.completer.Completer;
import jline.console.completer.FileNameCompleter;

import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.command.PromptTypeConverter;
import org.jboss.forge.shell.completer.CommandCompleter;
import org.jboss.forge.shell.completer.CompleterAdaptor;
import org.jboss.forge.shell.completer.EnumCompleter;
import org.jboss.forge.shell.util.BeanManagerUtils;
import org.jboss.forge.shell.util.Enums;
import org.jboss.forge.shell.util.Files;
import org.mvel2.DataConversion;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class AbstractShellPrompt implements Shell
{
   abstract BeanManager getBeanManager();

   public AbstractShellPrompt()
   {
      super();
   }

   protected abstract PromptTypeConverter getPromptTypeConverter();

   protected abstract ResourceFactory getResourceFactory();

   protected abstract String promptWithCompleter(String message, Completer completer);

   @Override
   public String prompt()
   {
      return prompt("");
   }

   @Override
   public String promptAndSwallowCR()
   {
      int c;
      StringBuilder buf = new StringBuilder();
      while (((c = scan()) != '\n') && (c != '\r'))
      {
         if (c == 127)
         {
            if (buf.length() > 0)
            {
               buf.deleteCharAt(buf.length() - 1);
               cursorLeft(1);
               print(" ");
               cursorLeft(1);
            }
            continue;
         }

         write((byte) c);
         buf.append((char) c);
      }
      return buf.toString();
   }

   @Override
   public String prompt(final String message)
   {
      return promptCompleter(message, null);
   }

   @Override
   public String promptCompleter(final String string, final Class<? extends CommandCompleter> type)
   {
      return promptWithCompleter(string,
               new CompleterAdaptor(BeanManagerUtils.getContextualInstance(getBeanManager(), type)));
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T prompt(final String message, final Class<T> clazz)
   {
      Object result;
      Object input;
      do
      {
         input = prompt(message);
         try
         {
            result = DataConversion.convert(input, clazz);
         }
         catch (Exception e)
         {
            result = InvalidInput.INSTANCE;
         }
      }
      while ((result instanceof InvalidInput));

      return (T) result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T prompt(final String message, final Class<T> clazz, final T defaultIfEmpty)
   {
      Object result;
      String input;
      do
      {
         input = prompt(message);
         if ((input == null) || "".equals(input.trim()))
         {
            result = defaultIfEmpty;
         }
         else
         {
            input = input.trim();
            try
            {
               result = DataConversion.convert(input, clazz);
            }
            catch (Exception e)
            {
               result = InvalidInput.INSTANCE;
            }
         }
      }
      while ((result instanceof InvalidInput));

      return (T) result;
   }

   @Override
   public boolean promptBoolean(final String message)
   {
      return promptBoolean(message, true);
   }

   @Override
   public boolean promptBoolean(final String message, final boolean defaultIfEmpty)
   {
      String query = " [Y/n] ";
      if (!defaultIfEmpty)
      {
         query = " [y/N] ";
      }

      return prompt(message + query, Boolean.class, defaultIfEmpty);
   }

   @Override
   public int promptChoice(final String message, final Object... options)
   {
      return promptChoice(message, Arrays.asList(options));
   }

   @Override
   public int promptChoice(final String message, final List<?> options)
   {
      if ((options == null) || options.isEmpty())
      {
         throw new IllegalArgumentException(
                  "promptChoice() Cannot ask user to select from a list of nothing. Ensure you have values in your options list.");
      }

      println(message);

      Object result = InvalidInput.INSTANCE;

      while (result instanceof InvalidInput)
      {
         int count = 1;
         println();
         for (Object entry : options)
         {
            if (entry != null)
            {
               println("  " + count + " - [" + entry + "]");
            }
            else
            {
               println("  " + count + " - (none)");
            }
            count++;
         }
         println();
         int input = prompt("Choose an option by typing the number of the selection: ", Integer.class) - 1;
         if (input < options.size())
         {
            return input;
         }
         else
         {
            println("Invalid selection, please try again.");
         }
      }
      return -1;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T promptChoiceTyped(final String message, final List<T> options)
   {
      if ((options == null) || options.isEmpty())
      {
         throw new IllegalArgumentException(
                  "promptChoice() Cannot ask user to select from a list of nothing. Ensure you have values in your options list.");
      }
      if (options.size() == 1)
      {
         return options.get(0);
      }

      println(message);

      Object result = InvalidInput.INSTANCE;

      while (result instanceof InvalidInput)
      {
         int count = 1;
         println();
         for (T entry : options)
         {
            if (entry != null)
            {
               println("  " + count + " - [" + entry + "]");
            }
            else
            {
               println("  " + count + " - (none)");
            }
            count++;
         }
         println();
         int input = prompt("Choose an option by typing the number of the selection: ", Integer.class) - 1;
         if ((input >= 0) && (input < options.size()))
         {
            result = options.get(input);
         }
         else
         {
            println("Invalid selection, please try again.");
         }
      }
      return (T) result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T promptChoiceTyped(final String message, final List<T> options, final T defaultIfEmpty)
   {
      if ((options == null) || options.isEmpty())
      {
         throw new IllegalArgumentException(
                  "promptChoice() Cannot ask user to select from a list of nothing. Ensure you have values in your options list.");
      }
      if (options.size() == 1)
      {
         return options.get(0);
      }

      println(message);

      Object result = InvalidInput.INSTANCE;

      while (result instanceof InvalidInput)
      {
         int count = 1;
         println();
         for (T entry : options)
         {
            if ((entry != null) && entry.equals(defaultIfEmpty))
            {
               print(ShellColor.BOLD, "  " + count + " - [" + entry + "]");
            }
            else if (entry != null)
            {
               print("  " + count + " - [" + entry + "]");
            }
            else if (defaultIfEmpty == null)
            {
               print(ShellColor.BOLD, "  " + count + " - (none)");
            }

            if ((entry == defaultIfEmpty) || ((entry != null) && entry.equals(defaultIfEmpty)))
            {
               print("*");
            }
            println();
            count++;
         }
         println();
         int input = prompt(
                  "Choose an option by typing the number of the selection "
                           + renderColor(ShellColor.BOLD, "[*-default]: "),
                  Integer.class, 0) - 1;
         if ((input >= 0) && (input < options.size()))
         {
            result = options.get(input);
         }
         else if (input == -1)
         {
            result = defaultIfEmpty;
         }
         else
         {
            println("Invalid selection, please try again.");
         }
      }
      return (T) result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T promptChoice(final String message, final Map<String, T> options)
   {
      println(message);
      List<Entry<String, T>> entries = new ArrayList<Map.Entry<String, T>>();
      entries.addAll(options.entrySet());

      Object result = InvalidInput.INSTANCE;
      while (result instanceof InvalidInput)
      {
         int count = 1;
         println();
         for (Entry<String, T> entry : entries)
         {
            println("  " + count + " - [" + entry.getKey() + "]");
            count++;
         }
         println();
         String input = prompt("Choose an option by typing the name or number of the selection: ");
         if (options.containsKey(input))
         {
            result = options.get(input);
         }
         else if (input.matches("[0-9]+"))
         {
            int choice = Integer.parseInt(input);
            if ((choice > 0) && (choice < options.size()))
            {
               result = entries.get(choice - 1).getValue();
            }
         }
      }
      return (T) result;
   }

   @Override
   public String promptCommon(final String message, final PromptType type)
   {

      String input;
      do
      {
         input = prompt(message);
      }
      while (!type.matches(input));
      input = getPromptTypeConverter().convert(type, input);
      return input;
   }

   @Override
   public String promptCommon(final String message, final PromptType type, final String defaultIfEmpty)
   {
      if (!type.matches(defaultIfEmpty))
      {
         throw new IllegalArgumentException("Default value [" + defaultIfEmpty
                  + "] is not a valid match for the given prompt type ["
                  + type.name() + "]");
      }

      String input;
      do
      {
         input = prompt(message);
         if ((input == null) || input.isEmpty())
         {
            input = defaultIfEmpty;
         }
      }
      while (!type.matches(input));
      input = getPromptTypeConverter().convert(type, input);
      return input;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Enum<T>> T promptEnum(final String message, final Class<T> type)
   {
      String value = "";
      while ((value == null) || value.trim().isEmpty())
      {
         value = promptWithCompleter(message, new CompleterAdaptor(new EnumCompleter(type)));
      }

      T result = (T) Enums.valueOf(type, value.trim());
      if (result == null)
      {
         result = promptChoiceTyped(message, Arrays.asList(type.getEnumConstants()));
      }
      return result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Enum<T>> T promptEnum(final String message, final Class<T> type, final T defaultIfEmpty)
   {
      T result;
      do
      {
         String value = promptWithCompleter(message, new CompleterAdaptor(
                  new EnumCompleter(type)));

         if ((value == null) || value.trim().isEmpty())
         {
            result = defaultIfEmpty;
         }
         else
         {
            result = (T) Enums.valueOf(type, value.trim());
            if (result == null)
            {
               result = promptChoiceTyped(message, Arrays.asList(type.getEnumConstants()),
                        defaultIfEmpty);
            }
         }
      }
      while (result == null);

      return result;
   }

   @Override
   public FileResource<?> promptFile(final String message)
   {
      String path = "";
      while ((path == null) || path.trim().isEmpty())
      {
         path = promptWithCompleter(message, new FileNameCompleter());
      }

      path = Files.canonicalize(path);
      Resource<File> resource = getResourceFactory().getResourceFrom(new File(path));

      if (resource instanceof FileResource)
      {
         return (FileResource<?>) resource;
      }
      return null;
   }

   @Override
   public FileResource<?> promptFile(final String message, final FileResource<?> defaultIfEmpty)
   {
      FileResource<?> result = defaultIfEmpty;
      String path = promptWithCompleter(message, new FileNameCompleter());
      if ((path != null) && !path.trim().isEmpty())
      {
         path = Files.canonicalize(path);
         Resource<File> resource = getResourceFactory().getResourceFrom(new File(path));

         if (resource instanceof FileResource)
         {
            result = (FileResource<?>) resource;
         }
         else
         {
            result = null;
         }
      }
      return result;
   }

   @Override
   public String promptRegex(final String message, final String regex)
   {
      String input;
      do
      {
         input = prompt(message);
      }
      while (!input.matches(regex));
      return input;
   }

   @Override
   public String promptRegex(final String message, final String pattern, final String defaultIfEmpty)
   {
      if (!defaultIfEmpty.matches(pattern))
      {
         throw new IllegalArgumentException("Default value [" + defaultIfEmpty + "] does not match required pattern ["
                  + pattern + "]");
      }

      String input;
      do
      {
         input = prompt(message + " [" + defaultIfEmpty + "]");
         if ("".equals(input.trim()))
         {
            input = defaultIfEmpty;
         }
      }
      while (!input.matches(pattern));
      return input;
   }

}