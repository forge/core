/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects;

import org.jboss.forge.addon.projects.spi.ProjectCache;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.spi.ListenerRegistration;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Used to create new or obtain references to existing {@link Project} instances.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProjectFactory
{
   /**
    * Locate a {@link Project} in the ancestry of the given {@link Resource}. Return <code>null</code> if no
    * {@link Project} could be located.
    */
   Project findProject(final Resource<?> target);

   /**
    * Locate a {@link Project} in the ancestry of the given {@link Resource}. Filter results using the given
    * {@link Predicate}. Return <code>null</code> if no {@link Project} could be located.
    */
   Project findProject(final Resource<?> target, Predicate<Project> filter);

   /**
    * Locate a {@link Project} with the given {@link ProjectProvider} in the ancestry of the given {@link Resource}.
    * Return <code>null</code> if no {@link Project} could be located.
    */
   Project findProject(final Resource<?> target, ProjectProvider projectProvider);

   /**
    * Locate a {@link Project} using the given {@link ProjectProvider} in the ancestry of the given {@link Resource}.
    * Filter results using the given {@link Predicate}. Return <code>null</code> if no {@link Project} could be located.
    */
   Project findProject(final Resource<?> target, ProjectProvider projectProvider, Predicate<Project> filter);

   /**
    * Invalidate all known {@link ProjectCache} instances. This causes the {@link ProjectFactory} to create new
    * instances of requested projects.
    */
   void invalidateCaches();

   /**
    * Create a {@link Project} in the specified {@link Resource}, using the given {@link ProjectProvider}.
    */
   Project createProject(Resource<?> projectDir, ProjectProvider buildSystem);

   /**
    * Create a {@link Project} with the given {@link ProjectFacet} types in the specified {@link Resource}, using the
    * given {@link ProjectProvider}.
    */
   Project createProject(Resource<?> targetDir, ProjectProvider projectProvider,
            Iterable<Class<? extends ProjectFacet>> facetTypes);

   /**
    * Returns true if a {@link Project} exists in the ancestry of the given {@link Resource}. false if no
    * {@link Project} could be located
    */
   boolean containsProject(final Resource<?> target);

   /**
    * Returns true if a {@link Project} of the given {@link ProjectProvider} exists in the ancestry of the given
    * {@link Resource}. false if no {@link Project} could be located
    */
   boolean containsProject(final Resource<?> target, ProjectProvider projectProvider);

   /**
    * Returns true if a {@link Project} exists in the ancestry of the given {@link Resource}. false if no
    * {@link Project} could be located. Throws {@link IllegalArgumentException} if target is not a child of bound
    */
   boolean containsProject(final Resource<?> bound, final Resource<?> target);

   /**
    * Returns true if a {@link Project} of the given {@link ProjectProvider} exists in the ancestry of the given
    * {@link Resource}. false if no {@link Project} could be located. Throws {@link IllegalArgumentException} if target
    * is not a child of bound
    */
   boolean containsProject(final Resource<?> bound, final Resource<?> target, ProjectProvider projectProvider);

   /**
    * Create a {@link Project} in a temporary location. This method is useful for testing purposes.
    *
    * @throws IllegalStateException when multiple {@link ProjectProvider} implementations are available.
    */
   Project createTempProject() throws IllegalStateException;

   /**
    * Create a {@link Project}, with the given {@link ProjectFacet} types, in a temporary location. This method is
    * useful for testing purposes.
    *
    * @throws IllegalStateException when multiple {@link ProjectProvider} implementations are available.
    */
   Project createTempProject(Iterable<Class<? extends ProjectFacet>> facetTypes) throws IllegalStateException;

   /**
    * Create a {@link Project} in a temporary location, using the given {@link ProjectProvider}. This method is useful
    * for testing purposes.
    */
   Project createTempProject(ProjectProvider projectProvider);

   /**
    * Create a {@link Project}, with the given {@link ProjectFacet} types, in a temporary location. This method is
    * useful for testing purposes.
    */
   Project createTempProject(ProjectProvider projectProvider, Iterable<Class<? extends ProjectFacet>> facetTypes);

   /**
    * Register a listener for {@link Project} events.
    */
   ListenerRegistration<ProjectListener> addProjectListener(ProjectListener listener);

   /**
    * Fire {@link org.jboss.forge.addon.projects.ProjectListener#projectCreated(Project)} for all listeners bound to the
    * {@link ProjectFactory}
    */
   void fireProjectCreated(Project project);
}
