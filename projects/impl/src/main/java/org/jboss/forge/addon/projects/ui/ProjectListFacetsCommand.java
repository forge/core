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
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.proxy.Proxies;

/**
 * List the facets associated with the current project
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ProjectListFacetsCommand extends AbstractProjectCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.addon.ui.command.AbstractUICommand#getMetadata(org.jboss.forge.addon.ui.context.UIContext)
    */
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Project: List Facets")
               .description("Lists the facets associated with the current project")
               .category(Categories.create("Project"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Project project = Projects.getSelectedProject(projectFactory, uiContext);
      PrintStream out = uiContext.getProvider().getOutput().out();
      for (ProjectFacet facet : project.getFacets())
      {
         Class<? extends ProjectFacet> type = facet.getClass();
         String name = Proxies.unwrapProxyClassName(type);
         out.println(name);
      }
      out.println();
      return Results.success();
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }
}