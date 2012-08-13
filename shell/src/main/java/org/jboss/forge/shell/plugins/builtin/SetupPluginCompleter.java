/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SetupPluginCompleter extends SimpleTokenCompleter
{
   @Inject
   private PluginRegistry registry;

   @Inject
   private Shell shell;

   @Override
   public List<Object> getCompletionTokens()
   {
      List<Object> results = new ArrayList<Object>();

      for (List<PluginMetadata> list : registry.getPlugins().values())
      {
         for (PluginMetadata p : list)
         {
            CommandMetadata command = p.getCommand("setup", shell);
            if ((command != null) && !results.contains(p.getName()))
            {
               results.add(p.getName());
            }
         }
      }

      return results;
   }
}
