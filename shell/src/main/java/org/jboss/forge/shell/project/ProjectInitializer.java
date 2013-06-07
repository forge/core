/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.project;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.events.InitProject;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ProjectInitializer
{
   private final Shell shell;
   private final CurrentProject cp;

   private final ProjectFactory projectFactory;

   @Inject
   public ProjectInitializer(final Shell shell, final CurrentProject currentProjectHolder,
            final ProjectFactory projectFactory)
   {
      this.shell = shell;
      this.cp = currentProjectHolder;
      this.projectFactory = projectFactory;
   }

   public void doInit(@Observes final InitProject event)
   {
      DirectoryResource currentDirectory = shell.getCurrentDirectory();

      Project newProject = null;

      try
      {
         final DirectoryResource newRoot = projectFactory.findProjectRootRecusively(currentDirectory);
         if (newRoot != null)
         {
            Project oldProject = cp.getCurrent();
            if (oldProject != null)
            {
               DirectoryResource oldProjectRoot = oldProject.getProjectRoot();
               if (!newRoot.equals(oldProjectRoot))
               {
                  newProject = projectFactory.findProjectRecursively(currentDirectory);
               }
               else
               {
                  newProject = oldProject;
               }
            }
            else
            {
               newProject = projectFactory.findProjectRecursively(currentDirectory);
            }
         }

         if (newProject != null)
         {
            shell.getEnvironment().setProperty("PROJECT_NAME",
                     newProject.getFacet(MetadataFacet.class).getProjectName());
         }
      }
      finally
      {
         cp.setCurrentProject(newProject);
      }
   }
}
