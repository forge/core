/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui;

import java.io.PrintStream;
import java.util.Map;
import java.util.Objects;

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
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;

/**
 * Prematurely builds the {@link Project} (if exists) and warns if it is valid
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ProjectBuildStatusListener extends AbstractCommandExecutionListener
{
   private static final String PROJECT_BUILDSTATUS_SKIP_FLAG = "PROJECT_BUILDSTATUS_SKIP";

   @Override
   public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
   {
      Map<Object, Object> attributeMap = context.getUIContext().getAttributeMap();
      String skipProjectBuild = Objects.toString(attributeMap.get(PROJECT_BUILDSTATUS_SKIP_FLAG), null);
      Imported<ProjectFactory> services = SimpleContainer
               .getServices(getClass().getClassLoader(), ProjectFactory.class);
      if (Boolean.parseBoolean(skipProjectBuild) || services.isUnsatisfied())
      {
         // ProjectFactory is not available or PROJECT_BUILDSTATUS_SKIP_FLAG provided, ignore
         return;
      }
      ProjectFactory projectFactory = services.get();
      UIContext uiContext = context.getUIContext();
      Project project = Projects.getSelectedProject(projectFactory, uiContext.getSelection());
      if (project != null && project.hasFacet(PackagingFacet.class))
      {
         PackagingFacet facet = project.getFacet(PackagingFacet.class);
         BuildResult buildResult = facet.getBuildResult();
         if (buildResult != null && !buildResult.isSuccess())
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
