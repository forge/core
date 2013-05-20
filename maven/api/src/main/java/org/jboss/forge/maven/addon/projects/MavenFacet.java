/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.addon.projects;

import java.util.List;

import org.apache.maven.model.Model;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.maven.addon.resources.MavenPomResource;

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

   /**
    * Get the current Maven {@link Model} for this {@link Project}.
    */
   Model getPOM();

   /**
    * Set the current Maven {@link Model} for this {@link Project}.
    */
   void setPOM(Model pom);

   /**
    * Resolve Maven properties for the given input {@link String}, replacing occurrences of the `${property}` with their
    * defined value.
    */
   String resolveProperties(String value);

   /**
    * Execute Maven with the given arguments. Attempt to use any native installation of Maven before falling back to
    * Maven embedded. Return <code>true</code> on success or <code>false</code> on failure.
    */
   boolean executeMaven(List<String> parameters);

   /**
    * Execute Maven with the given arguments. Attempt to use any native installation of Maven before falling back to
    * Maven embedded. Return <code>true</code> on success or <code>false</code> on failure.
    */
   boolean executeMavenEmbedded(List<String> parameters);

   /**
    * Returns a {@link DirectoryResource} representing the location of the current local Maven repository.
    */
   DirectoryResource getLocalRepositoryDirectory();

}
