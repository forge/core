/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.completer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.OptionMetadata;
import org.jboss.forge.shell.command.parser.CommandParser;
import org.jboss.forge.shell.command.parser.CommandParserContext;
import org.jboss.forge.shell.command.parser.CompositeCommandParser;
import org.jboss.forge.shell.command.parser.NamedBooleanOptionParser;
import org.jboss.forge.shell.command.parser.NamedValueOptionParser;
import org.jboss.forge.shell.command.parser.NamedValueVarargsOptionParser;
import org.jboss.forge.shell.command.parser.OrderedValueOptionParser;
import org.jboss.forge.shell.command.parser.OrderedValueVarargsOptionParser;

public class OptionResolverCompleter implements CommandCompleter
{
   private final CommandParser commandParser = new CompositeCommandParser(
            new NamedBooleanOptionParser(),
            new NamedValueOptionParser(),
            new NamedValueVarargsOptionParser(),
            new OrderedValueOptionParser(),
            new OrderedValueVarargsOptionParser());

   @Override
   public void complete(final CommandCompleterState st)
   {
      PluginCommandCompleterState state = ((PluginCommandCompleterState) st);

      CommandMetadata command = state.getCommand();
      if (command != null)
      {
         if (command.hasOptions())
         {
            getOptionCandidates(command, state);
         }
      }
   }

   private void getOptionCandidates(final CommandMetadata command, final PluginCommandCompleterState state)
   {
      ArrayList<String> results = new ArrayList<String>();
      Queue<String> tokens = state.getTokens();
      CommandParserContext context = new CommandParserContext();
      context.setCompleting(true);
      context.setFinalTokenComplete(state.isFinalTokenComplete());
      commandParser.parse(state.getCommand(), tokens,
               context);
      state.setCommandContext(context);
      Map<OptionMetadata, Object> valueMap = context.getValueMap();
      List<OptionMetadata> options = command.getOptions();

      if (tokens.isEmpty())
      {
         if (state.isFinalTokenComplete())
         {
            if ((context.isEmpty() || context.isLastOptionValued()))
            {
               for (OptionMetadata option : options)
               {
                  if (!valueMap.containsKey(option))
                  {
                     if (option.isNamed() && option.isRequired())
                     {
                        results.clear();
                        results.add("--" + option.getName() + " ");
                        break;
                     }
                     else if (option.isNamed())
                     {
                        results.add("--" + option.getName() + " ");
                     }
                     else if (!option.isNamed() && !option.isPipeOut() && !option.isPipeIn())
                     {
                        state.setOption(option);
                        break;
                     }
                  }
               }
            }
            else if (!context.isEmpty() && !context.isLastOptionValued())
            {
               state.setOption(context.getLastParsed());
            }
         }
         else
         {
            if (!state.isFinalTokenComplete() && !context.isEmpty())
            {
               state.setOption(context.getLastParsed());

               if (context.isLastOptionValued())
               {
                  state.getTokens().add(context.getLastParsedToken());
               }
            }
         }
      }
      else
      {
         String finalToken = tokens.peek();
         int finalTokenIndex = state.getBuffer().lastIndexOf(finalToken);

         boolean tailOptionValued = true;
         boolean finalTokenIsValue = false;
         if (!finalToken.startsWith("-"))
         {
            finalTokenIsValue = true;
            finalTokenIndex = state.getIndex();
         }
         else
         {
            boolean shortOption = finalToken.matches("^-[^\\-]+$")
                     && (finalToken.length() > 1);
            finalToken = finalToken.replaceFirst("^[-]+", "");
            for (Entry<OptionMetadata, Object> entry : valueMap.entrySet())
            {
               OptionMetadata option = entry.getKey();
               if (entry.getValue() == null)
               {
                  tailOptionValued = false;
               }
               if (((option.getShortName().equals(finalToken) && shortOption) || option.getName().equals(
                        finalToken)))
               {
                  state.setOption(option);
               }
            }

            if (tailOptionValued)
            {
               for (OptionMetadata option : options)
               {
                  if (option.isNamed())
                  {
                     if (((option.getShortName().equals(finalToken) && shortOption) || option.getName().equals(
                              finalToken)) && valueMap.containsKey(option))
                     {
                        if (!state.isFinalTokenComplete())
                        {
                           results.add(" ");
                        }
                        break;
                     }
                     if (option.getName().startsWith(finalToken) && !valueMap.containsKey(option))
                     {
                        results.add("--" + option.getName() + " ");
                     }
                  }
               }
            }

            if (!results.isEmpty())
            {
               tokens.remove();
            }

            /*
             * If we haven't gotten any suggestions yet, then we just need to add everything we haven't seen yet. First
             * time - just the required options.
             */
            if (results.isEmpty() && tailOptionValued)
            {
               for (OptionMetadata option : options)
               {
                  if (option.isNamed() && !valueMap.containsKey(option))
                  {
                     if (option.isRequired() || state.isDuplicateBuffer())
                        results.add("--" + option.getName() + " ");
                  }
               }
            }

         }

         // add to state
         if (!state.isFinalTokenComplete() && finalTokenIsValue)
         {
            state.setOption(context.getLastParsed());
            results.clear();
            if (state.isDuplicateBuffer())
            {
               // wait until the buffer remains the same until we append a space
               results.add(" ");
            }
         }
         else if (!results.isEmpty() && tailOptionValued)
         {
            state.setIndex(finalTokenIndex);
         }
      }
      state.getCandidates().addAll(results);
   }
}
