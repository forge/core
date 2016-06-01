/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.javaee.rest.config.RestConfigurationStrategy;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.projects.Project;

/**
 * If installed, this {@link Project} supports features from the JAX-RS specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraints({
         @FacetConstraint(ServletFacet_3_0.class)
})
public interface RestFacet extends JavaEEFacet
{
   /**
    * Returns the activation type chosen for this facet
    */
   public RestConfigurationStrategy getConfigurationStrategy();

   /**
    * Returns the application path configured for this project
    * 
    * @return
    */
   public String getApplicationPath();

   /**
    * Set the configuration strategy for this RestFacet
    */
   public void setConfigurationStrategy(RestConfigurationStrategy configurationStrategy);
}
