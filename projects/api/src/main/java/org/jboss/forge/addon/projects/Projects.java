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

/**
 * Helper class for {@link Project} objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public final class Projects
{
   /**
    * @return the project if {@link UIContext#getInitialSelection()} returns a path containing a project, null otherwise
    */
   public static Project getSelectedProject(ProjectFactory projectFactory, UIContext context)
   {
      return getSelectedProject(projectFactory, context.getInitialSelection());
   }

   /**
    * @return the project if {@link UISelection#isEmpty()} does not return false, null otherwise
    */
   public static Project getSelectedProject(ProjectFactory projectFactory, UISelection<?> selection)
   {
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
      UISelection<?> selection = context.getInitialSelection();
      if (!selection.isEmpty() && selection.get() instanceof Resource)
      {
         return projectFactory.containsProject((Resource<?>) selection.get());
      }
      return false;

   }
}
