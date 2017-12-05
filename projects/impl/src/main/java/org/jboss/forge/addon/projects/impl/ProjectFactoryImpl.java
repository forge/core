/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectAssociationProvider;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ProjectListener;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;
import org.jboss.forge.addon.projects.spi.ProjectCache;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.events.ResourceEvent;
import org.jboss.forge.addon.resource.monitor.ResourceListener;
import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.AbstractEventListener;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Default implementation of {@link ProjectFactory}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ProjectFactoryImpl extends AbstractEventListener implements ProjectFactory
{
   private static final Logger log = Logger.getLogger(ProjectFactoryImpl.class.getName());

   private AddonRegistry addonRegistry;
   private ResourceFactory resourceFactory;
   private FacetFactory facetFactory;
   private Imported<ProjectListener> builtInListeners;
   private Imported<ProjectCache> caches;

   private final List<ListenerRegistration<ResourceListener>> listeners = new ArrayList<>();
   private final Set<ProjectProvider> providers = new HashSet<>();
   private long version = -1;

   @Override
   protected void handleThisPreShutdown()
   {
      invalidateCaches();
      for (ListenerRegistration<ResourceListener> registration : listeners)
      {
         registration.removeListener();
      }
   }

   private final Predicate<ProjectFacet> notProvidedProjectFacetFilter = new Predicate<ProjectFacet>()
   {
      @Override
      public boolean accept(ProjectFacet type)
      {
         return !(type instanceof ProvidedProjectFacet);
      }
   };

   private final List<ProjectListener> projectListeners = new ArrayList<>();

   private final Predicate<Project> acceptsAllProjects = new Predicate<Project>()
   {
      @Override
      public boolean accept(Project type)
      {
         return true;
      }
   };

   @Override
   public Project findProject(Resource<?> target)
   {
      return findProject(target, (Predicate<Project>) null);
   }

   @Override
   public Project findProject(Resource<?> target, ProjectProvider projectProvider)
   {
      return findProject(target, projectProvider, acceptsAllProjects);
   }

   @Override
   public Project findProject(Resource<?> target, Predicate<Project> filter)
   {
      if (filter == null)
      {
         filter = acceptsAllProjects;
      }

      Project result = null;

      for (Resource<?> dir : allDirectoriesOnPath(target))
      {
         for (ProjectProvider projectProvider : getProviders())
         {
            result = findProjectInDirectory(dir, projectProvider, filter);

            if (result != null)
               break;
         }

         if (result != null)
            break;
      }

      return result;
   }

   private Iterable<ProjectProvider> getProviders()
   {
      AddonRegistry addonRegistry = getAddonRegistry();
      if (addonRegistry.getVersion() != version)
      {
         version = addonRegistry.getVersion();
         providers.clear();
         for (ProjectProvider provider : addonRegistry.getServices(ProjectProvider.class))
         {
            providers.add(provider);
         }
      }
      return providers;
   }

   @Override
   public Project findProject(Resource<?> target, ProjectProvider projectProvider, Predicate<Project> filter)
   {
      Assert.notNull(target, "Target cannot be null");
      if (filter == null)
      {
         filter = acceptsAllProjects;
      }

      Project result = null;

      Iterator<Resource<?>> pathIterator = allDirectoriesOnPath(target).iterator();
      while (pathIterator.hasNext() && result == null)
      {
         result = findProjectInDirectory(pathIterator.next(), projectProvider, filter);
      }

      return result;
   }

   /**
    * Returns all directories on path starting from given directory up to the root.
    */
   private List<Resource<?>> allDirectoriesOnPath(Resource<?> startingDir)
   {
      List<Resource<?>> result = new ArrayList<>();

      while (startingDir != null)
      {
         result.add(startingDir);
         startingDir = startingDir.getParent();
      }

      return result;
   }

   /**
    * Returns project residing in given directory, if no such is found then null is returned.
    */
   private Project findProjectInDirectory(Resource<?> target, ProjectProvider projectProvider,
            Predicate<Project> filter)
   {
      Project result = null;
      Imported<ProjectCache> caches = getCaches();
      if (projectProvider.containsProject(target))
      {
         boolean cached = false;
         for (ProjectCache cache : caches)
         {
            result = cache.get(target);
            if (result != null && !filter.accept(result))
            {
               result = null;
            }
            if (result != null)
            {
               cached = true;
               break;
            }
         }
         if (result == null)
         {
            result = projectProvider.createProject(target);
         }
         if (result != null && !filter.accept(result))
         {
            result = null;
         }
         if (result != null && !cached)
         {
            registerAvailableFacets(result);
            cacheProject(result);
         }
      }

      return result;
   }

   @Override
   public Project createProject(Resource<?> target, ProjectProvider projectProvider)
   {
      return createProject(target, projectProvider, null);
   }

   @Override
   public Project createProject(Resource<?> target, ProjectProvider projectProvider,
            Iterable<Class<? extends ProjectFacet>> facetTypes)
   {
      Assert.notNull(target, "Target project directory must not be null.");
      Assert.notNull(projectProvider, "Build system type must not be null.");
      FacetFactory facetFactory = getFacetFactory();
      if (facetTypes != null)
         Assert.isTrue(
                  isBuildable(projectProvider, facetTypes),
                  "The provided build system ["
                           + projectProvider.getType()
                           + "] cannot create a project that requires facets of the following types: "
                           + getMissingProvidedProjectFacets(projectProvider,
                                    getRequiredProvidedProjectFacets(facetTypes)));

      Project result = projectProvider.createProject(target);
      if (result != null)
      {
         Resource<?> parent = result.getRoot().getParent();
         if (parent != null)
         {
            for (ProjectAssociationProvider provider : getAddonRegistry()
                     .getServices(ProjectAssociationProvider.class))
            {
               if (provider.canAssociate(result, parent))
               {
                  provider.associate(result, parent);
               }
            }
         }
      }

      if (result != null && facetTypes != null)
      {
         for (Class<? extends ProjectFacet> facetType : facetTypes)
         {
            try
            {
               if (!ProvidedProjectFacet.class.isAssignableFrom(facetType))
               {
                  Iterable<? extends ProjectFacet> facets = facetFactory.createFacets(result, facetType);
                  for (ProjectFacet projectFacet : facets)
                  {
                     if (facetFactory.install(result, projectFacet, notProvidedProjectFacetFilter))
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

   private Iterable<Class<? extends ProvidedProjectFacet>> getMissingProvidedProjectFacets(ProjectProvider buildSystem,
            Iterable<Class<? extends ProvidedProjectFacet>> requiredFacets)
   {
      Set<Class<? extends ProvidedProjectFacet>> result = new HashSet<>();
      Iterable<Class<? extends ProvidedProjectFacet>> providedFacetTypes = buildSystem.getProvidedFacetTypes();
      if (requiredFacets != null && providedFacetTypes != null)
      {
         for (Class<? extends ProvidedProjectFacet> required : requiredFacets)
         {
            boolean found = false;
            for (Class<? extends ProvidedProjectFacet> provided : providedFacetTypes)
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

   private boolean isBuildable(ProjectProvider buildSystem, Iterable<Class<? extends ProjectFacet>> facets)
   {
      boolean result = false;
      Iterable<Class<? extends ProvidedProjectFacet>> requiredFacets = getRequiredProvidedProjectFacets(facets);
      if (requiredFacets == null)
      {
         result = true;
      }
      else
      {
         result = !getMissingProvidedProjectFacets(buildSystem, requiredFacets).iterator().hasNext();
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   private Iterable<Class<? extends ProvidedProjectFacet>> getRequiredProvidedProjectFacets(
            Iterable<Class<? extends ProjectFacet>> facets)
   {
      Set<Class<? extends ProvidedProjectFacet>> result = new HashSet<>();
      for (Class<? extends ProjectFacet> facetType : facets)
      {
         if (ProvidedProjectFacet.class.isAssignableFrom(facetType))
         {
            result.add((Class<? extends ProvidedProjectFacet>) facetType);
         }
      }
      return result;
   }

   private void registerAvailableFacets(Project result)
   {
      FacetFactory facetFactory = getFacetFactory();
      for (Class<ProjectFacet> type : getAddonRegistry().getExportedTypes(ProjectFacet.class))
      {
         Iterable<ProjectFacet> facets = facetFactory.createFacets(result, type);
         for (ProjectFacet facet : facets)
         {
            if (facet != null && facetFactory.register(result, facet))
            {
               if (log.isLoggable(Level.FINE))
               {
                  log.fine("Registered Facet [" + facet + "] into Project [" + result + "]");
               }
            }
         }
      }
   }

   private void cacheProject(final Project project)
   {
      if (Projects.isCacheDisabled())
      {
         return;
      }
      final Imported<ProjectCache> caches = getCaches();
      for (ProjectCache cache : caches)
      {
         cache.store(project);
      }
      // If under a transaction, don't start monitoring
      DirectoryResource rootDirectory = project.getRoot().reify(DirectoryResource.class);
      if (rootDirectory != null && rootDirectory.getUnderlyingResourceObject().exists())
      {
         final ResourceMonitor monitor = rootDirectory.monitor();
         ListenerRegistration<ResourceListener> registration = monitor.addResourceListener(new ResourceListener()
         {
            @Override
            public void processEvent(ResourceEvent event)
            {
               for (ProjectCache cache : caches)
               {
                  cache.evict(project);
               }
               monitor.cancel();
            }
         });
         this.listeners.add(registration);
      }
   }

   @Override
   public void fireProjectCreated(Project project)
   {
      for (ProjectListener listener : getBuiltInListeners())
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
      Imported<ProjectProvider> buildSystems = getAddonRegistry().getServices(ProjectProvider.class);
      if (buildSystems.isAmbiguous())
         throw new IllegalStateException(
                  "Cannot create generic temporary project in environment where multiple build systems are available. "
                           + "A single build system must be selected.");

      ProjectProvider buildSystem = buildSystems.get();
      return createTempProject(buildSystem, facetTypes);
   }

   @Override
   public Project createTempProject(ProjectProvider buildSystem)
   {
      return createTempProject(buildSystem, Collections.<Class<? extends ProjectFacet>> emptySet());
   }

   @Override
   public Project createTempProject(ProjectProvider buildSystem, Iterable<Class<? extends ProjectFacet>> facetTypes)
   {
      File rootDirectory = OperatingSystemUtils.createTempDir();
      DirectoryResource addonDir = getResourceFactory().create(DirectoryResource.class, rootDirectory);
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
   public boolean containsProject(Resource<?> bound, Resource<?> target)
   {
      boolean found = false;
      for (ProjectProvider buildSystem : getProviders())
      {
         found = containsProject(bound, target, buildSystem);
         if (found)
            break;
      }
      return found;
   }

   @Override
   public boolean containsProject(Resource<?> bound, Resource<?> target, ProjectProvider buildSystem)
   {
      Assert.notNull(bound, "Boundary should not be null");
      Assert.isTrue(bound.equals(target) || isParent(bound, target), "Target should be a child of bound");
      boolean found = false;
      Resource<?> r = bound;
      while (r != null && !found)
      {
         found = buildSystem.containsProject(r);
         if (target.equals(r))
         {
            break;
         }
         r = r.getParent();
      }
      return found;
   }

   private boolean isParent(Resource<?> parent, Resource<?> child)
   {
      Resource<?> childDir = child.getParent();
      while (childDir != null)
      {
         if (parent.equals(childDir))
         {
            return true;
         }
         childDir = childDir.getParent();
      }
      return false;
   }

   @Override
   public boolean containsProject(Resource<?> target)
   {
      boolean found = false;
      for (ProjectProvider buildSystem : getProviders())
      {
         found = containsProject(target, buildSystem);
         if (found)
            break;
      }
      return found;
   }

   @Override
   public boolean containsProject(Resource<?> target, ProjectProvider buildSystem)
   {
      Assert.notNull(target, "Target resource must not be null.");
      Assert.notNull(buildSystem, "Project build system must not be null.");
      boolean found = false;
      Resource<?> r = target;
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
      if (caches != null)
      {
         for (ProjectCache cache : caches)
         {
            cache.invalidate();
         }
      }
   }

   private AddonRegistry getAddonRegistry()
   {
      if (addonRegistry == null)
      {
         addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      }
      return addonRegistry;
   }

   private FacetFactory getFacetFactory()
   {
      if (facetFactory == null)
      {
         facetFactory = SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
      }
      return facetFactory;
   }

   private ResourceFactory getResourceFactory()
   {
      if (resourceFactory == null)
      {
         resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
      }
      return resourceFactory;
   }

   private Imported<ProjectCache> getCaches()
   {
      if (caches == null)
      {
         caches = SimpleContainer.getServices(getClass().getClassLoader(), ProjectCache.class);
      }
      return caches;
   }

   private Imported<ProjectListener> getBuiltInListeners()
   {
      if (builtInListeners == null)
      {
         builtInListeners = SimpleContainer.getServices(getClass().getClassLoader(), ProjectListener.class);
      }
      return builtInListeners;
   }
}
