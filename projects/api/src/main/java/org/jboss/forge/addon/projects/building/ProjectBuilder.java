/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.building;

import java.io.PrintStream;

import org.jboss.forge.addon.resource.Resource;

/**
 * Used to configure and execute the project build system.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProjectBuilder
{
   /**
    * Manually add an argument to be passed to the underlying build system.
    */
   ProjectBuilder addArguments(String... args);

   /**
    * Enable or disable test execution during build.
    */
   ProjectBuilder runTests(boolean test);

   /**
    * Profiles to be enabled for this build (ignored if not applicable to the project build system)
    */
   default ProjectBuilder profiles(String... profiles)
   {
      // Default implementation is a NOOP
      return this;
   }

   /**
    * Execute the build, returning the final product as a {@link Resource}.
    */
   Resource<?> build() throws BuildException;

   /**
    * Execute the build, returning the final product as a {@link Resource} and redirect the output to the provided
    * streams
    */
   Resource<?> build(PrintStream out, PrintStream err) throws BuildException;

   /**
    * Run in "quiet" mode (no logging output is displayed)
    */
   ProjectBuilder quiet(boolean quiet);

}
