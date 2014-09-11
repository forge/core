/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.projects.facets.BuildStatusFacet;
import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;

/**
 * Prematurely builds the {@link Project} (if exists) and warns if it is valid
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ProjectBuildStatusListener extends AbstractCommandExecutionListener
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
   {
      UIContext uiContext = context.getUIContext();
      // Display build errors only if in shell
      if (!uiContext.getProvider().isGUI())
      {
         UISelection<?> selection = uiContext.getSelection();
         Project project = Projects.getSelectedProject(projectFactory, selection);
         if (project != null && project.hasFacet(BuildStatusFacet.class))
         {
            BuildStatusFacet facet = project.getFacet(BuildStatusFacet.class);
            if (!facet.isBuildable())
            {
               UIOutput output = uiContext.getProvider().getOutput();
               PrintStream err = output.err();
               output.error(err, String.format("Project '%s' has errors", project.getRoot()));
               for (LogRecord record : facet.getBuildMessages())
               {
                  if (Level.SEVERE.equals(record.getLevel()))
                  {
                     output.error(err, record.getMessage());
                  }
                  else if (Level.WARNING.equals(record.getLevel()))
                  {
                     output.warn(err, record.getMessage());
                  }
                  else
                  {
                     output.info(err, record.getMessage());
                  }
               }
            }
         }
      }
   }
}
