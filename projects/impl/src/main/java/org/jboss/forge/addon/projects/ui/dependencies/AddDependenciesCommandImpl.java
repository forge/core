package org.jboss.forge.addon.projects.ui.dependencies;

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
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

@FacetConstraint(DependencyFacet.class)
public class AddDependenciesCommandImpl extends AbstractProjectCommand implements AddDependenciesCommand
{
   private UIInputMany<Dependency> arguments;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      arguments = factory.createInputMany("arguments", 'd', Dependency.class).setLabel("Coordinates").setRequired(true)
               .setDescription(
                        "The coordinates of the arguments to be added [groupId :artifactId {:version :scope :packaging}]");
      builder.add(arguments);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(AddDependenciesCommandImpl.class)
               .description("Add one or more dependencies to the current project.")
               .name("Project: Add Dependencies")
               .category(Categories.create("Project", "Manage"));
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      Project project = getSelectedProject(context.getUIContext());
      final DependencyFacet deps = project.getFacet(DependencyFacet.class);

      if (arguments.hasValue())
      {
         int count = 0;
         DependencyInstaller installer = SimpleContainer
                  .getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
         for (Dependency gav : arguments.getValue())
         {
            Dependency existingDep = deps.getEffectiveManagedDependency(DependencyBuilder.create(gav).setVersion(null));
            if (existingDep != null)
            {
               if (context.getPrompt().promptBoolean(String.format(
                        "Dependency [%s:%s] is currently managed. "
                                 + "Reference the existing managed dependency [%s:%s:%s]?",
                        gav.getCoordinate().getArtifactId(),
                        gav.getCoordinate().getGroupId(),
                        existingDep.getCoordinate().getGroupId(),
                        existingDep.getCoordinate().getArtifactId(),
                        existingDep.getCoordinate().getVersion())))
               {
                  gav = DependencyBuilder.create(existingDep).setScopeType(gav.getScopeType());
               }
            }

            installer.install(project, gav);
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
      return SimpleContainer
               .getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }
}