/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.env;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
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
import org.jboss.forge.shell.squelch.ConfigAdapterQualifierLiteral;
import org.jboss.forge.shell.util.BeanManagerUtils;
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
   private BeanManager bm;

   public ConfigurationImpl()
   {
   }

   @Inject
   public ConfigurationImpl(final Shell shell, BeanManager bm)
   {
      this.bm = bm;
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

         ConfigurationAdapter adapter = BeanManagerUtils.getContextualInstance(bm, ConfigurationAdapter.class,
                  new ConfigAdapterQualifierLiteral());
         adapter.setParent(projectConfig);
         adapter.setDelegate(projectLocalConfig);
         adapter.setBeanManager(bm);
         projectConfig.setScopedConfiguration(ConfigurationScope.PROJECT, adapter);
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

         ConfigurationAdapter adapter = BeanManagerUtils.getContextualInstance(bm, ConfigurationAdapter.class,
                  new ConfigAdapterQualifierLiteral());
         adapter.setDelegate(globalXml);
         adapter.setBeanManager(bm);
         userConfig = new ScopedConfigurationAdapter(ConfigurationScope.USER, adapter);
      }
      return userConfig;
   }

   public FileResource<?> getProjectSettings(final Project project)
   {
      FileResource<?> settingsFile = project.getProjectRoot().getChild(".forge_settings").reify(FileResource.class);
      return settingsFile;
   }
}
