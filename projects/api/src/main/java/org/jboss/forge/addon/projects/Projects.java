/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.furnace.util.Assert;

/**
 * Helper class for {@link Project} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public final class Projects
{
   /**
    * @return the project if {@link UIContext#getSelection()()} returns a path containing a project, null otherwise
    */
   public static Project getSelectedProject(ProjectFactory projectFactory, UIContext context)
   {
      return getSelectedProject(projectFactory, context.getSelection());
   }

   /**
    * @return the project if {@link UISelection#isEmpty()} does not return false, null otherwise
    */
   public static Project getSelectedProject(ProjectFactory projectFactory, UISelection<?> selection)
   {
      Assert.notNull(projectFactory, "Project Factory cannot be null");
      Project project = null;
      if (!selection.isEmpty() && selection.get() instanceof Resource)
      {
         Resource<?> resource = (Resource<?>) selection.get();
         project = projectFactory.findProject(resource);
      }
      return project;
   }

   /**
    * @return true if {@link UIContext#getInitialSelection()} returns a path containing a project, false otherwise
    */
   public static boolean containsProject(ProjectFactory projectFactory, UIContext context)
   {
      Assert.notNull(projectFactory, "Project Factory cannot be null");
      UISelection<?> selection = context.getInitialSelection();
      if (!selection.isEmpty() && selection.get() instanceof Resource)
      {
         return projectFactory.containsProject((Resource<?>) selection.get());
      }
      return false;

   }
}
