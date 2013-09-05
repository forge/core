/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * Supports Project operations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractProjectCommand extends AbstractUICommand
{
   @Inject
   protected AddonRegistry addonRegistry;

   /**
    * Checks if a project exists in the current selection
    * 
    * @param context
    * @return
    */
   protected boolean containsProject(UIContext context)
   {
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         return getProjectFactory().containsProject(initialSelection.get());
      }
      return false;

   }

   /**
    * Returns the selected project. null if no project is found
    */
   protected Project getSelectedProject(UIContext context)
   {
      Project project = null;
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         project = getProjectFactory().findProject(initialSelection.get());
      }
      return project;
   }

   protected ProjectFactory getProjectFactory()
   {
      return addonRegistry.getServices(ProjectFactory.class).get();
   }

}
