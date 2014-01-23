package org.jboss.forge.addon.projects.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
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

public class AddDependencyCommand extends AbstractProjectCommand
{

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(AddDependencyCommand.class)
               .name(context.getProvider().isGUI() ? "Project: Add Dependency" : "project-add-dependency")
               .category(Categories.create("Project", "Manage"));
   }

   @Inject
   private ProjectFactory factory;

   @Inject
   private DependencyInstaller installer;

   @Inject
   @WithAttributes(shortName = 'd', label = "Coordinates", required = true,
            description = "The coordinates of the project dependency to be added [groupId :artifactId {:version :scope :packaging}}")
   private UIInput<Dependency> dependency;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(dependency);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      Project project = getSelectedProject(context.getUIContext());
      final DependencyFacet deps = project.getFacet(DependencyFacet.class);

      Dependency gav = dependency.getValue();
      final boolean hasEffectiveManagedDependency = deps.hasEffectiveManagedDependency(gav);

      if (hasEffectiveManagedDependency)
      {
         Dependency existingDep = deps.getEffectiveManagedDependency(gav);
         if (context.getPrompt().promptBoolean(
                  String.format("Dependency is managed [%s:%s:%s], reference the managed dependency?",
                           existingDep.getCoordinate().getGroupId(), existingDep.getCoordinate().getArtifactId(),
                           existingDep.getCoordinate().getVersion())))
         {
            gav = existingDep;
         }
      }

      this.installer.install(project, gav);

      return Results.fail("Not implemented!");
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