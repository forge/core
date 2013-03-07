/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects;

import org.jboss.forge.projects.BaseProject;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.projects.ProjectLocator;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.FileResource;
import org.jboss.forge.resource.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenProjectLocator implements ProjectLocator
{
   @Override
   public Project createProject(final DirectoryResource dir)
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

      MavenFacetImpl mavenFacetImpl = new MavenFacetImpl();
      mavenFacetImpl.setOrigin(project);
      if (!project.install(mavenFacetImpl))
      {
         throw new IllegalStateException("Could not install Maven into Project located at ["
                  + dir.getFullyQualifiedName() + "]");
      }

      return project;
   }

   @Override
   public boolean containsProject(final DirectoryResource dir)
   {
      Resource<?> pom = dir.getChild("pom.xml");
      return pom.exists() && pom instanceof FileResource;
   }

}
