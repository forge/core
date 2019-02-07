/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Model;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;
import org.jboss.forge.addon.projects.building.BuildResult;
import org.jboss.forge.addon.resource.DirectoryResource;

/**
 * A {@link ProjectFacet} adding support for the Maven build system.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface MavenFacet extends ProvidedProjectFacet
{
   /**
    * Get the {@link MavenModelResource} for this {@link Project}.
    */
   MavenModelResource getModelResource();

   /**
    * Get the current Maven {@link Model} for this {@link Project}.
    */
   Model getModel();

   /**
    * Get the effective Maven {@link Model} for this {@link Project}.
    */
   Model getEffectiveModel();

   /**
    * Returns the {@link BuildResult} of the effective {@link Model} for this project.
    * <p/>
    * It is not affected by the builds executed in {@link #executeBuild(String...)}
    */
   BuildResult getEffectiveModelBuildResult();

   /**
    * Set the current Maven {@link Model} for this {@link Project}.
    */
   void setModel(Model pom);

   /**
    * Get a {@link Map} of all resolvable project properties.
    */
   Map<String, String> getProperties();

   /**
    * Resolve Maven properties for the given input {@link String}, replacing occurrences of the `${property}` with their
    * defined value.
    */
   String resolveProperties(String value);

   /**
    * Execute Maven with the given arguments. Attempt to use any native installation of Maven before falling back to
    * Maven embedded.
    * 
    * @return <code>true</code> on success or <code>false</code> on failure.
    */
   boolean executeMaven(List<String> parameters);

   /**
    * Execute embedded Maven with the given arguments. Redirects the output and error output to the provided
    * {@link PrintStream}s
    *
    * @return <code>true</code> on success or <code>false</code> on failure.
    */
   boolean executeMaven(List<String> parameters,final PrintStream out, final PrintStream err);

   /**
    * Execute Maven with the given arguments.
    * 
    * @return <code>true</code> on success or <code>false</code> on failure.
    */
   boolean executeMavenEmbedded(List<String> parameters);

   /**
    * Execute embedded Maven with the given arguments. Redirects the output and error output to the provided
    * {@link PrintStream}s
    * 
    * @return <code>true</code> on success or <code>false</code> on failure.
    */
   boolean executeMavenEmbedded(List<String> parameters, final PrintStream out, final PrintStream err);

   /**
    * Returns a {@link DirectoryResource} representing the location of the current local Maven repository.
    */
   DirectoryResource getLocalRepositoryDirectory();

   /**
    * Returns <code>true</code> if the underlying {@link Project} {@link Model} is in a build-able state.
    */
   boolean isModelValid();

}
