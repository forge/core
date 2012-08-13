/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.command;

import java.util.List;
import java.util.Map;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;

/**
 * Contains the collection of all installed and available plugins.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface PluginRegistry
{
   /**
    * Add a plugin to the registry. Typically this will only be used by Forge itself.
    */
   void addPlugin(PluginMetadata plugin);

   /**
    * Get the {@link PluginMetadata} for the given plugin name. Returns an empty list if no plugins exist for the given
    * name.
    */
   List<PluginMetadata> getPluginMetadata(String plugin);

   /**
    * Resolves a single {@link PluginMetadata} instance representing the singular type that is in scope, and satisfied
    * by the current project constraints such as {@link RequiresProject} or {@link RequiresFacet}
    */
   PluginMetadata getPluginMetadataForScopeAndConstraints(String name, Shell shell);

   /**
    * Get a map of all known plugin names and metadata.
    */
   Map<String, List<PluginMetadata>> getPlugins();

   /**
    * Get the {@link Plugin} instance defined by the given {@link PluginMetadata}
    */
   Plugin instanceOf(PluginMetadata meta);
}
