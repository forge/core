/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration;

import javax.inject.Inject;

import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.FileResource;

/**
 * Provides configuration capabilities for a project
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ConfigurationFacetImpl extends AbstractFacet<Project> implements ConfigurationFacet
{
   private final ConfigurationFactory configurationFactory;
   private Configuration configuration;

   @Inject
   public ConfigurationFacetImpl(ConfigurationFactory configurationFactory)
   {
      this.configurationFactory = configurationFactory;
   }

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return true;
   }

   @Override
   public boolean uninstall()
   {
      getConfigLocation().delete();
      return true;
   }

   @Override
   public FileResource<?> getConfigLocation()
   {
      return getFaceted().getRoot().getChild(CONFIGURATION_FILE).reify(FileResource.class);
   }

   @Override
   public Configuration getConfiguration()
   {
      if (this.configuration == null)
      {
         this.configuration = configurationFactory.getConfiguration(getConfigLocation());
      }
      return this.configuration;
   }
}
