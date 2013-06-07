/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.observers;

import javax.enterprise.event.Observes;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.events.ProjectChanged;

/**
 * Displays information about a project if the environment is in verbose mode
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class ProjectInfoObserver
{

   public void observeProjectChanged(@Observes ProjectChanged projectChanged, Shell shell)
   {
      Project newProject = projectChanged.getNewProject();
      if (newProject != null && shell.isVerbose())
      {
         MetadataFacet metadata = newProject.getFacet(MetadataFacet.class);
         PackagingFacet packaging = newProject.getFacet(PackagingFacet.class);
         ShellMessages.info(shell, "Project found");
         ShellMessages.info(shell, "Name: \t" + shell.renderColor(ShellColor.BOLD, metadata.getProjectName()));
         ShellMessages.info(shell, "Version: \t" + shell.renderColor(ShellColor.BOLD, metadata.getProjectVersion()));
         ShellMessages.info(shell,
                  "Type: \t" + shell.renderColor(ShellColor.BOLD, packaging.getPackagingType().getDescription()));
         shell.println();
      }
   }
}
