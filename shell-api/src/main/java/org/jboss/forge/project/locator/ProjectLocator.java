/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.locator;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;

/**
 * Locates project root directories, and creates instances of projects for that type.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProjectLocator
{
   /**
    * Attempt to locate a project root directory in the given folder. If found, return the project; otherwise, return
    * null;
    */
   public Project createProject(DirectoryResource dir);

   /**
    * Return true if the given {@link DirectoryResource} contains a project. Otherwise, return false.
    */
   boolean containsProject(DirectoryResource dir);
}
