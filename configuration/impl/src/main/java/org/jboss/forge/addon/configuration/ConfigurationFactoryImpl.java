package org.jboss.forge.addon.configuration;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

@ApplicationScoped
public class ConfigurationFactoryImpl implements ConfigurationFactory
{
   private Configuration userConfiguration;

   @Override
   @Produces
   @ApplicationScoped
   public Configuration getUserConfiguration() throws ConfigurationException
   {
      if (userConfiguration == null)
      {
         File userConfigurationFile = new File(OperatingSystemUtils.getUserForgeDir(), "config.xml");
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
      XMLConfiguration commonsConfig;
      try
      {
         commonsConfig = new XMLConfiguration(file);
         commonsConfig.setEncoding("UTF-8");
         commonsConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
         commonsConfig.setAutoSave(true);

         return new ConfigurationAdapter(commonsConfig);
      }
      catch (org.apache.commons.configuration.ConfigurationException e)
      {
         throw new ConfigurationException(e);
      }
   }

}
