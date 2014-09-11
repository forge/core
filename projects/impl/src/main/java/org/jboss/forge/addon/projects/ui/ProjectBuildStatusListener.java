/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import java.io.PrintStream;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.projects.building.BuildMessage;
import org.jboss.forge.addon.projects.building.BuildResult;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
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
      Project project = Projects.getSelectedProject(projectFactory, uiContext.getSelection());
      if (project != null && project.hasFacet(PackagingFacet.class))
      {
         PackagingFacet facet = project.getFacet(PackagingFacet.class);
         BuildResult buildResult = facet.getBuildResult();
         if (!buildResult.isSuccess())
         {
            UIOutput output = uiContext.getProvider().getOutput();
            PrintStream err = output.err();
            output.error(err, String.format("Project '%s' has errors", project.getRoot()));
            for (BuildMessage message : buildResult.getMessages())
            {
               switch (message.getSeverity())
               {
               case ERROR:
                  output.error(err, message.getMessage());
                  break;
               case WARN:
                  output.warn(err, message.getMessage());
                  break;
               case INFO:
               default:
                  output.info(err, message.getMessage());
                  break;
               }
            }
         }
      }
   }
}
