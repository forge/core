/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.command;

import java.util.List;
import java.util.Set;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;

/**
 * Defines a plugin.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface PluginMetadata
{
   /**
    * Return true if all constraints defined on this plugin (such as {@link RequiresProject} or {@link RequiresFacet})
    * are satisfied by the current project (if available) and {@link Shell#getCurrentResource()} scope.
    */
   boolean constrantsSatisfied(Shell shell);

   /**
    * Get a list of all commands defined by this plugin, regardless of the current {@link Shell#getCurrentResource()}
    * scope or constraints.
    */
   List<CommandMetadata> getAllCommands();

   /**
    * Get the command with the given name, if it exists in this plugin and is not overloaded in multiple
    * {@link Resource} scopes. If it does not exist, return null.
    */
   CommandMetadata getCommand(String name);

   /**
    * Get the command with the given name and {@link Resource} scope, if it exists in this plugin. If it does not exist,
    * return null.
    */
   CommandMetadata getCommand(String name, Class<? extends Resource<?>> scope);

   /**
    * Get the command with the given name if it exists in this plugin, and is available in the current
    * {@link Shell#getCurrentResource()} scope. If it does not exist, or is not in scope, return null.
    */
   CommandMetadata getCommand(String name, Shell shell);

   /**
    * Get a list of all commands defined by this plugin, if they are in scope.
    */
   List<CommandMetadata> getCommands();

   /**
    * For the {@link Shell#getCurrentResource()} scope, return a list of all available commands defined by this plugin.
    */
   List<CommandMetadata> getCommands(Shell shell);

   /**
    * Get the default command specified by this plugin. If none exists, return null.
    */
   CommandMetadata getDefaultCommand();

   /**
    * Get the help text for this plugin.
    */
   String getHelp();

   /**
    * Get the name by which this plugin is referenced on the command line.
    */
   String getName();

   /**
    * Get the {@link Resource} scopes for which this plugin is available.
    */
   Set<Class<? extends Resource<?>>> getResourceScopes();

   /**
    * Get the topic text for this plugin.
    */
   String getTopic();

   /**
    * Get the implementing {@link Plugin} class type for this {@link PluginMetadata}
    */
   Class<? extends Plugin> getType();

   /**
    * Return true if this plugin defines a command with the given name and {@link Resource} scope.
    */
   boolean hasCommand(String name, Class<? extends Resource<?>> scope);

   /**
    * Return true if this plugin defines a command with the given name in the current
    * {@link Shell#getCurrentResourceScope()}
    */
   boolean hasCommand(String name, Shell shell);

   /**
    * Return true if this plugin has any commands.
    */
   boolean hasCommands();

   /**
    * Return true if this plugin has a default command. Default commands are executed with the name of the plugin,
    * instead of the name of the command.
    */
   boolean hasDefaultCommand();

   /**
    * Return true if this plugin is overloaded in multiple {@link Resource} scopes.
    */
   boolean isCommandOverloaded(String name);

   /**
    * Return true if this plugin is usable in the given {@link Resource} scope
    */
   @SuppressWarnings("rawtypes")
   boolean usableWithScope(Class<? extends Resource> scope);

   /**
    * Return true if this plugin has a "setup" command. Setup commands should be exposed even if constraints have not
    * been satisfied.
    */
   boolean hasSetupCommand();

   /**
    * Get the "setup" command for this plugin. Return false if no "setup" command exists.
    */
   CommandMetadata getSetupCommand();

   /**
    * Return true if {@link #hasSetupCommand()} returns true, and it is available in the current scope.
    */
   boolean isSetupAvailable(Shell shell);
}
