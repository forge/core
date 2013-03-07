/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects;

import org.jboss.forge.maven.resources.MavenPomResource;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;

/**
 * A {@link ProjectFacet} adding support for the Maven build system.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface MavenFacet extends ProjectFacet
{
   /**
    * Get the {@link MavenPomResource} for this {@link Project}.
    */
   MavenPomResource getPomResource();

}
