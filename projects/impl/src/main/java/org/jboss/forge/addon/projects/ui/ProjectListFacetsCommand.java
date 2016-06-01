/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui;

import java.io.PrintStream;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.proxy.Proxies;

/**
 * List the facets associated with the current project
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ProjectListFacetsCommand extends AbstractProjectCommand
{
   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

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
      Project project = getSelectedProject(context);
      PrintStream out = uiContext.getProvider().getOutput().out();
      for (ProjectFacet facet : project.getFacets())
      {
         Object unwrappedFacet = Proxies.unwrap(facet);
         Class<?> type = unwrappedFacet.getClass();
         out.println(type.getSimpleName() + "\t[" + unwrappedFacet + "]");
      }
      out.println();
      return Results.success();
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return super.isEnabled(context) && !context.getProvider().isGUI();
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return SimpleContainer
               .getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }
}