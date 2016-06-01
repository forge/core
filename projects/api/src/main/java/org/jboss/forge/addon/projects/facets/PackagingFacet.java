/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.facets;

import org.jboss.forge.addon.projects.ProvidedProjectFacet;
import org.jboss.forge.addon.projects.building.BuildResult;
import org.jboss.forge.addon.projects.building.ProjectBuilder;
import org.jboss.forge.addon.resource.Resource;

/**
 * A Facet representing this project's Packaging (JAR, WAR, EAR, etc...)
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public interface PackagingFacet extends ProvidedProjectFacet
{
   /**
    * Set the packaging type currently in use by this project. For example, JAR, WAR,... etc.
    */
   void setPackagingType(String type);

   /**
    * Get the packaging type currently in use by this project. For example, JAR, WAR,... etc.
    */
   String getPackagingType();

   /**
    * Return the resource representing the fully built output artifact of this project. For example, if the project
    * builds a JAR file, this method must return the {@link Resource} representing that JAR file.
    */
   Resource<?> getFinalArtifact();

   /**
    * Return a new {@link ProjectBuilder} instance. This object is responsible for executing a build with custom
    * options.
    */
   ProjectBuilder createBuilder();

   /**
    * Trigger the underlying build system to perform a build with the given arguments or flags.
    *
    * @return The final build artifact if building succeeded, otherwise return null
    * @see {@link #getFinalArtifact()}
    */
   Resource<?> executeBuild(String... args);

   /**
    * Get the final name of this project's build output artifact. This represents the name without file extension.
    */
   String getFinalName();

   /**
    * Set the final name of this project's build output artifact. This represents the name without file extension.
    */
   void setFinalName(String finalName);

   /**
    * Returns the current {@link BuildResult} of this project. Runs a build if it was not executed previously.
    * <p/>
    * It is not affected by the builds executed in {@link #executeBuild(String...)}
    */
   BuildResult getBuildResult();

}
