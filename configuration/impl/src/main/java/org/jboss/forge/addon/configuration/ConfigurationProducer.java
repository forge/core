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
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

@ApplicationScoped
public class ConfigurationProducer
{

   private ScopedConfigurationAdapter userConfig;
   private ScopedConfigurationAdapter projectConfig;

   @Produces
   // public Configuration getConfiguration() throws ConfigurationException
   // {
   // FileResource<?> projectSettings = getProjectSettings();
   // if ((project != null) && !project.equals(this.currentProject))
   // {
   // ScopedConfigurationAdapter projectConfig = new ScopedConfigurationAdapter();
   // XMLConfiguration projectLocalConfig;
   // try
   // {
   // projectLocalConfig = new XMLConfiguration(getProjectSettings().getUnderlyingResourceObject());
   // projectLocalConfig.setEncoding("UTF-8");
   // }
   // catch (org.apache.commons.configuration.ConfigurationException e)
   // {
   // throw new ConfigurationException(e);
   // }
   // projectLocalConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
   // projectLocalConfig.setAutoSave(true);
   //
   // ConfigurationAdapter adapter = new ConfigurationAdapter(projectConfig, projectLocalConfig);
   // projectConfig.setScopedConfiguration(ConfigurationScope.PROJECT, adapter);
   // projectConfig.setScopedConfiguration(ConfigurationScope.USER, getUserConfig());
   //
   // this.projectConfig = projectConfig;
   // return projectConfig;
   // }
   // else if ((project != null) && project.equals(this.currentProject))
   // {
   // return projectConfig;
   // }
   // return getUserConfig();
   // }
   //
   public Configuration getUserConfig() throws ConfigurationException
   {
      // FIXME NPE caused when no project exists because config param is null
      if (userConfig == null)
      {
         XMLConfiguration globalXml;
         try
         {
            File userConfigurationFile = new File(OperatingSystemUtils.getUserForgeDir(), "config.xml");
            globalXml = new XMLConfiguration(userConfigurationFile);
            globalXml.setEncoding("UTF-8");
         }
         catch (org.apache.commons.configuration.ConfigurationException e)
         {
            throw new ConfigurationException(e);
         }
         globalXml.setReloadingStrategy(new FileChangedReloadingStrategy());
         globalXml.setAutoSave(true);

         ConfigurationAdapter adapter = new ConfigurationAdapter(null, globalXml);
         userConfig = new ScopedConfigurationAdapter(ConfigurationScope.USER, adapter);
      }
      return userConfig;
   }

   private FileResource<?> getProjectSettings()
   {
      final Project project = null;
      FileResource<?> settingsFile = project.getProjectRoot().getChild(".forge_settings").reify(FileResource.class);
      return settingsFile;
   }

}
