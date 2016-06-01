/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.logmanager.ConfigurationLocator;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class LoggingConfigurationLocator implements ConfigurationLocator
{
   static final FilenameFilter LOGGING_CONFIG_FILTER = new FilenameFilter()
   {
      @Override
      public boolean accept(final File dir, final String name)
      {
         return name.equals("logging.properties");
      }
   };

   @Override
   public InputStream findConfiguration() throws IOException
   {
      // First look for the property
      final String propLoc = System.getProperty("logging.configuration");
      if (propLoc != null)
         try
         {
            return new URL(propLoc).openStream();
         }
         catch (IOException e)
         {
            System.err.printf("Unable to read the logging configuration from '%s' (%s)%n", propLoc, e);
         }
      File[] files = null;
      // Second attempt to find the configuration in the users .forge directory
      final File userForgeDir = OperatingSystemUtils.getUserForgeDir();
      // Look for a logging.properties file
      if (userForgeDir.isDirectory())
      {
         files = userForgeDir.listFiles(LOGGING_CONFIG_FILTER);
         if (files != null && files.length > 0)
         {
            return new FileInputStream(files[0]);
         }
      }
      // Finally default to $FORGE_HOME/logging.properties
      final File forgeHomeDir = OperatingSystemUtils.getForgeHomeDir();
      // Look for a logging.properties file
      if (forgeHomeDir != null && forgeHomeDir.isDirectory())
      {
         files = forgeHomeDir.listFiles(LOGGING_CONFIG_FILTER);
      }
      // If the file was found, return it, otherwise return null
      if (files != null && files.length > 0)
      {
         return new FileInputStream(files[0]);
      }
      System.err.println("No logging configuration was found.");
      return null;
   }
}
