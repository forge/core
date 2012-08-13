/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.packaging.events;

import org.jboss.forge.QueuedEvent;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.packaging.PackagingType;

/**
 * This event is fired when the current {@link Project}'s {@link PackagingType} is changed.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@QueuedEvent
public final class PackagingChanged
{
   private final PackagingType oldPackagingType;
   private final PackagingType newPackagingType;
   private final Project project;

   public PackagingChanged(final Project project, final PackagingType old, final PackagingType newType)
   {
      this.project = project;
      this.oldPackagingType = old;
      this.newPackagingType = newType;
   }

   public PackagingType getOldPackagingType()
   {
      return oldPackagingType;
   }

   public PackagingType getNewPackagingType()
   {
      return newPackagingType;
   }

   public Project getProject()
   {
      return project;
   }
}
