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
package org.jboss.forge.env;

import javax.inject.Inject;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.solder.unwraps.Unwraps;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationImpl
{
   private final Shell shell;
   private final ForgeEnvironment environment;

   @Inject
   public ConfigurationImpl(final Shell shell)
   {
      this.shell = shell;
      this.environment = shell.getEnvironment();
   }

   @Unwraps
   public Configuration getProjectConfiguration() throws ConfigurationException
   {
      Project project = shell.getCurrentProject();
      ScopedConfigurationAdapter config = new ScopedConfigurationAdapter();
      if (project != null)
      {
         XMLConfiguration forgeXml = new XMLConfiguration(getProjectSettings(project).getUnderlyingResourceObject());
         forgeXml.setReloadingStrategy(new FileChangedReloadingStrategy());
         forgeXml.setAutoSave(true);
         config.setScopedConfiguration(ConfigurationScope.PROJECT,
                  new ConfigurationAdapter(config, forgeXml));
         config.setScopedConfiguration(ConfigurationScope.USER, getGlobalConfig(config));
         return config;
      }
      return getGlobalConfig(config);
   }

   private Configuration getGlobalConfig(final ScopedConfigurationAdapter config) throws ConfigurationException
   {
      XMLConfiguration globalXml = new XMLConfiguration(environment.getUserConfiguration()
               .getUnderlyingResourceObject());
      globalXml.setReloadingStrategy(new FileChangedReloadingStrategy());
      globalXml.setAutoSave(true);
      return new ConfigurationAdapter(config, globalXml);
   }

   public FileResource<?> getProjectSettings(final Project project)
   {
      FileResource<?> settingsFile = project.getProjectRoot().getChild(".forge_settings").reify(FileResource.class);
      return settingsFile;
   }
}
