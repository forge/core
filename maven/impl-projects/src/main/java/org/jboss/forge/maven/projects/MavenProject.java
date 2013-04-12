/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.projects;

import org.jboss.forge.projects.AbstractProject;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.resource.DirectoryResource;

class MavenProject extends AbstractProject
{
   private final DirectoryResource projectRoot;

   public MavenProject(DirectoryResource projectRoot)
   {
      this.projectRoot = projectRoot;
   }

   @Override
   public boolean supports(ProjectFacet type)
   {
      return true;
   }

   @Override
   public DirectoryResource getProjectRoot()
   {
      return projectRoot;
   }

   @Override
   public String toString()
   {
      return projectRoot.toString();
   }

}
