/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.maven.projects.facets.MavenDependencyFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenMetadataFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenPackagingFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenResourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectLocator;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenProjectLocator implements ProjectLocator
{
   @Inject
   private FacetFactory factory;

   @Override
   public Project createProject(final DirectoryResource dir)
   {
      Project project = new MavenProject(dir);

      try
      {
         factory.install(project, MavenFacet.class);
         factory.install(project, MavenPluginFacet.class);
         factory.install(project, MavenMetadataFacet.class);
         factory.install(project, MavenPackagingFacet.class);
         factory.install(project, MavenDependencyFacet.class);
         factory.install(project, MavenResourceFacet.class);
      }
      catch (RuntimeException e)
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
