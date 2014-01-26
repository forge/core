/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.cdi;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;
import org.jboss.forge.addon.ui.context.UISelection;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class ProjectUIContextListener implements UIContextListener
{
   private Project project;

   @Inject
   private ProjectFactory projectFactory;

   @Override
   public void contextInitialized(UIContext context)
   {
      this.project = getSelectedProject(context);
   }

   @Override
   public void contextDestroyed(UIContext context)
   {
      this.project = null;
   }

   @Produces
   @Typed(Project.class)
   public Project getProject()
   {
      return project;
   }

   /**
    * Returns the selected project. null if no project is found
    */
   private Project getSelectedProject(UIContext context)
   {
      Project project = null;
      UISelection<FileResource<?>> initialSelection = context.getInitialSelection();
      if (!initialSelection.isEmpty())
      {
         project = projectFactory.findProject(initialSelection.get());
      }
      return project;
   }
}
