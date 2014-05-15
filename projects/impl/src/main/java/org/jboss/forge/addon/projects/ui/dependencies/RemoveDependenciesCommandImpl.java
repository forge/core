package org.jboss.forge.addon.projects.ui.dependencies;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

@FacetConstraint(DependencyFacet.class)
public class RemoveDependenciesCommandImpl extends AbstractProjectCommand implements RemoveDependenciesCommand
{
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(RemoveDependenciesCommandImpl.class)
               .description("Remove one or more arguments from the current project.")
               .name("Project: Remove Dependencies")
               .category(Categories.create("Project", "Manage"));
   }

   @Inject
   private ProjectFactory factory;

   @Inject
   @WithAttributes(shortName = 'd', label = "Coordinates", required = true,
            description = "The coordinates of the arguments to be removed [groupId :artifactId {:version :scope :packaging}]")
   private UISelectMany<Dependency> arguments;

   @Inject
   @WithAttributes(shortName = 'r', label = "Remove managed arguments", defaultValue = "false", required = false,
            description = "Also remove any related managed arguments from the current project, if possible.")
   private UIInput<Boolean> removeManaged;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder.getUIContext());
      arguments.setValueChoices(project.getFacet(DependencyFacet.class).getDependencies());
      builder.add(arguments).add(removeManaged);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      Project project = getSelectedProject(context.getUIContext());
      final DependencyFacet deps = project.getFacet(DependencyFacet.class);

      if (arguments.hasValue())
      {
         int count = 0;
         for (Dependency gav : arguments.getValue())
         {
            deps.removeDependency(gav);
            if(removeManaged.getValue())
            {
               deps.removeManagedDependency(gav);
            }
            count++;
         }

         return Results.success("Removed [" + count + "] dependenc" + (count == 1 ? "y" : "ies") + ".");
      }
      return Results.fail("No arguments specified.");
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