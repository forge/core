/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.completer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.shell.console.jline.console.completer.Completer;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class PluginCommandCompleter implements Completer
{

   List<CommandCompleter> completers = new ArrayList<CommandCompleter>();

   private String lastBuffer = null;

   private final CompletedCommandHolder optionHolder;

   @Inject
   public PluginCommandCompleter(final PluginResolverCompleter plugin,
            final CommandResolverCompleter command,
            final OptionResolverCompleter option,
            final OptionValueResolverCompleter value,
            final CompletedCommandHolder optionHolder)
   {
      this.optionHolder = optionHolder;
      completers.add(plugin);
      completers.add(command);
      completers.add(option);
      completers.add(value);
   }

   @Override
   public int complete(final String buffer, final int cursor, final List<CharSequence> candidates)
   {
      optionHolder.setState(null);

      PluginCommandCompleterState state = new PluginCommandCompleterState(buffer, lastBuffer, cursor);

      // TODO replace lastBuffer with a lastState object?
      lastBuffer = buffer;

      for (CommandCompleter c : completers)
      {
         if (!state.hasSuggestions())
         {
            c.complete(state);
         }
      }

      candidates.addAll(state.getCandidates());

      // ensure the completer is triggered always
      if ((state.getPlugin() != null) && state.isFinalTokenComplete() && !state.hasSuggestions()
               && state.isDuplicateBuffer() && state.getCandidates().isEmpty())
      {
         candidates.add("");
      }

      optionHolder.setState(state);

      return state.getIndex();
   }

   /**
    * Add option completions for the given command, with or without argument tokens
    */
   public static boolean isPotentialMatch(final String full, final String partial)
   {
      return full.matches("(?i)" + partial + ".*");
   }

}
