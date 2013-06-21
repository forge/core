/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.configuration;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

public class ConfigurationProducer
{
   @Produces
   @ApplicationScoped
   public Configuration getUserConfig() throws ConfigurationException
   {
      File userConfigurationFile = new File(OperatingSystemUtils.getUserForgeDir(), "config.xml");
      return readConfig(userConfigurationFile);
   }

   /**
    * Creates a configuration based on the specified file
    * 
    * @param config
    * @return
    * @throws ConfigurationException
    */
   static Configuration readConfig(File config) throws ConfigurationException
   {
      XMLConfiguration globalXml;
      try
      {
         globalXml = new XMLConfiguration(config);
         globalXml.setEncoding("UTF-8");
         globalXml.setReloadingStrategy(new FileChangedReloadingStrategy());
         globalXml.setAutoSave(true);

         return new ConfigurationAdapter(globalXml);
      }
      catch (org.apache.commons.configuration.ConfigurationException e)
      {
         throw new ConfigurationException(e);
      }

   }
}
