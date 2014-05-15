package org.jboss.forge.addon.projects.ui.dependencies;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

@FacetConstraint(DependencyFacet.class)
public class AddManagedDependenciesCommandImpl extends AbstractProjectCommand implements AddManagedDependenciesCommand
{
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(AddManagedDependenciesCommandImpl.class)
               .description("Add one or more managed dependencies to the current project.")
               .name("Project: Add Managed Dependencies")
               .category(Categories.create("Project", "Manage"));
   }

   @Inject
   private ProjectFactory factory;

   @Inject
   private DependencyInstaller installer;

   @Inject
   @WithAttributes(shortName = 'd', label = "Coordinates", required = true,
            description = "The coordinates of the managed arguments to be added [groupId :artifactId {:version :scope :packaging}]")
   private UIInputMany<Dependency> arguments;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      Project project = getSelectedProject(context.getUIContext());
      final DependencyFacet deps = project.getFacet(DependencyFacet.class);

      if (arguments.hasValue())
      {
         int count = 0;
         for (Dependency dependency : arguments.getValue())
         {

            Dependency existingDep = deps.getEffectiveManagedDependency(DependencyBuilder.create(dependency)
                     .setVersion(
                              null));
            if (existingDep != null)
            {
               if (context.getPrompt().promptBoolean(
                        String.format("Dependency is already managed [%s:%s:%s], reference the managed dependency?",
                                 existingDep.getCoordinate().getGroupId(), existingDep.getCoordinate().getArtifactId(),
                                 existingDep.getCoordinate().getVersion())))
               {
                  return Results.success("Project not updated: No changes required.");
               }
            }

            this.installer.installManaged(project, dependency);
            count++;
         }

         return Results.success("Installed [" + count + "] dependenc" + (count == 1 ? "y" : "ies") + ".");
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