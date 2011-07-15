/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

}
