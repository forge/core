/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.events;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.PackagingFacet;

/**
 * Fired when a {@link Project} is configured to produce a new {@link PackagingFacet#getString()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PackagingChanged
{
   private final String previousType;
   private final String newType;
   private final Project project;

   protected PackagingChanged()
   {
      previousType = null;
      newType = null;
      project = null;
   }

   public PackagingChanged(final Project project, final String previousType, final String newType)
   {
      this.project = project;
      this.previousType = previousType;
      this.newType = newType;
   }

   public String getPreviousType()
   {
      return previousType;
   }

   public String getNewType()
   {
      return newType;
   }

   public Project getProject()
   {
      return project;
   }
}
