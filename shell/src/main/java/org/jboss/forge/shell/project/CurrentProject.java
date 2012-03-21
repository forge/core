/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
import org.jboss.forge.shell.project.ProjectScoped;
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
