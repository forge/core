/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven;

import java.io.PrintStream;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingResult;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.ShellPrintWriter;

/**
 * Provides *DIRECT* access to a Project's Maven POM and Build artifacts. Should only be used by extremely low-level
 * operations.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public interface MavenCoreFacet extends Facet
{
   /**
    * Get the current Maven POM file.
    */
   public Model getPOM();

   /**
    * Get the current Maven POM file.
    */
   public FileResource<?> getPOMFile();

   /**
    * Set the current Maven POM file (overwriting any existing POM)
    */
   public void setPOM(Model pom);

   /**
    * Ask Maven to process this project's POM and return the resulting metadata. Do not build dependency hierarchy past
    * the immediate POM.
    * <p>
    * <b>**Warning!**</b> Calling this method has serious performance implications! Avoid whenever possible!
    */
   public ProjectBuildingResult getPartialProjectBuildingResult();

   /**
    * Ask Maven to process this project's POM and return the resulting metadata.
    * <p>
    * <b>**Warning!**</b> Calling this method has serious performance implications! Avoid whenever possible!
    */
   public ProjectBuildingResult getFullProjectBuildingResult();

   /**
    * Return the fully-resolved POM/{@link MavenProject} for this Maven enabled {@link Project}
    */
   public MavenProject getMavenProject();

   /**
    * Execute a command using the embedded Maven shell. Return the exit status code. 0 = success, anything else =
    * failure.
    */
   public boolean executeMavenEmbedded(String[] parameters);

   /**
    * Execute a command using the embedded Maven shell, using the given PrintStreams for output and error output. Return
    * the exit status code. 0 = success, anything else = failure.
    */
   boolean executeMavenEmbedded(PrintStream out, PrintStream err, String[] parameters);

   /**
    * Execute a command using the native Maven installation. If native Maven is not available, fall back to the embedded
    * Maven provider built in to Forge.
    *
    * @return
    */
   public boolean executeMaven(ShellPrintWriter out, String[] parameters);

   /**
    * Execute a command using the native Maven installation and given parameters. If native Maven is not available, fall
    * back to the embedded Maven provider built in to Forge.
    */
   public boolean executeMaven(String[] parameters);

   /**
    * Execute a command using the native Maven installation and given parameters. If native Maven is not available, fall
    * back to the embedded Maven provider built in to Forge.
    */
   public boolean executeMaven(List<String> parameters);

   /**
    * Get the location of the currently configured local maven repository.
    */
   public DirectoryResource getLocalRepositoryDirectory();

   /**
    * Resolve Maven ${properties} in the given value, and replace them with their computed values.
    */
   public String resolveProperties(String value);

}
