/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectAssociationProvider;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ProjectListener;
import org.jboss.forge.addon.projects.ProjectLocator;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Predicate;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ProjectFactoryImpl implements ProjectFactory
{
   private static final Logger log = Logger.getLogger(ProjectFactoryImpl.class.getName());

   @Inject
   private AddonRegistry registry;

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private FacetFactory factory;

   private final List<ProjectListener> projectListeners = new ArrayList<ProjectListener>();

   @Override
   public Project findProject(FileResource<?> target)
   {
      return findProject(target, null);
   }

   @Override
   public Project findProject(FileResource<?> target, Predicate<Project> filter)
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
      Imported<ProjectLocator> instances = registry.getServices(ProjectLocator.class);
      for (ProjectLocator locator : instances)
      {
         DirectoryResource r = (target instanceof DirectoryResource) ? (DirectoryResource) target : target.getParent();
         while (r != null && result == null)
         {
            if (locator.containsProject(r))
            {
               result = locator.createProject(r);
               if (!filter.accept(result))
                  result = null;
            }

            r = r.getParent();
         }
         instances.release(locator);
      }

      if (result != null)
      {
         for (Class<ProjectFacet> instance : registry.getExportedTypes(ProjectFacet.class))
         {
            ProjectFacet facet = factory.create(result, instance);
            if (facet != null && factory.register(result, facet))
            {
               log.fine("Registered Facet [" + facet + "] into Project [" + result + "]");
            }
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
      Imported<ProjectLocator> instances = registry.getServices(ProjectLocator.class);
      for (ProjectLocator locator : instances)
      {
         result = locator.createProject(target);
         if (result != null)
            break;
         instances.release(locator);
      }

      if (result != null)
      {
         DirectoryResource parentDir = result.getProjectRoot().getParent().reify(DirectoryResource.class);
         if (parentDir != null)
         {
            Imported<ProjectAssociationProvider> locatorInstances = registry
                     .getServices(ProjectAssociationProvider.class);
            for (ProjectAssociationProvider provider : locatorInstances)
            {
               if (provider.canAssociate(result, parentDir))
               {
                  provider.associate(result, parentDir);
               }
               locatorInstances.release(provider);
            }
         }
      }

      if (result != null && facetTypes != null)
      {
         for (Class<? extends ProjectFacet> facetType : facetTypes)
         {
            try
            {
               factory.install(result, facetType);
            }
            catch (RuntimeException e)
            {
               throw new IllegalStateException("Could not install " + Facet.class.getSimpleName() + " of type ["
                        + facetType + "] into Project [" + result + "]");
            }
         }
      }

      if (result != null)
      {
         for (Class<ProjectFacet> instance : registry.getExportedTypes(ProjectFacet.class))
         {
            ProjectFacet facet = factory.create(result, instance);
            if (facet != null && factory.register(result, facet))
            {
               log.fine("Installed Facet [" + facet + "] into Project [" + result + "]");
            }
         }
      }

      if (result != null)
         fireProjectCreated(result);

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
   public Project createTempProject()
   {
      File rootDirectory = null;
      try
      {
         rootDirectory = File.createTempFile("forgeproject", ".tmp");
         rootDirectory.delete();
         rootDirectory.mkdirs();
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not create temp folder", e);
      }

      DirectoryResource addonDir = resourceFactory.create(DirectoryResource.class, rootDirectory);
      DirectoryResource projectDir = addonDir.createTempResource();
      projectDir.deleteOnExit();
      Project project = createProject(projectDir);
      return project;
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

   @Override
   public boolean containsProject(FileResource<?> target)
   {
      boolean result = false;
      DirectoryResource dir = (target instanceof DirectoryResource) ? (DirectoryResource) target : target.getParent();
      Imported<ProjectLocator> instances = registry.getServices(ProjectLocator.class);
      for (ProjectLocator locator : instances)
      {
         DirectoryResource r = dir;
         while (r != null && !result)
         {
            result = locator.containsProject(r);
            r = r.getParent();
         }
         if (result)
            break;
         instances.release(locator);
      }
      return result;
   }
}
