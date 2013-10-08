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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.BuildSystem;
import org.jboss.forge.addon.projects.BuildSystemFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectAssociationProvider;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ProjectListener;
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

   private Predicate<ProjectFacet> notBuildSystemFilter = new Predicate<ProjectFacet>()
   {
      @Override
      public boolean accept(ProjectFacet type)
      {
         return !(type instanceof BuildSystemFacet);
      }
   };

   private final List<ProjectListener> projectListeners = new ArrayList<ProjectListener>();

   private final Predicate<Project> acceptsAllProjects = new Predicate<Project>()
   {
      @Override
      public boolean accept(Project type)
      {
         return true;
      }
   };

   @Override
   public Project findProject(FileResource<?> target)
   {
      Project result = null;
      Imported<BuildSystem> instances = registry.getServices(BuildSystem.class);
      for (BuildSystem buildSystem : instances)
      {
         try
         {
            result = findProject(target, buildSystem);
         }
         finally
         {
            instances.release(buildSystem);
         }

         if (result != null)
            break;
      }
      return result;
   }

   @Override
   public Project findProject(FileResource<?> target, BuildSystem buildSystem)
   {
      return findProject(target, buildSystem, acceptsAllProjects);
   }

   @Override
   public Project findProject(FileResource<?> target, Predicate<Project> filter)
   {
      if (filter == null)
      {
         filter = acceptsAllProjects;
      }

      Project result = null;
      Imported<BuildSystem> instances = registry.getServices(BuildSystem.class);
      for (BuildSystem buildSystem : instances)
      {
         try
         {
            result = findProject(target, buildSystem, filter);
         }
         finally
         {
            instances.release(buildSystem);
         }

         if (result != null)
            break;
      }

      return result;
   }

   @Override
   public Project findProject(FileResource<?> target, BuildSystem buildSystem, Predicate<Project> filter)
   {
      if (filter == null)
      {
         filter = acceptsAllProjects;
      }

      Project result = null;
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

         if (result == null && buildSystem.containsProject(r))
         {
            result = buildSystem.createProject(r);
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

      return result;
   }

   @Override
   public Project createProject(DirectoryResource projectDir, BuildSystem buildSystem)
   {
      return createProject(projectDir, buildSystem, null);
   }

   @Override
   public Project createProject(DirectoryResource target, BuildSystem buildSystem,
            Iterable<Class<? extends ProjectFacet>> facetTypes)
   {
      Assert.notNull(target, "Target project directory must not be null.");
      Assert.notNull(buildSystem, "Build system type must not be null.");

      if (facetTypes != null)
         Assert.isTrue(isBuildable(buildSystem, facetTypes),
                  "The provided build system [" + buildSystem.getType()
                           + "] cannot create a project that requires facets of the following types: "
                           + getMissingBuildSystemFacets(buildSystem, getRequiredBuildSystemFacets(facetTypes)));

      Project result = buildSystem.createProject(target);
      if (result != null)
      {
         DirectoryResource parentDir = result.getProjectRoot().getParent().reify(DirectoryResource.class);
         if (parentDir != null)
         {
            Imported<ProjectAssociationProvider> buildSystemInstances = registry
                     .getServices(ProjectAssociationProvider.class);
            for (ProjectAssociationProvider provider : buildSystemInstances)
            {
               if (provider.canAssociate(result, parentDir))
               {
                  provider.associate(result, parentDir);
               }
               buildSystemInstances.release(provider);
            }
         }
      }

      if (result != null && facetTypes != null)
      {
         for (Class<? extends ProjectFacet> facetType : facetTypes)
         {
            try
            {
               if (!BuildSystemFacet.class.isAssignableFrom(facetType))
               {
                  Iterable<? extends ProjectFacet> facets = factory.createFacets(result, facetType);
                  for (ProjectFacet projectFacet : facets)
                  {
                     if (factory.install(result, projectFacet, notBuildSystemFilter))
                     {
                        break;
                     }
                  }
               }
            }
            catch (RuntimeException e)
            {
               throw new IllegalStateException("Could not install " + Facet.class.getSimpleName() + " of type ["
                        + facetType + "] into Project [" + result + "]", e);
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

   private Iterable<Class<? extends BuildSystemFacet>> getMissingBuildSystemFacets(BuildSystem buildSystem,
            Iterable<Class<? extends BuildSystemFacet>> requiredFacets)
   {
      Set<Class<? extends BuildSystemFacet>> result = new HashSet<Class<? extends BuildSystemFacet>>();
      Iterable<Class<? extends BuildSystemFacet>> providedFacetTypes = buildSystem.getProvidedFacetTypes();
      if (requiredFacets != null && providedFacetTypes != null)
      {
         for (Class<? extends BuildSystemFacet> required : requiredFacets)
         {
            boolean found = false;
            for (Class<? extends BuildSystemFacet> provided : providedFacetTypes)
            {
               if (provided.isAssignableFrom(required))
                  found = true;
            }
            if (!found)
               result.add(required);
         }
      }
      return result;
   }

   private boolean isBuildable(BuildSystem buildSystem, Iterable<Class<? extends ProjectFacet>> facets)
   {
      boolean result = false;
      Iterable<Class<? extends BuildSystemFacet>> requiredFacets = getRequiredBuildSystemFacets(facets);
      if (requiredFacets == null)
      {
         result = true;
      }
      else
      {
         result = !getMissingBuildSystemFacets(buildSystem, requiredFacets).iterator().hasNext();
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   private Iterable<Class<? extends BuildSystemFacet>> getRequiredBuildSystemFacets(
            Iterable<Class<? extends ProjectFacet>> facets)
   {
      Set<Class<? extends BuildSystemFacet>> result = new HashSet<Class<? extends BuildSystemFacet>>();
      for (Class<? extends ProjectFacet> facetType : facets)
      {
         if (BuildSystemFacet.class.isAssignableFrom(facetType))
         {
            result.add((Class<? extends BuildSystemFacet>) facetType);
         }
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
   public Project createTempProject() throws IllegalStateException
   {
      return createTempProject(Collections.<Class<? extends ProjectFacet>> emptySet());
   }

   @Override
   public Project createTempProject(Iterable<Class<? extends ProjectFacet>> facetTypes) throws IllegalStateException
   {
      Imported<BuildSystem> buildSystems = registry.getServices(BuildSystem.class);
      if (buildSystems.isAmbiguous())
         throw new IllegalStateException(
                  "Cannot create generic temporary project in environment where multiple build systems are available. "
                           + "A single build system must be selected.");

      BuildSystem buildSystem = buildSystems.get();
      try
      {
         return createTempProject(buildSystem, facetTypes);
      }
      finally
      {
         buildSystems.release(buildSystem);
      }
   }

   @Override
   public Project createTempProject(BuildSystem buildSystem)
   {
      return createTempProject(buildSystem, Collections.<Class<? extends ProjectFacet>> emptySet());
   }

   @Override
   public Project createTempProject(BuildSystem buildSystem, Iterable<Class<? extends ProjectFacet>> facetTypes)
   {
      File rootDirectory = OperatingSystemUtils.createTempDir();
      DirectoryResource addonDir = resourceFactory.create(DirectoryResource.class, rootDirectory);
      DirectoryResource projectDir = addonDir.createTempResource();
      projectDir.deleteOnExit();
      Project project = createProject(projectDir, buildSystem, facetTypes);
      return project;
   }

   @Override
   public ListenerRegistration<ProjectListener> addProjectListener(final ProjectListener listener)
   {
      Assert.notNull(listener, "Project listener must not be null.");
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
      boolean found = false;
      Imported<BuildSystem> instances = registry.getServices(BuildSystem.class);
      for (BuildSystem buildSystem : instances)
      {
         try
         {
            found = containsProject(bound, target, buildSystem);
            if (found)
               break;
         }
         finally
         {
            instances.release(buildSystem);
         }
      }
      return found;
   }

   @Override
   public boolean containsProject(DirectoryResource bound, FileResource<?> target, BuildSystem buildSystem)
   {
      Assert.notNull(bound, "Boundary should not be null");
      Assert.isTrue(isParent(bound, target), "Target should be a child of bound");
      DirectoryResource dir = (target instanceof DirectoryResource) ? (DirectoryResource) target : target.getParent();
      boolean found = false;
      DirectoryResource r = bound;
      while (r != null && !found)
      {
         found = buildSystem.containsProject(r);
         if (dir.equals(r))
         {
            break;
         }
         r = r.getParent();
      }
      return found;
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
      boolean found = false;
      Imported<BuildSystem> instances = registry.getServices(BuildSystem.class);
      for (BuildSystem buildSystem : instances)
      {
         try
         {
            found = containsProject(target, buildSystem);
            if (found)
               break;
         }
         finally
         {
            instances.release(buildSystem);
         }
      }
      return found;
   }

   @Override
   public boolean containsProject(FileResource<?> target, BuildSystem buildSystem)
   {
      Assert.notNull(target, "Target resource must not be null.");
      Assert.notNull(buildSystem, "Project build system must not be null.");
      boolean found = false;
      DirectoryResource dir = (target instanceof DirectoryResource) ? (DirectoryResource) target : target.getParent();
      DirectoryResource r = dir;
      while (r != null && !found)
      {
         found = buildSystem.containsProject(r);
         r = r.getParent();
      }
      return found;
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
