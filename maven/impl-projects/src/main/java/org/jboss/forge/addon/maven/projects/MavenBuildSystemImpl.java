/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.maven.projects.facets.MavenDependencyFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenMetadataFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenPackagingFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenResourcesFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenWebResourcesFacet;
import org.jboss.forge.addon.projects.BuildSystemFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenBuildSystemImpl implements MavenBuildSystem
{
   private static final Logger log = Logger.getLogger(MavenBuildSystemImpl.class.getName());

   @Inject
   private FacetFactory factory;

   @Override
   public String getType()
   {
      return "Maven";
   }

   @Override
   public Project createProject(final DirectoryResource dir)
   {
      Project project = new MavenProject(dir);

      try
      {
         factory.install(project, MavenFacetImpl.class);
         factory.install(project, MavenPluginFacet.class);
         factory.install(project, MavenMetadataFacet.class);
         factory.install(project, MavenPackagingFacet.class);
         factory.install(project, MavenDependencyFacet.class);
         factory.install(project, MavenResourcesFacet.class);
         try
         {
            factory.register(project, MavenWebResourcesFacet.class);
         }
         catch (IllegalStateException e)
         {
            log.log(Level.FINE, "Could not install [" + MavenWebResourcesFacet.class.getName() + "] into project ["
                     + project + "]", e);
         }
      }
      catch (RuntimeException e)
      {
         throw new IllegalStateException("Could not install Maven into Project located at ["
                  + dir.getFullyQualifiedName() + "]", e);
      }

      return project;
   }

   @Override
   public boolean containsProject(final DirectoryResource dir)
   {
      Resource<?> pom = dir.getChild("pom.xml");
      return pom.exists() && pom instanceof FileResource;
   }

   @Override
   public Set<Class<? extends BuildSystemFacet>> getProvidedFacetTypes()
   {
      Set<Class<? extends BuildSystemFacet>> result = new HashSet<Class<? extends BuildSystemFacet>>();
      result.add(MavenFacet.class);
      result.add(MavenPluginFacet.class);
      result.add(DependencyFacet.class);
      result.add(MetadataFacet.class);
      result.add(PackagingFacet.class);
      result.add(ResourcesFacet.class);

      return Collections.unmodifiableSet(result);
   }

   @Override
   public int priority()
   {
      return 0;
   }
}
