/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.projects;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.container.spi.ListenerRegistration;
import org.jboss.forge.container.util.Predicate;
import org.jboss.forge.resource.DirectoryResource;

/**
 * Used to create new or obtain references to existing {@link Project} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface ProjectFactory
{
   /**
    * Locate a {@link Project} in the ancestry of the given {@link DirectoryResource}. Return <code>null</code> if no
    * {@link Project} could be located.
    */
   public Project findProject(final DirectoryResource target);

   /**
    * Locate a {@link Project} in the ancestry of the given {@link DirectoryResource}. Filter results using the given
    * {@link Predicate}. Return <code>null</code> if no {@link Project} could be located.
    */
   public Project findProject(final DirectoryResource target, Predicate<Project> filter);

   /**
    * Create a {@link Project} in the specified {@link DirectoryResource}.
    */
   public Project createProject(DirectoryResource projectDir);

   /**
    * Create a {@link Project} with the given {@link ProjectFacet} types in the specified {@link DirectoryResource}.
    */
   public Project createProject(DirectoryResource targetDir, Iterable<Class<? extends ProjectFacet>> facetTypes);

   /**
    * Register a listener for project events
    */
   public ListenerRegistration<ProjectListener> addProjectListener(ProjectListener listener);
}
