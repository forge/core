/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.observers;

import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.events.CommandMissing;
import org.jboss.forge.shell.exceptions.NoSuchCommandException;
import org.jboss.forge.shell.plugins.builtin.AliasRegistry;

/**
 * An observer that suggests possible commands if the issued command was not found.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class DidYouMeanObserver
{

   private static final int LETTERS_NEEDED_TO_BE_REPLACED = 2;

   @Inject
   private PluginRegistry pluginRegistry;

   @Inject
   private AliasRegistry aliasRegistry;

   /**
    * Did you mean ?
    */
   public void suggestMissingPlugin(@Observes CommandMissing commandMissing, Shell shell)
   {
      String pluginName = commandMissing.getOriginalStatement().split(" ")[0];
      // Find similar plugins
      Set<String> similarPlugins = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
      for (String plugin : pluginRegistry.getPlugins().keySet())
      {
         if (Strings.getLevenshteinDistance(pluginName, plugin) < LETTERS_NEEDED_TO_BE_REPLACED)
         {
            similarPlugins.add(plugin);
         }
      }
      for (String alias : aliasRegistry.getAliases().keySet())
      {
         if (Strings.getLevenshteinDistance(pluginName, alias) < LETTERS_NEEDED_TO_BE_REPLACED)
         {
            similarPlugins.add(alias);
         }
      }

      if (similarPlugins.isEmpty())
      {
         throw new NoSuchCommandException(null, "No such command: "
                  + commandMissing.getOriginalStatement());
      }
      else
      {
         ShellMessages.error(shell, "No such command: " + pluginName);
         if (similarPlugins.size() == 1)
         {
            shell.println("Did you mean this ?");
         }
         else
         {
            shell.println("Did you mean any of these ?");
         }
         for (String plugin : similarPlugins)
         {
            shell.println(ShellColor.BOLD, "\t" + plugin);
         }
      }
   }

}
