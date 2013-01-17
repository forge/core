/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies.events;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;

/**
 * Fired when a dependency is finishes updating
 *
 */
public final class UpdatedDependency
{
   private Dependency from;
   private Dependency to;

   private Project project;

   public UpdatedDependency(Project project, Dependency from, Dependency to)
   {
      this.project = project;
      this.from = from;
      this.to = to;
   }

   public Dependency getFrom()
   {
      return from;
   }

   public Dependency getTo()
   {
      return to;
   }

   public Project getProject()
   {
      return project;
   }
}
