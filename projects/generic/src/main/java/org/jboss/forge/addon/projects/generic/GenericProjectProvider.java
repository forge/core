/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.generic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.AbstractProjectProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;
import org.jboss.forge.addon.projects.generic.facets.GenericMetadataFacet;
import org.jboss.forge.addon.projects.generic.facets.GenericProjectFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Abstract class for {@link ProjectProvider} implementations that create {@link GenericProject} projects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class GenericProjectProvider extends AbstractProjectProvider
{
   @Override
   public String getType()
   {
      return "None";
   }

   @Override
   public Iterable<Class<? extends ProvidedProjectFacet>> getProvidedFacetTypes()
   {
      Set<Class<? extends ProvidedProjectFacet>> result = new HashSet<>();
      result.add(GenericProjectFacet.class);
      result.add(GenericMetadataFacet.class);
      return Collections.unmodifiableSet(result);
   }

   @Override
   public Project createProject(Resource<?> target)
   {
      Project project = new GenericProject(target);
      FacetFactory factory = SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
      factory.install(project, GenericProjectFacet.class);
      factory.install(project, GenericMetadataFacet.class);
      return project;
   }

   @Override
   public boolean containsProject(final Resource<?> target)
   {
      if (target.exists())
      {
         Resource<?> projectMetadata = target.getChild(GenericMetadataFacet.PROJECT_METADATA_FILE_NAME);
         return projectMetadata != null && projectMetadata.exists();
      }
      return false;
   }

   @Override
   public int priority()
   {
      return Integer.MAX_VALUE;
   }
}
