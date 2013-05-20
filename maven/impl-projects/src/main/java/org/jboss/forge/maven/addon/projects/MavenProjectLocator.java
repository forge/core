/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.addon.projects;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectLocator;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourceFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.maven.addon.dependencies.MavenDependencyResolver;
import org.jboss.forge.maven.addon.projects.MavenFacet;
import org.jboss.forge.maven.addon.projects.MavenPluginFacet;
import org.jboss.forge.maven.addon.projects.facets.MavenDependencyFacet;
import org.jboss.forge.maven.addon.projects.facets.MavenMetadataFacet;
import org.jboss.forge.maven.addon.projects.facets.MavenPackagingFacet;
import org.jboss.forge.maven.addon.projects.facets.MavenResourceFacet;

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
