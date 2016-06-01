/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration;

import org.jboss.forge.addon.resource.FileResource;

/**
 * Configuration factory
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ConfigurationFactory
{
   /**
    * Returns the user configuration. Eg: ~/.forge/config.xml
    */
   Configuration getUserConfiguration();

   /**
    * Wraps the {@link FileResource} as a {@link Configuration} object
    */
   Configuration getConfiguration(FileResource<?> configFile);

}
