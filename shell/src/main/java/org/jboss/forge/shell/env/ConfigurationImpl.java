/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.shell.env;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationException;
import org.jboss.forge.env.ConfigurationScope;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.solder.unwraps.Unwraps;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@ApplicationScoped
public class ConfigurationImpl
{
   private Shell shell;
   private ForgeEnvironment environment;
   private ScopedConfigurationAdapter userConfig;
   private ScopedConfigurationAdapter projectConfig;
   private Project currentProject;

   public ConfigurationImpl()
   {
   }

   @Inject
   public ConfigurationImpl(final Shell shell)
   {
      this.shell = shell;
      this.environment = shell.getEnvironment();
   }

   @Unwraps
   public Configuration getConfiguration() throws ConfigurationException
   {
      Project project = shell.getCurrentProject();
      if ((project != null) && !project.equals(this.currentProject))
      {
         currentProject = project;
         ScopedConfigurationAdapter projectConfig = new ScopedConfigurationAdapter();
         XMLConfiguration projectLocalConfig;
         try
         {
            projectLocalConfig = new XMLConfiguration(getProjectSettings(project).getUnderlyingResourceObject());
            projectLocalConfig.setEncoding("UTF-8");
         }
         catch (org.apache.commons.configuration.ConfigurationException e)
         {
            throw new ConfigurationException(e);
         }
         projectLocalConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
         projectLocalConfig.setAutoSave(true);

         projectConfig.setScopedConfiguration(ConfigurationScope.PROJECT, new ConfigurationAdapter(projectConfig,
                  projectLocalConfig));
         projectConfig.setScopedConfiguration(ConfigurationScope.USER, getUserConfig());

         this.projectConfig = projectConfig;
         return projectConfig;
      }
      else if ((project != null) && project.equals(this.currentProject))
      {
         return projectConfig;
      }
      return getUserConfig();
   }

   public Configuration getUserConfig() throws ConfigurationException
   {
      // FIXME NPE caused when no project exists because config param is null
      if (userConfig == null)
      {
         XMLConfiguration globalXml;
         try
         {
            globalXml = new XMLConfiguration(environment.getUserConfiguration().getUnderlyingResourceObject());
            globalXml.setEncoding("UTF-8");
         }
         catch (org.apache.commons.configuration.ConfigurationException e)
         {
            throw new ConfigurationException(e);
         }
         globalXml.setReloadingStrategy(new FileChangedReloadingStrategy());
         globalXml.setAutoSave(true);
         userConfig = new ScopedConfigurationAdapter(ConfigurationScope.USER, new ConfigurationAdapter(globalXml));
      }
      return userConfig;
   }

   public FileResource<?> getProjectSettings(final Project project)
   {
      FileResource<?> settingsFile = project.getProjectRoot().getChild(".forge_settings").reify(FileResource.class);
      return settingsFile;
   }
}
