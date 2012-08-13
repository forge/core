/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("setup")
@Topic("Project")
@RequiresProject
@Help("Setup a plugin.")
@Singleton
public class SetupPlugin implements Plugin
{

   @Inject
   private Shell shell;

   @Inject
   private SetupPluginCompleter completer;

   @Inject
   private PluginRegistry registry;

   @Inject
   @Current
   private Resource<?> resource;

   @DefaultCommand
   public void run(
            @Option(description = "The plugins to set up", required = true, completer = SetupPluginCompleter.class) final String[] plugins,
            final PipeOut out) throws Exception
   {
      for (String plugin : plugins)
      {
         if (completer.getCompletionTokens().contains(plugin))
         {
            shell.execute(plugin + " setup");
         }
         else if (registry.getPluginMetadataForScopeAndConstraints(plugin, shell) != null)
         {
            throw new RuntimeException("Plugin does not have a [setup] method.");
         }
         else
         {
            throw new RuntimeException("No such plugin [" + plugin
                     + "], or plugin not available for current Resource ["
                     + resource.getClass().getName() + "]");
         }
      }
   }
}