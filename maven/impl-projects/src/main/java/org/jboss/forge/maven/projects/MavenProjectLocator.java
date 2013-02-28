/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.projects.BaseProject;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.ProjectLocator;
import org.jboss.forge.projects.ProjectType;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.FileResource;
import org.jboss.forge.resource.Resource;

/**
 * Locate a Maven project starting in the current directory, and progressing up the chain of parent directories until a
 * project is found, or the root directory is found. If a project is found, return the {@link File} referring to the
 * directory containing that project, or return null if no projects were found.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenProjectLocator implements ProjectLocator
{
   private ProjectFactory factory;

   @Inject
   public MavenProjectLocator(final ProjectFactory factory)
   {
      this.factory = factory;
   }

   @Override
   public Project createProject(final DirectoryResource dir, ProjectType type)
   {
      Project project = new BaseProject()
      {
         @Override
         public boolean supports(ProjectFacet type)
         {
            return true;
         }

         @Override
         public Resource<?> getProjectRoot()
         {
            return dir;
         }
      };

      project.install(new MavenFacetImpl());

      Iterable<Class<? extends ProjectFacet>> requiredFacets = type.getRequiredFacets();

      return project;
   }

   @Override
   public boolean containsProject(final Resource<?> dir)
   {
      Resource<?> pom = dir.getChild("pom.xml");
      return pom.exists() && pom instanceof FileResource;
   }

}
