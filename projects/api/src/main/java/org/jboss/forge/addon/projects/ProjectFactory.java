/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects;

import org.jboss.forge.addon.projects.spi.ProjectCache;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
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
    * Locate a {@link Project} in the ancestry of the given {@link FileResource}. Return <code>null</code> if no
    * {@link Project} could be located.
    */
   public Project findProject(final FileResource<?> target);

   /**
    * Locate a {@link Project} in the ancestry of the given {@link FileResource}. Filter results using the given
    * {@link Predicate}. Return <code>null</code> if no {@link Project} could be located.
    */
   public Project findProject(final FileResource<?> target, Predicate<Project> filter);

   /**
    * Locate a {@link Project} with the given {@link ProjectProvider} in the ancestry of the given {@link FileResource}.
    * Return <code>null</code> if no {@link Project} could be located.
    */
   public Project findProject(final FileResource<?> target, ProjectProvider projectProvider);

   /**
    * Locate a {@link Project} using the given {@link ProjectProvider} in the ancestry of the given {@link FileResource}.
    * Filter results using the given {@link Predicate}. Return <code>null</code> if no {@link Project} could be located.
    */
   public Project findProject(final FileResource<?> target, ProjectProvider projectProvider, Predicate<Project> filter);

   /**
    * Invalidate all known {@link ProjectCache} instances. This causes the {@link ProjectFactory} to create new
    * instances of requested projects.
    */
   public void invalidateCaches();

   /**
    * Create a {@link Project} in the specified {@link DirectoryResource}, using the given {@link ProjectProvider}.
    */
   public Project createProject(DirectoryResource projectDir, ProjectProvider buildSystem);

   /**
    * Create a {@link Project} with the given {@link ProjectFacet} types in the specified {@link DirectoryResource},
    * using the given {@link ProjectProvider}.
    */
   public Project createProject(DirectoryResource targetDir, ProjectProvider projectProvider,
            Iterable<Class<? extends ProjectFacet>> facetTypes);

   /**
    * Returns true if a {@link Project} exists in the ancestry of the given {@link FileResource}. false if no
    * {@link Project} could be located
    */
   public boolean containsProject(final FileResource<?> target);

   /**
    * Returns true if a {@link Project} of the given {@link ProjectProvider} exists in the ancestry of the given
    * {@link FileResource}. false if no {@link Project} could be located
    */
   public boolean containsProject(final FileResource<?> target, ProjectProvider projectProvider);

   /**
    * Returns true if a {@link Project} exists in the ancestry of the given {@link FileResource}. false if no
    * {@link Project} could be located. Throws {@link IllegalArgumentException} if target is not a child of bound
    */
   public boolean containsProject(final DirectoryResource bound, final FileResource<?> target);

   /**
    * Returns true if a {@link Project} of the given {@link ProjectProvider} exists in the ancestry of the given
    * {@link FileResource}. false if no {@link Project} could be located. Throws {@link IllegalArgumentException} if
    * target is not a child of bound
    */
   public boolean containsProject(final DirectoryResource bound, final FileResource<?> target, ProjectProvider projectProvider);

   /**
    * Create a {@link Project} in a temporary location. This method is useful for testing purposes.
    * 
    * @throws IllegalStateException when multiple {@link ProjectProvider} implementations are available.
    */
   public Project createTempProject() throws IllegalStateException;

   /**
    * Create a {@link Project}, with the given {@link ProjectFacet} types, in a temporary location. This method is
    * useful for testing purposes.
    * 
    * @throws IllegalStateException when multiple {@link ProjectProvider} implementations are available.
    */
   public Project createTempProject(Iterable<Class<? extends ProjectFacet>> facetTypes) throws IllegalStateException;

   /**
    * Create a {@link Project} in a temporary location, using the given {@link ProjectProvider}. This method is useful for
    * testing purposes.
    */
   public Project createTempProject(ProjectProvider projectProvider);

   /**
    * Create a {@link Project}, with the given {@link ProjectFacet} types, in a temporary location. This method is
    * useful for testing purposes.
    */
   public Project createTempProject(ProjectProvider projectProvider, Iterable<Class<? extends ProjectFacet>> facetTypes);

   /**
    * Register a listener for {@link Project} events.
    */
   public ListenerRegistration<ProjectListener> addProjectListener(ProjectListener listener);
}
