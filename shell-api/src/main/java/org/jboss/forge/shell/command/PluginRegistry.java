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
