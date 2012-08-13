/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies.events;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.QueuedEvent;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;

/**
 * Fired when dependencies are removed from the current {@link Project}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@QueuedEvent
public final class RemovedDependencies
{
   private final List<Dependency> dependencies;
   private final Project project;

   public RemovedDependencies(Project project, Dependency... dependencies)
   {
      this.dependencies = Arrays.asList(dependencies);
      this.project = project;
   }

   public RemovedDependencies(Project project, List<Dependency> dependencies)
   {
      this.dependencies = dependencies;
      this.project = project;
   }

   /**
    * Return a list of all removed {@link Dependency} objects
    */
   public List<Dependency> getDependencies()
   {
      return dependencies;
   }

   /**
    * Get the {@link Project} from which this event was fired.
    */
   public Project getProject()
   {
      return project;
   }
}
