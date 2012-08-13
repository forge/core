/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.build;

import org.jboss.forge.resources.Resource;

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
    * Execute the build.
    */
   Resource<?> build() throws BuildException;

}
