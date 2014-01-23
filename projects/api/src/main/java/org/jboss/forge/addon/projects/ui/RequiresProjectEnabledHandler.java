/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.annotation.handler.EnableCommandHandler;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;

/**
 * Requires a project to be enabled
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RequiresProjectEnabledHandler implements EnableCommandHandler
{

   @Inject
   private ProjectFactory projectFactory;

   @Override
   public boolean isEnabled(UIContext context)
   {
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         return projectFactory.containsProject(initialSelection.get());
      }
      return false;
   }

   protected Project getProject(UIContext context)
   {
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         return projectFactory.findProject(initialSelection.get());
      }
      return null;
   }

   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
