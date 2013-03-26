/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.maven.projects.facets.MavenDependencyFacet;
import org.jboss.forge.maven.projects.facets.MavenMetadataFacet;
import org.jboss.forge.maven.projects.facets.MavenPackagingFacet;
import org.jboss.forge.maven.projects.facets.MavenResourceFacet;
import org.jboss.forge.projects.AbstractProject;
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
      Project project = new AbstractProject()
      {
         @Override
         public boolean supports(ProjectFacet type)
         {
            return true;
         }

         @Override
         public DirectoryResource getProjectRoot()
         {
            return dir;
         }

         @Override
         public String toString()
         {
            return dir.toString();
         }
      };

      List<Class<? extends ProjectFacet>> result = new ArrayList<Class<? extends ProjectFacet>>();
      result.add(MavenFacetImpl.class);
      result.add(MavenMetadataFacet.class);
      result.add(MavenPackagingFacet.class);
      result.add(MavenDependencyFacet.class);
      result.add(MavenResourceFacet.class);
      
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
