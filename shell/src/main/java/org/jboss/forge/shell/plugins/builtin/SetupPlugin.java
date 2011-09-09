/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
            @Option(description = "The plugin to set up",
                     required = true, completer = SetupPluginCompleter.class) final String plugin,
            final PipeOut out) throws Exception
   {
      if (completer.getCompletionTokens().contains(plugin))
      {
         shell.execute(plugin + " setup");
      }
      else if (registry.getPluginMetadataForScopeAndConstraints(plugin, shell) != null)
         throw new RuntimeException("Plugin does not have a [setup] method.");
      else
         throw new RuntimeException("No such plugin [" + plugin + "], or plugin not available for current Resource ["
                  + resource.getClass().getName() + "]");
   }

}
