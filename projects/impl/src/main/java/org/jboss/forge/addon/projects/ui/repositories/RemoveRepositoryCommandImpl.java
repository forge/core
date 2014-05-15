package org.jboss.forge.addon.projects.ui.repositories;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

@FacetConstraint(DependencyFacet.class)
public class RemoveRepositoryCommandImpl extends AbstractProjectCommand implements RemoveRepositoryCommand
{
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(RemoveRepositoryCommandImpl.class)
               .description("Remove a repository configured in the current project descriptor.")
               .name("Project: Remove Repository")
               .category(Categories.create("Project", "Manage"));
   }

   @Inject
   private ProjectFactory factory;

   @Inject
   @WithAttributes(label = "Repository URL", required = true, description = "The repository URL")
   private UIInput<String> url;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(url);
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
      return factory;
   }
}