/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui.repositories;

import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

@FacetConstraint(DependencyFacet.class)
public class RemoveRepositoryCommandImpl extends AbstractProjectCommand implements RemoveRepositoryCommand
{
   private UIInput<String> url;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      url = factory.createInput("url", String.class).setLabel("Repository URL").setRequired(true)
               .setDescription("The repository URL");
      builder.add(url);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(RemoveRepositoryCommandImpl.class)
               .description("Remove a repository configured in the current project descriptor.")
               .name("Project: Remove Repository")
               .category(Categories.create("Project", "Manage"));
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      final Result result;
      Project project = getSelectedProject(context.getUIContext());
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      String urlValue = url.getValue();
      DependencyRepository rep = deps.removeRepository(urlValue);
      if (rep != null)
      {
         result = Results.success("Removed repository [" + rep.getId() + "->" + rep.getUrl() + "]");
      }
      else
      {
         result = Results.fail("No repository with url [" + urlValue + "]");
      }
      return result;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return SimpleContainer
               .getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }
}