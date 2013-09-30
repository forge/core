/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import org.jboss.forge.addon.projects.spi.ProjectCache;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.monitor.ResourceListener;
import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
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

   @Inject
   private Imported<ProjectListener> builtInListeners;

   @Inject
   private Imported<ProjectCache> caches;

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
         try
         {
            DirectoryResource r = (target instanceof DirectoryResource) ? (DirectoryResource) target : target
                     .getParent();
            while (r != null && result == null)
            {
               Iterator<ProjectCache> cacheIterator = caches.iterator();
               while (cacheIterator.hasNext() && result == null)
               {
                  ProjectCache cache = cacheIterator.next();
                  try
                  {
                     result = cache.get(r);
                     if (result != null && !filter.accept(result))
                        result = null;
                     if (result != null)
                        break;
                  }
                  finally
                  {
                     caches.release(cache);
                  }
               }

               if (result == null && locator.containsProject(r))
               {
                  result = locator.createProject(r);
                  if (result != null && !filter.accept(result))
                     result = null;

                  if (result != null)
                  {
                     registerAvailableFacets(result);
                     cacheProject(result);
                  }
               }

               r = r.getParent();
            }
         }
         finally
         {
            instances.release(locator);
         }

         if (result != null)
            break;
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
         registerAvailableFacets(result);
      }

      if (result != null)
      {
         cacheProject(result);
         fireProjectCreated(result);
      }

      return result;
   }

   private void registerAvailableFacets(Project result)
   {
      for (Class<ProjectFacet> type : registry.getExportedTypes(ProjectFacet.class))
      {
         Iterable<ProjectFacet> facets = factory.createFacets(result, type);
         for (ProjectFacet facet : facets)
         {
            if (facet != null && factory.register(result, facet))
            {
               log.fine("Registered Facet [" + facet + "] into Project [" + result + "]");
            }
         }
      }
   }

   private void cacheProject(final Project project)
   {
      for (ProjectCache cache : caches)
      {
         try
         {
            cache.store(project);
         }
         finally
         {
            caches.release(cache);
         }
      }

      final ResourceMonitor monitor = project.getProjectRoot().monitor();
      monitor.addResourceListener(new ResourceListener()
      {
         @Override
         public void processEvent(ResourceEvent event)
         {
            for (ProjectCache cache : caches)
            {
               try
               {
                  cache.evict(project);
               }
               finally
               {
                  caches.release(cache);
               }
            }
            monitor.cancel();
         }
      });
   }

   private void fireProjectCreated(Project project)
   {
      for (ProjectListener listener : builtInListeners)
      {
         listener.projectCreated(project);
      }
      for (ProjectListener listener : projectListeners)
      {
         listener.projectCreated(project);
      }
   }

   @Override
   public Project createTempProject()
   {
      return createTempProject(Collections.<Class<? extends ProjectFacet>> emptySet());
   }

   @Override
   public Project createTempProject(Iterable<Class<? extends ProjectFacet>> facetTypes)
   {
      File rootDirectory = OperatingSystemUtils.createTempDir();
      DirectoryResource addonDir = resourceFactory.create(DirectoryResource.class, rootDirectory);
      DirectoryResource projectDir = addonDir.createTempResource();
      projectDir.deleteOnExit();
      Project project = createProject(projectDir, facetTypes);
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
   public boolean containsProject(DirectoryResource bound, FileResource<?> target)
   {
      Assert.notNull(bound, "Boundary should not be null");
      Assert.isTrue(isParent(bound, target), "Target should be a child of bound");
      boolean result = false;
      DirectoryResource dir = (target instanceof DirectoryResource) ? (DirectoryResource) target : target.getParent();
      Imported<ProjectLocator> instances = registry.getServices(ProjectLocator.class);
      for (ProjectLocator locator : instances)
      {
         DirectoryResource r = dir;
         while (r != null && !result)
         {
            result = locator.containsProject(r);
            if (bound.equals(r))
            {
               break;
            }
            r = r.getParent();
         }
         if (result)
            break;
         instances.release(locator);
      }
      return result;
   }

   private boolean isParent(DirectoryResource dir, FileResource<?> child)
   {
      DirectoryResource childDir = (child instanceof DirectoryResource) ? child.reify(DirectoryResource.class) : child
               .getParent();
      while (childDir != null)
      {
         if (dir.equals(childDir))
         {
            return true;
         }
         childDir = childDir.getParent();
      }
      return false;
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

   @Override
   public void invalidateCaches()
   {
      for (ProjectCache cache : caches)
      {
         try
         {
            cache.invalidate();
         }
         finally
         {
            caches.release(cache);
         }
      }
   }
}
