/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.command.PromptTypeConverter;
import org.jboss.forge.shell.completer.CommandCompleter;
import org.jboss.forge.shell.completer.CompleterAdaptor;
import org.jboss.forge.shell.completer.EnumCompleter;
import org.jboss.forge.shell.console.jline.console.completer.Completer;
import org.jboss.forge.shell.console.jline.console.completer.FileNameCompleter;
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
   public String prompt(final String message, final String defaultIfEmpty)
   {
      return prompt(message, String.class, defaultIfEmpty);
   }

   @Override
   public String promptAndSwallowCR()
   {
      int c;
      StringBuilder buf = new StringBuilder();
      while (((c = scan()) != '\n') && (c != '\r'))
      {
         if ((c == 127) || (c == 8))
         {
            if (buf.length() > 0)
            {
               buf.deleteCharAt(buf.length() - 1);
               cursorLeft(1);
               print(" ");
               cursorLeft(1);
               flush();
            }
            continue;
         }

         write((byte) c);
         buf.append((char) c);
         flush();
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
      if (this.isAcceptDefaults())
      {
         return defaultIfEmpty;
      }

      Object result;
      String input;
      do
      {
         String query = " [" + defaultIfEmpty + "] ";

         if (defaultIfEmpty instanceof Boolean)
         {
            if (Boolean.TRUE.equals(defaultIfEmpty))
               query = " [Y/n] ";
            else
            {
               query = " [y/N] ";
            }
         }

         input = prompt(message + query);
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
      return prompt(message, Boolean.class, defaultIfEmpty);
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
   public <T> T promptChoiceTyped(final String message, List<T> options)
   {
      if ((options == null) || options.isEmpty())
      {
         throw new IllegalArgumentException(
                  "promptChoice() Cannot ask user to select from a list of nothing. Ensure you have values in your options list.");
      }

      /*
       * Remove duplicates from the list.
       */
      options = new ArrayList<T>(new LinkedHashSet<T>(options));

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
   public <T> T promptChoiceTyped(final String message, List<T> options, final T defaultIfEmpty)
   {
      if (this.isAcceptDefaults())
      {
         return defaultIfEmpty;
      }

      if ((options == null) || options.isEmpty())
      {
         throw new IllegalArgumentException(
                  "promptChoice() Cannot ask user to select from a list of nothing. Ensure you have values in your options list.");
      }

      /*
       * Remove duplicates from the list.
       */
      options = new ArrayList<T>(new LinkedHashSet<T>(options));

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

            boolean objectEquals = (entry != null) && entry.equals(defaultIfEmpty);
            boolean instanceEquals = entry == defaultIfEmpty;
            if (instanceEquals || objectEquals)
            {
               print("*");
            }
            println();
            count++;
         }
         println();
         int input = prompt(
                  "Choose an option by typing the number of the selection "
                           + renderColor(ShellColor.BOLD, "[*-default] "),
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
      final List<Entry<String, T>> entries = new ArrayList<Map.Entry<String, T>>();
      entries.addAll(options.entrySet());

      //very like StringsCompleter
      class ChoiceCompleter implements Completer
      {
         final SortedSet<String> keys = new TreeSet<String>(options.keySet());

         @Override
         public int complete(String buffer, int cursor, List<CharSequence> candidates)
         {
            for (String s : keys.tailSet(buffer)) {
               if (!s.startsWith(buffer))
               {
                  break;
               }
               candidates.add(s);
            }
            return candidates.isEmpty() ? -1 : 0;
         }
      }

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
         String input = promptWithCompleter("Choose an option by typing the name or number of the selection: ",
                  new ChoiceCompleter());
         if (options.containsKey(input))
         {
            result = options.get(input);
         }
         else if (input.matches("[0-9]+"))
         {
            int choice = Integer.parseInt(input);
            if ((choice > 0) && (choice <= options.size()))
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
      if (this.isAcceptDefaults())
      {
         return defaultIfEmpty;
      }

      if (!type.matches(defaultIfEmpty))
      {
         throw new IllegalArgumentException("Default value [" + defaultIfEmpty
                  + "] is not a valid match for the given prompt type ["
                  + type.name() + "]");
      }

      String input;
      do
      {
         String query = " [" + defaultIfEmpty + "] ";
         input = prompt(message + query);
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
      if (this.isAcceptDefaults())
      {
         return defaultIfEmpty;
      }

      T result;
      do
      {
         String query = " [" + defaultIfEmpty + "] ";
         String value = promptWithCompleter(message + query, new CompleterAdaptor(
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
               result = promptChoiceTyped(message + query, Arrays.asList(type.getEnumConstants()),
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
      if (this.isAcceptDefaults())
      {
         return defaultIfEmpty;
      }

      FileResource<?> result = defaultIfEmpty;

      String query = " [ .../" + defaultIfEmpty.getName() + "] ";
      String path = promptWithCompleter(message + query, new FileNameCompleter());
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
      if (this.isAcceptDefaults())
      {
         return defaultIfEmpty;
      }

      if (!defaultIfEmpty.matches(pattern))
      {
         throw new IllegalArgumentException("Default value [" + defaultIfEmpty + "] does not match required pattern ["
                  + pattern + "]");
      }

      String input;
      do
      {
         String query = " [" + defaultIfEmpty + "] ";
         input = prompt(message + query);
         if ("".equals(input.trim()))
         {
            input = defaultIfEmpty;
         }
      }
      while (!input.matches(pattern));
      return input;
   }

   @Override
   public String promptSecret(final String message, final String defaultIfEmpty)
   {
      if (this.isAcceptDefaults())
      {
         return defaultIfEmpty;
      }

      String secret = promptSecret(message + " [ENTER for default]");

      if ((secret == null) || secret.trim().isEmpty())
         secret = defaultIfEmpty;

      return secret;
   }

   @Override
   public <T> Set<T> promptMultiSelect(String message, Set<T> options)
   {
      return promptMultiSelectWithWildcard(null, message, options);
   }

   @Override
   public <T> Set<T> promptMultiSelectWithWildcard(String wildcard, String message, Set<T> options)
   {
      final Map<String, T> optionMap = new LinkedHashMap<String, T>();
      for (T t : options)
      {
         if (t == null)
         {
            throw new IllegalArgumentException("null values not allowed");
         }
         // use #name() for enums, which may not be the same as #toString()
         optionMap.put(t.getClass().isEnum() ? ((Enum<?>) t).name() : t.toString(), t);
      }
      return promptMultiSelectWithWildcard(wildcard, message, optionMap);
   }

   @Override
   public <T> Set<T> promptMultiSelect(String message, Map<String, T> options)
   {
      return promptMultiSelectWithWildcard(null, message, options);
   }

   @Override
   public <T> Set<T> promptMultiSelect(String message, T... options)
   {
      return promptMultiSelect(message, new LinkedHashSet<T>(Arrays.asList(options)));
   }
   
   @Override
   public <T> Set<T> promptMultiSelectWithWildcard(String wildcard, String message, T... options)
   {
      return promptMultiSelectWithWildcard(wildcard, message, new LinkedHashSet<T>(Arrays.asList(options)));
   }
   
}
