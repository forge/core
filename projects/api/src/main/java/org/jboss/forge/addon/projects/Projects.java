/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects;

import java.util.Map;

import org.jboss.forge.addon.resource.FileResource;
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
    * Stores the project inside the {@link UIContext}, to avoid unnecessary lookups
    */
   private static final String SCOPED_PROJECT_KEY = "org.jboss.forge.projects.CURRENT_PROJECT";

   /**
    * @return the project if {@link UIContext#getInitialSelection()} returns a path containing a project, null otherwise
    */
   public static Project getSelectedProject(ProjectFactory projectFactory, UIContext context)
   {
      Map<Object, Object> attributeMap = context.getAttributeMap();
      Project project = (Project) attributeMap.get(SCOPED_PROJECT_KEY);
      if (project == null)
      {
         UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
         if (!initialSelection.isEmpty())
         {
            project = projectFactory.findProject(initialSelection.get());
         }
         if (project != null)
         {
            attributeMap.put(SCOPED_PROJECT_KEY, project);
         }
      }
      return project;
   }

   /**
    * @return true if {@link UIContext#getInitialSelection()} returns a path containing a project, false otherwise
    */
   public static boolean containsProject(ProjectFactory projectFactory, UIContext context)
   {
      Project project = (Project) context.getAttributeMap().get(SCOPED_PROJECT_KEY);
      if (project == null)
      {
         UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
         if (!initialSelection.isEmpty())
         {
            return projectFactory.containsProject(initialSelection.get());
         }
         return false;
      }
      return true;
   }
}
