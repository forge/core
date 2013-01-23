/*
 * Copyright 2012-2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.completer.CommandCompleter;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ShellPrompt
{

   /**
    * Prompt for user input, and return as a String.
    */
   String prompt();

   /**
    * This works like prompt, however when a CR is received, the method returns the value, but does not produce a CR in
    * the actual terminal buffer. This is useful when a plugin is using manual cursor control.
    * 
    * @return
    */
   String promptAndSwallowCR();

   /**
    * Prompt for user input, first printing the given line, then return user input as a String.
    */
   String prompt(String message);

   /**
    * Prompt for user input, first printing the given line, then return user input as a String.
    * 
    * @param defaultIfEmpty The value to be returned when an empty or whitespace-only user input is read.
    */
   String prompt(String message, String defaultIfEmpty);

   /**
    * Prompt for user input, first printing the given message, then return user input cast to the type provided.
    */
   <T> T prompt(String message, Class<T> clazz);

   /**
    * Prompt for boolean user input (Y/n), first printing the given message, then returning user input as a boolean. The
    * value returned will default to <code>true</code> if an empty or whitespace-only user input is read.
    */
   boolean promptBoolean(String string);

   /**
    * Prompt for boolean user input (Y/n), first printing the given message, then returning user input as a boolean.
    * 
    * @param defaultIfEmpty The value to be returned when an empty or whitespace-only user input is read.
    */
   boolean promptBoolean(String message, boolean defaultIfEmpty);

   /**
    * Prompt for user input, first printing the given message, followed by an enumerated list of options (printing the
    * String value of each item in the list.) Loop until the user enters a number corresponding to one of the options,
    * then return the index of that option from the list.
    * 
    * @param message The prompt message to display until valid input is entered
    * @param options The list of selection options
    * @return the index of selected option
    */
   int promptChoice(String message, Object... options);

   /**
    * Prompt for user input, first printing the given message, followed by an enumerated list of options (printing the
    * String value of each item in the list.) Loop until the user enters a number corresponding to one of the options,
    * then return the index of that option from the list.
    * 
    * @param message The prompt message to display until valid input is entered
    * @param options The list of selection options
    * @return the index of the selected option
    */
   int promptChoice(String message, List<?> options);

   /**
    * Prompt for user input, first printing the given message, followed by an enumerated list of options (printing the
    * String value of each item in the list.) Loop until the user enters a number corresponding to one of the options,
    * then return that option from the list.
    * 
    * @param <T> The type of the objects contained in the list
    * @param message The prompt message to display until valid input is entered
    * @param options The list of selection options
    * @return the selected option
    */
   <T> T promptChoiceTyped(String message, List<T> options);

   /**
    * Prompt for user input, first printing the given message, followed by an enumerated list of options (printing the
    * String value of each item in the list.) Loop until the user enters a number corresponding to one of the options,
    * then return that option from the list.
    * 
    * @param <T> The type of the objects contained in the list
    * @param message The prompt message to display until valid input is entered
    * @param options The list of selection options
    * @param defaultIfEmpty The value to be returned when an empty or whitespace-only user input is read.
    * @return the selected option
    */
   <T> T promptChoiceTyped(String message, List<T> options, T defaultIfEmpty);

   /**
    * Prompt for user input, first printing the given message, followed by a keyed list of options. Loop until the user
    * enters a key corresponding to one of the options, then return the value of that option from the map.
    * 
    * @param <T> The type of the objects contained in the map
    * @param message The prompt message to display until valid input is entered
    * @param options The map of selection options
    * @return the selected option
    */
   <T> T promptChoice(String message, Map<String, T> options);

   /**
    * Prompt for user input, first printing the given message, then returning user input as a String. The prompt will
    * repeat until input matching the prompt type is entered.
    * 
    * @param message The prompt message to display until valid input is entered
    * @param type The prompt type to which valid input must be matched
    */
   String promptCommon(String message, PromptType type);

   /**
    * Prompt for user input in the form of an {@link Enum}, first printing the given message, then returning user input
    * as a {@link Enum}. The prompt will repeat until input matching the prompt type is entered.
    * 
    * @param message The prompt message to display until valid input is entered
    */
   <T extends Enum<T>> T promptEnum(String message, Class<T> type);

   /**
    * Prompt for user input in the form of an {@link Enum}, first printing the given message, then returning user input
    * as a {@link Enum}. The prompt will repeat until input matching the prompt type is entered.
    * 
    * @param message The prompt message to display until valid input is entered
    * @param defaultIfEmpty The value to be returned when an empty or whitespace-only user input is read.
    */
   <T extends Enum<T>> T promptEnum(String message, Class<T> type, T defaultIfEmpty);

   /**
    * Prompt for user input in the form of a file path, first printing the given message, then returning user input as a
    * File. The prompt will repeat until input matching the prompt type is entered.
    * 
    * @param message The prompt message to display until valid input is entered
    */
   FileResource<?> promptFile(String message);

   /**
    * Prompt for user input in the form of a file path, first printing the given message, then returning user input as a
    * File. The prompt will repeat until input matching the prompt type is entered.
    * 
    * @param message The prompt message to display until valid input is entered
    * @param defaultIfEmpty The value to be returned when an empty or whitespace-only user input is read.
    */
   FileResource<?> promptFile(String message, FileResource<?> defaultIfEmpty);

   /**
    * Same as {@link #promptCommon(String, PromptType)}, but will default to a given value if user input is empty.
    * 
    * @param message The prompt message to display until valid input is entered
    * @param type The prompt type to which valid input must be matched
    * @param defaultIfEmpty The value to be returned when an empty or whitespace-only user input is read.
    */
   String promptCommon(String message, PromptType type, String defaultIfEmpty);

   /**
    * Prompt for user input (Y/n), first printing the given message, then returning user input as a String. The prompt
    * will repeat until input matching the regular expression is entered.
    * 
    * @param message The prompt message to display until valid input is entered
    * @param regex The regular expression to which valid input must be matched
    */
   String promptRegex(String message, String regex);

   /**
    * Same as {@link #promptRegex(String, String)}, but will default to a given value if user input is empty.
    * 
    * @param message The prompt message to display until valid input is entered
    * @param pattern The regular expression to which valid input must be matched
    * @param defaultIfEmpty The value to be returned when an empty or whitespace-only user input is read.
    */
   String promptRegex(String message, String pattern, String defaultIfEmpty);

   /**
    * Prompt for user input, first printing the given line, then returning user input as a converted value.
    * 
    * @param clazz The type to which the value will be converted, if possible.
    * @param defaultIfEmpty The value to be returned when an empty or whitespace-only user input is read.
    */
   <T> T prompt(String message, Class<T> clazz, T defaultIfEmpty);

   /**
    * Prompt for user input, first printing the given line, then returning user input with help from the given completer
    * type.
    * 
    * @param message The prompt message to display until valid input is entered
    * @param type The command completer type to instantiate and use during completion
    */
   String promptCompleter(String string, Class<? extends CommandCompleter> type);

   /**
    * First print the given message, prompt the user for input (masking keystrokes for secrecy,) then return user input.
    */
   String promptSecret(String message);

   /**
    * First print the given message, prompt the user for input (masking keystrokes for secrecy,) then return user input.
    * 
    * @param defaultIfEmpty The value to be returned when an empty or whitespace-only user input is read.
    */
   String promptSecret(String string, String defaultIfEmpty);

   /**
    * Prompt for user input, first printing the given message, followed by a keyed list of options. Loop until the user
    * has selected all available options or signifies he is finished by entering an empty response, then return a set
    * of all values selected.
    * 
    * @param <T> The type of the objects to be selected
    * @param message The prompt message to display
    * @param options The set of selection options
    * @return the selected options
    */
   <T> Set<T> promptMultiSelect(String message, Set<T> options);

   /**
    * Prompt for user input, first printing the given message, followed by a keyed list of options, including a
    * "wildcard" option to select all. Loop until the user has selected all available options or signifies he is
    * finished by entering an empty response, then return a set of all values selected.
    * 
    * @param <T> The type of the objects to be selected
    * @param wildcard the shortcut response signifying "select all options", not {@code null}
    * @param message The prompt message to display
    * @param options The set of selection options
    * @return the selected options
    */
   <T> Set<T> promptMultiSelectWithWildcard(String wildcard, String message, Set<T> options);

   /**
    * Prompt for user input, first printing the given message, followed by a keyed list of options. Loop until the user
    * signifies he is finished by entering an empty response, then return a set of all values selected.
    * 
    * @param <T> The type of the objects to be selected
    * @param message The prompt message to display
    * @param options The array of unique selection options
    * @return the selected options
    */
   <T> Set<T> promptMultiSelect(String message, T... options);

   /**
    * Prompt for user input, first printing the given message, followed by a keyed list of options, including a
    * "wildcard" option to select all. Loop until the user has selected all available options or signifies he is
    * finished by entering an empty response, then return a set of all values selected.
    * 
    * @param <T> The type of the objects to be selected
    * @param wildcard the shortcut response signifying "select all options", not {@code null}
    * @param message The prompt message to display
    * @param options The array of unique selection options
    * @return the selected options
    */
   <T> Set<T> promptMultiSelectWithWildcard(String wildcard, String message, T... options);

   /**
    * Prompt for user input, first printing the given message, followed by a keyed list of options. Loop until the user
    * signifies he is finished by entering an empty response, then return a set of all values selected.
    * 
    * @param <T> The type of the objects to be selected
    * @param message The prompt message to display
    * @param options The map of selection options
    * @return the selected options
    */
   <T> Set<T> promptMultiSelect(String message, Map<String, T> options);

   /**
    * Prompt for user input, first printing the given message, followed by a keyed list of options, including a
    * "wildcard" option to select all. Loop until the user has selected all available options or signifies he is
    * finished by entering an empty response, then return a set of all values selected.
    * 
    * @param <T> The type of the objects to be selected
    * @param wildcard the shortcut response signifying "select all options", not {@code null}
    * @param message The prompt message to display
    * @param options The map of selection options
    * @return the selected options
    */
   <T> Set<T> promptMultiSelectWithWildcard(String wildcard, String message, Map<String, T> options);
}
