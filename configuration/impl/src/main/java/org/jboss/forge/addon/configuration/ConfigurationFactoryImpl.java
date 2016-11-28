/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration;

import java.io.File;
import java.io.IOException;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

@Singleton
public class ConfigurationFactoryImpl implements ConfigurationFactory
{
   static final String USER_CONFIG_PATH = "org.jboss.forge.addon.configuration.USER_CONFIG_PATH";

   private Configuration userConfiguration;

   @Inject
   private Furnace furnace;

   @Produces
   Configuration getUserConfiguration(InjectionPoint ip) throws ConfigurationException
   {
      Configuration config = getUserConfiguration();
      if (ip != null)
      {
         Annotated annotated = ip.getAnnotated();
         if (annotated.isAnnotationPresent(Subset.class))
         {
            config = config.subset(annotated.getAnnotation(Subset.class).value());
         }
      }
      return config;
   }

   @Override
   public Configuration getUserConfiguration() throws ConfigurationException
   {
      if (furnace.isTestMode())
         setupTemporaryUserConfig();

      if (userConfiguration == null)
      {
         String property = System.getProperty(USER_CONFIG_PATH);
         File userConfigurationFile;
         if (property == null || property.isEmpty())
         {
            userConfigurationFile = new File(OperatingSystemUtils.getUserForgeDir(), "config.properties");
         }
         else
         {
            userConfigurationFile = new File(property);
         }
         if (!userConfigurationFile.exists() || userConfigurationFile.length() == 0L)
         {
            File parentFile = userConfigurationFile.getParentFile();
            if (parentFile != null)
            {
               parentFile.mkdirs();
            }
            try
            {
               userConfigurationFile.createNewFile();
            }
            catch (IOException e)
            {
               throw new ConfigurationException(
                        "Error while creating user configuration file: " + userConfigurationFile, e);
            }
         }
         userConfiguration = getConfiguration(userConfigurationFile);
      }
      return userConfiguration;
   }

   @Override
   public Configuration getConfiguration(FileResource<?> configFile)
   {
      return getConfiguration(configFile.getUnderlyingResourceObject());
   }

   private Configuration getConfiguration(File file)
   {
      try
      {
         PropertiesConfiguration commonsConfig = new PropertiesConfiguration(file);
         commonsConfig.setEncoding("UTF-8");
         commonsConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
         commonsConfig.setAutoSave(true);
         return new ConfigurationAdapter(commonsConfig);
      }
      catch (org.apache.commons.configuration.ConfigurationException e)
      {
         throw new ConfigurationException("Error while creating configuration from " + file, e);
      }
   }

   private void setupTemporaryUserConfig()
   {
      if (System.getProperty(USER_CONFIG_PATH) == null)
      {
         File tmpFile;
         try
         {
            tmpFile = File.createTempFile("user_config", ".properties");
            System.setProperty(USER_CONFIG_PATH, tmpFile.getAbsolutePath());
            tmpFile.deleteOnExit();
         }
         catch (IOException e)
         {
            throw new IllegalStateException("Cannot create temp file", e);
         }
      }
   }
}
