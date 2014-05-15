package org.jboss.forge.addon.projects.ui.dependencies;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

@FacetConstraint(DependencyFacet.class)
public class HasManagedDependenciesCommandImpl extends AbstractProjectCommand implements HasManagedDependenciesCommand
{
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(HasManagedDependenciesCommandImpl.class)
               .description("Check one or more managed dependencies in the current project.")
               .name("Project: Has Managed Dependencies")
               .category(Categories.create("Project", "Manage"));
   }

   @Inject
   private ProjectFactory factory;

   @Inject
   @WithAttributes(shortName = 'd', label = "Coordinates", required = true,
            description = "The coordinates of the managed arguments to be checked [groupId :artifactId {:version :scope :packaging}]")
   private UIInputMany<Dependency> arguments;

   @Inject
   @WithAttributes(shortName = 'e', label = "Effective", required = false,
            description = "", defaultValue = "")
   private UIInput<Boolean> effective;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments);
      builder.add(effective);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      Project project = getSelectedProject(context.getUIContext());
      final DependencyFacet deps = project.getFacet(DependencyFacet.class);

      if (arguments.hasValue())
      {
         int numberOfGavsFound = 0;
         int numberOfGavs = 0;
         for (Dependency gav : arguments.getValue())
         {
            numberOfGavs++;
            DependencyBuilder dep = DependencyBuilder.create(gav);
            if (effective.getValue())
            {
               if (deps.hasEffectiveManagedDependency(gav))
               {
                  numberOfGavsFound++;
               }
            }
            else
            {
               if (deps.hasDirectManagedDependency(dep))
               {
                  numberOfGavsFound++;
               }
            }
         }
         if (numberOfGavs == numberOfGavsFound)
         {
            return Results.success("All arguments found");
         }
         else
         {
            return Results.fail("Missing " + (numberOfGavs - numberOfGavsFound));
         }
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