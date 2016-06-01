/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.config;

import org.jboss.forge.addon.javaee.rest.RestFacet;
import org.jboss.forge.addon.projects.Project;

/**
 * A {@link RestConfigurationStrategy} allows a {@link RestFacet} to be configured properly
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface RestConfigurationStrategy
{
   /**
    * Returns the application path
    */
   public String getApplicationPath();

   /**
    * Installs this rest configuration in the project
    */
   public void install(Project project);

   /**
    * Un-installs this rest configuration in the project
    */
   public void uninstall(Project project);

}
