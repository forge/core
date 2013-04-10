/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.projects.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.spi.ListenerRegistration;
import org.jboss.forge.container.util.Predicate;
import org.jboss.forge.facets.FacetFactory;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.ProjectListener;
import org.jboss.forge.projects.ProjectLocator;
import org.jboss.forge.resource.DirectoryResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ProjectFactoryImpl implements ProjectFactory
{
   @Inject
   private AddonRegistry registry;

   @Inject
   private FacetFactory factory;

   private final List<ProjectListener> projectListeners = new ArrayList<ProjectListener>();

   @Override
   public Project findProject(DirectoryResource target)
   {
      return findProject(target, null);
   }

   @Override
   public Project findProject(DirectoryResource target, Predicate<Project> filter)
   {
      if (filter == null)
      {
         filter = new Predicate<Project>()
         {
            @Override
            public boolean accept(Project type)
            {
               return true;
            }
         };
      }

      Project result = null;
      for (ExportedInstance<ProjectLocator> instance : registry.getExportedInstances(ProjectLocator.class))
      {
         DirectoryResource r = target;
         while (r != null && result == null)
         {
            ProjectLocator locator = instance.get();
            if (locator.containsProject(target))
            {
               result = locator.createProject(target);
               if (!filter.accept(result))
                  result = null;
            }

            r = (r.getParent() == null ? null : r.getParent().reify(DirectoryResource.class));
         }
      }
      return result;
   }

   @Override
   public Project createProject(DirectoryResource projectDir)
   {
      return createProject(projectDir, null);
   }

   @Override
   public Project createProject(DirectoryResource target, Iterable<Class<? extends ProjectFacet>> facetTypes)
   {
      Project result = null;
      for (ExportedInstance<ProjectLocator> instance : registry.getExportedInstances(ProjectLocator.class))
      {
         ProjectLocator locator = instance.get();
         result = locator.createProject(target);
         if (result != null)
            break;
      }

      if (result != null && facetTypes != null)
      {
         for (Class<? extends ProjectFacet> facetType : facetTypes)
         {
            ProjectFacet facet = factory.create(facetType, result);
            if (!result.install(facet))
            {
               throw new IllegalStateException("Could not install Facet [" + facet + "] of type [" + facetType
                        + "] into Project [" + result + "]");
            }
         }
      }

      if (result != null)
      {
         fireProjectCreated(result);
      }
      return result;
   }

   private void fireProjectCreated(Project project)
   {
      for (ProjectListener listener : projectListeners)
      {
         listener.projectCreated(project);
      }
   }

   @Override
   public ListenerRegistration<ProjectListener> addProjectListener(final ProjectListener listener)
   {
      projectListeners.add(listener);
      return new ListenerRegistration<ProjectListener>()
      {
         @Override
         public ProjectListener removeListener()
         {
            projectListeners.remove(listener);
            return listener;
         }
      };
   }
}
