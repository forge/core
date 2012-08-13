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

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.command.PluginRegistry;

/**
 * @author Lincoln
 * 
 */
public class PluginResolverCompleter implements CommandCompleter
{
   @Inject
   private Shell shell;

   @Inject
   private PluginRegistry registry;

   @Override
   public void complete(final CommandCompleterState st)
   {
      PluginCommandCompleterState state = ((PluginCommandCompleterState) st);
      Queue<String> tokens = state.getTokens();

      if (!tokens.isEmpty())
      {
         String pluginName = tokens.remove();
         PluginMetadata plugin = registry.getPluginMetadataForScopeAndConstraints(pluginName, shell);

         if ((plugin != null))
         {
            // found a plugin match directly
            state.setPlugin(plugin);
            if (tokens.isEmpty())
            {
               // there's only a plugin so far
               if (state.isFinalTokenComplete())
               {
                  // they chose this plugin, start at the end for command
                  // completion
                  state.setIndex(state.getBuffer().length());
               }
               else
               {
                  state.getCandidates().add(pluginName + " ");
                  // they haven't yet chosen a plugin, start at the beginning
                  state.setIndex(0);
               }
            }
         }
         else
         {
            // no plugin found, but we have a partial name with which to attempt
            // suggestion
            state.setIndex(0);
            if (tokens.isEmpty())
            {
               List<String> pluginCandidates = getPluginCandidates(registry, pluginName);
               state.getCandidates().addAll(pluginCandidates);
               // TODO add file completion candidates
            }
            else
            {
               // bad input, must always begin with a plugin
               // try to add file completion
            }
         }
      }
      else
      {
         state.setIndex(0);
         List<String> pluginCandidates = getPluginCandidates(registry, "");
         state.getCandidates().addAll(pluginCandidates);
      }
   }

   private List<String> getPluginCandidates(final PluginRegistry registry, final String pluginBase)
   {
      Map<String, List<PluginMetadata>> plugins = registry.getPlugins();

      List<String> results = new ArrayList<String>();
      for (Entry<String, List<PluginMetadata>> entry : plugins.entrySet())
      {
         for (PluginMetadata pluginMeta : entry.getValue())
         {
            if (pluginMeta.hasCommands())
            {
               String pluginName = pluginMeta.getName();
               if (PluginCommandCompleter.isPotentialMatch(pluginName, pluginBase)
                        && (pluginMeta.constrantsSatisfied(shell) || pluginMeta.isSetupAvailable(shell)))
               {
                  results.add(pluginName + " ");
               }
            }
         }
      }

      return results;
   }

}
