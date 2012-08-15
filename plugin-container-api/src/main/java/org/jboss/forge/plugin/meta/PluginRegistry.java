/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.meta;

import java.util.List;
import java.util.Map;

import org.jboss.forge.plugin.Plugin;

/**
 * Contains the collection of all installed and available plugins.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface PluginRegistry
{
   /**
    * Add a {@link Plugin} to the registry.
    */
   void addPlugin(PluginMetadata plugin);

   /**
    * Get the {@link PluginMetadata} for the given {@link Plugin} name. Returns an empty list if no {@link Plugin}
    * exists for the given name.
    */
   List<PluginMetadata> getPluginMetadata(String plugin);

   /**
    * Get a map of all known {@link Plugin} names and {@link PluginMetadata}.
    */
   Map<String, List<PluginMetadata>> getPlugins();

   /**
    * Get the {@link Plugin} instance defined by the given {@link PluginMetadata}
    */
   Plugin instanceOf(PluginMetadata meta);
}
