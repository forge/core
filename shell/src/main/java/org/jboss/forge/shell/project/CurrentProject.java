/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.project;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.events.InitProject;
import org.jboss.forge.shell.events.ProjectChanged;
import org.jboss.forge.shell.project.resources.CurrentResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class CurrentProject
{
   // TODO separate project-specific forge impl into separate project!
   private Project currentProject;

   @Inject
   private CurrentResource currentResource;
   @Inject
   private Event<InitProject> init;
   @Inject
   private Event<ProjectChanged> projectChanged;

   @Produces
   @Default
   @ProjectScoped
   public Project getCurrent()
   {
      return currentProject;
   }

   public void setCurrentProject(final Project project)
   {
      if ((project != null) && (currentProject != null))
      {
         FileResource<?> currentRoot = currentProject.getProjectRoot();
         FileResource<?> newRoot = project.getProjectRoot();
         if (!currentRoot.equals(newRoot))
         {
            changeProject(currentProject, project);
         }
         else
         {
            // Do not notify when updating the current project instance.
            this.currentProject = project;
         }
      }
      else if (((project != null) && (currentProject == null))
               || ((project == null) && (currentProject != null)))
      {
         changeProject(currentProject, project);
      }
   }

   private void changeProject(final Project currentProject, final Project project)
   {
      ProjectChanged event = new ProjectChanged(currentProject, project);
      this.currentProject = project;
      projectChanged.fire(event);
   }

   public void setCurrentResource(final Resource<?> resource)
   {
      this.currentResource.setCurrent(resource);
      init.fire(new InitProject());
   }

   public Resource<?> getCurrentResource()
   {
      return this.currentResource.getCurrent();
   }
}
