/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.meta;

import java.util.List;

import org.jboss.forge.plugin.Plugin;
import org.jboss.forge.plugin.SetupCommand;

/**
 * Describes a {@link Plugin}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface PluginMetadata
{

   /**
    * Get the command with the given name. If it does not exist, return null.
    */
   CommandMetadata getCommand(String name);

   /**
    * Get a list of all commands defined by this {@link Plugin}.
    */
   List<CommandMetadata> getCommands();

   /**
    * Get the default command specified by this {@link Plugin}. If none exists, return null.
    */
   CommandMetadata getDefaultCommand();

   /**
    * Get the help text for this {@link Plugin}.
    */
   String getHelp();

   /**
    * Get the name by which this {@link Plugin} is referenced.
    */
   String getName();

   /**
    * Get the topic text for this {@link Plugin}.
    */
   String getTopic();

   /**
    * Get the implementing {@link Plugin} class type for this {@link PluginMetadata}
    */
   Class<? extends Plugin> getType();

   /**
    * Return true if this {@link Plugin} defines a command with the given name.
    */
   boolean hasCommand(String name);

   /**
    * Return true if this {@link Plugin} has any commands.
    */
   boolean hasCommands();

   /**
    * Return true if this {@link Plugin} has a default command. Default commands are executed with the name of the
    * {@link Plugin}, instead of the name of the command.
    */
   boolean hasDefaultCommand();

   /**
    * Return true if this {@link Plugin} has a {@link SetupCommand}. The {@link SetupCommand} should be exposed even if
    * constraints have not been satisfied.
    */
   boolean hasSetupCommand();

   /**
    * Get the "setup" command for this plugin. Return false if no "setup" command exists.
    */
   CommandMetadata getSetupCommand();
}
