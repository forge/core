/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects;

import org.jboss.forge.addon.projects.AbstractProject;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;

class MavenProject extends AbstractProject
{
   private final Resource<?> root;

   public MavenProject(Resource<?> projectRoot)
   {
      this.root = projectRoot;
   }

   @Override
   public boolean supports(ProjectFacet type)
   {
      return true;
   }

   @Override
   public DirectoryResource getRootDirectory()
   {
      if (root instanceof DirectoryResource)
         return (DirectoryResource) root;
      throw new IllegalStateException("Project root [" + root + "] is not an instance of DirectoryResource");
   }

   @Override
   public Resource<?> getRoot()
   {
      return root;
   }

   @Override
   public String toString()
   {
      return root.toString();
   }

}
