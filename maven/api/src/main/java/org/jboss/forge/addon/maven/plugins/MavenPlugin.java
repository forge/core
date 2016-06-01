/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

import java.util.List;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;

/**
 * Represents a Maven plugin
 * 
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */

public interface MavenPlugin extends PluginElement
{
   /**
    * Returns the {@link Coordinate} represented by this plugin.
    */
   Coordinate getCoordinate();

   /**
    * Returns the {@link Configuration} associated with this plugin, if any. (Never null.)
    */
   Configuration getConfig();

   /**
    * Return the list of {@link Execution} associated with this plugin, if any.
    */
   List<Execution> listExecutions();

   /**
    * @return if this plugin extensions is enabled
    */
   boolean isExtensionsEnabled();

   /**
    * @return the dependencies for this plugin, if any.
    */
   List<Dependency> getDirectDependencies();

}
