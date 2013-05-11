/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.projects.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.repositories.MutableAddonRepository;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.spi.ListenerRegistration;
import org.jboss.forge.container.util.Predicate;
import org.jboss.forge.facets.FacetFactory;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectAssociationProvider;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.ProjectListener;
import org.jboss.forge.projects.ProjectLocator;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.ResourceFactory;

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
   private Forge forge;

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

      if (result != null)
      {
         for (Class<ProjectFacet> instance : registry.getExportedTypes(ProjectFacet.class))
         {
            ProjectFacet facet = factory.create(instance, result);
            if (facet != null && facet.isInstalled() && result.install(facet))
            {
               log.fine("Installed Facet [" + facet + "] into Project [" + result + "]");
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
      for (ExportedInstance<ProjectLocator> instance : registry.getExportedInstances(ProjectLocator.class))
      {
         ProjectLocator locator = instance.get();
         result = locator.createProject(target);
         if (result != null)
            break;
      }

      if (result != null)
      {
         DirectoryResource parentDir = result.getProjectRoot().getParent().reify(DirectoryResource.class);
         if (parentDir != null)
         {
            for (ExportedInstance<ProjectAssociationProvider> providerInstance : registry
                     .getExportedInstances(ProjectAssociationProvider.class))
            {
               ProjectAssociationProvider provider = providerInstance.get();
               if (provider.canAssociate(result, parentDir))
               {
                  provider.associate(result, parentDir);
               }
            }
         }
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
         for (Class<ProjectFacet> instance : registry.getExportedTypes(ProjectFacet.class))
         {
            ProjectFacet facet = factory.create(instance, result);
            if (facet != null && facet.isInstalled() && result.install(facet))
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
      List<AddonRepository> repositories = forge.getRepositories();
      File rootDirectory = null;
      for (AddonRepository addonRepository : repositories)
      {
         if (addonRepository instanceof MutableAddonRepository)
         {
            rootDirectory = addonRepository.getRootDirectory();
         }
      }
      if (rootDirectory == null)
      {
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
      }
      DirectoryResource addonDir = resourceFactory.create(DirectoryResource.class, rootDirectory);
      DirectoryResource projectDir = addonDir.createTempResource();
      return createProject(projectDir);
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
