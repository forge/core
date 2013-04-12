/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects;

import javax.inject.Inject;

import org.jboss.forge.facets.FacetFactory;
import org.jboss.forge.maven.dependencies.MavenDependencyResolver;
import org.jboss.forge.maven.projects.facets.MavenDependencyFacet;
import org.jboss.forge.maven.projects.facets.MavenMetadataFacet;
import org.jboss.forge.maven.projects.facets.MavenPackagingFacet;
import org.jboss.forge.maven.projects.facets.MavenResourceFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectLocator;
import org.jboss.forge.projects.facets.DependencyFacet;
import org.jboss.forge.projects.facets.MetadataFacet;
import org.jboss.forge.projects.facets.PackagingFacet;
import org.jboss.forge.projects.facets.ResourceFacet;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.FileResource;
import org.jboss.forge.resource.Resource;

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

      MavenFacet mavenFacet = factory.create(MavenFacet.class, project);
      MavenPluginFacet mavenPluginFacet = factory.create(MavenPluginFacet.class, project);
      MetadataFacet metadataFacet = factory.create(MavenMetadataFacet.class, project);
      PackagingFacet packagingFacet = factory.create(MavenPackagingFacet.class, project);
      DependencyFacet dependencyFacet = factory.create(MavenDependencyFacet.class, project);
      ResourceFacet resourceFacet = factory.create(MavenResourceFacet.class, project);

      if (!(project.install(mavenFacet)
               && project.install(metadataFacet)
               && project.install(packagingFacet)
               && project.install(dependencyFacet)
               && project.install(resourceFacet)
               && project.install(mavenPluginFacet)

      ))
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
