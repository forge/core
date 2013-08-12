package org.jboss.forge.addon.configuration;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.services.Exported;

/**
 * Configuration factory
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Exported
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
