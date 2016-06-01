/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration.facets;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.FileResource;

/**
 * Returns the configuration for a given project
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface ConfigurationFacet extends ProjectFacet
{
   /**
    * The name of the configuration file created inside the project
    */
   public static final String CONFIGURATION_FILE = ".forge_settings";

   /**
    * Returns the current configuration for this project.
    */
   public Configuration getConfiguration();

   /**
    * Returns the current configuration location for this project.
    */
   public FileResource<?> getConfigLocation();

}
