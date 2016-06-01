/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl.facets;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ClassLoaderFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;

/**
 * Implementation of the {@link ClassLoaderFacet} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(DependencyFacet.class)
public class ClassLoaderFacetImpl extends AbstractFacet<Project> implements
         ClassLoaderFacet
{
   private static final Logger log = Logger
            .getLogger(ClassLoaderFacetImpl.class.getName());

   @Override
   public boolean install()
   {
      return isInstalled();
   }

   @Override
   public boolean isInstalled()
   {
      return true;
   }

   @Override
   public URLClassLoader getClassLoader(List<? extends FileResource<?>> compiledResources)
   {
      Project project = getFaceted();
      DependencyFacet facet = project.getFacet(DependencyFacet.class);
      List<Dependency> effectiveDependencies = facet
               .getEffectiveDependencies();
      List<URL> urls = new ArrayList<>();
      // Add project dependencies
      for (Dependency dependency : effectiveDependencies)
      {
         FileResource<?> artifact = dependency.getArtifact();
         if (artifact != null)
         {
            File artifactFile = artifact.getUnderlyingResourceObject();
            try
            {
               urls.add(artifactFile.toURI().toURL());
            }
            catch (MalformedURLException e)
            {
               log.warning("Error while configuring Reflections: "
                        + e.getMessage());
            }
         }
      }
      for (FileResource<?> compiledResource : compiledResources)
      {
         File artifact = compiledResource.getUnderlyingResourceObject();
         try
         {
            urls.add(artifact.toURI().toURL());
         }
         catch (MalformedURLException e)
         {
            log.warning("Error while configuring Reflections: "
                     + e.getMessage());
         }
      }
      // Project Classloader. May introduce memory leaks if not closed properly
      URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]));
      return urlClassLoader;
   }

   @Override
   public URLClassLoader getClassLoader()
   {
      // Add project build
      PackagingFacet packagingFacet = getFaceted().getFacet(PackagingFacet.class);
      Resource<?> finalArtifact = packagingFacet.getFinalArtifact();
      if (!finalArtifact.exists())
      {
         // Force build
         finalArtifact = packagingFacet.createBuilder().quiet(true).build();
      }

      return getClassLoader(Arrays.<FileResource<?>> asList(finalArtifact.reify(FileResource.class)));
   }

}
