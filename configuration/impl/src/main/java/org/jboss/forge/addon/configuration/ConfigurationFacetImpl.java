/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.configuration;

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

   private Configuration configuration;

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
      return getFaceted().getProjectRoot().getChild(".forge_settings").reify(FileResource.class);
   }

   @Override
   public Configuration getConfiguration()
   {
      if (this.configuration == null)
      {
         this.configuration = ConfigurationProducer.readConfig(getConfigLocation().getUnderlyingResourceObject());
      }
      return this.configuration;
   }
}
