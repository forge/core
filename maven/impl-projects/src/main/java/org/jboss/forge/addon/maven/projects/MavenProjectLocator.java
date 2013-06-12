/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.maven.dependencies.MavenDependencyResolver;
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
   private MavenDependencyResolver resolver;

   @Inject
   private FacetFactory factory;

   @Override
   public Project createProject(final DirectoryResource dir)
   {
      Project project = new MavenProject(dir);

      try
      {
         factory.install(MavenFacet.class, project);
         factory.install(MavenPluginFacet.class, project);
         factory.install(MavenMetadataFacet.class, project);
         factory.install(MavenPackagingFacet.class, project);
         factory.install(MavenDependencyFacet.class, project);
         factory.install(MavenResourceFacet.class, project);
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
