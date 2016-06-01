/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest;

import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategy;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategyFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;

/**
 * Base class for {@link RestFacet} implementations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractRestFacet extends AbstractJavaEEFacet implements RestFacet
{
   private RestConfigurationStrategy configurationStrategy;

   public AbstractRestFacet(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "JAX-RS";
   }

   @Override
   public boolean isInstalled()
   {
      return super.isInstalled() && getConfigurationStrategy() != null;
   }

   @Override
   public boolean install()
   {
      return super.install();
   }

   @Override
   public RestConfigurationStrategy getConfigurationStrategy()
   {
      if (configurationStrategy == null)
      {
         Project project = getFaceted();
         configurationStrategy = RestConfigurationStrategyFactory.from(project);
      }
      return configurationStrategy;
   }

   @Override
   public void setConfigurationStrategy(RestConfigurationStrategy strategy)
   {
      Project project = getFaceted();
      if (this.configurationStrategy != null)
      {
         this.configurationStrategy.uninstall(project);
      }
      strategy.install(project);
      this.configurationStrategy = strategy;
   }

   @Override
   public String getApplicationPath()
   {
      return getConfigurationStrategy() != null ? getConfigurationStrategy().getApplicationPath() : null;
   }
}