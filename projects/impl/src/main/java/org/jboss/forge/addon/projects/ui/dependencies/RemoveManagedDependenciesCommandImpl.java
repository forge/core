/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui.dependencies;

import org.jboss.forge.addon.dependencies.Dependency;
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
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

@FacetConstraint(DependencyFacet.class)
public class RemoveManagedDependenciesCommandImpl extends AbstractProjectCommand implements
         RemoveManagedDependenciesCommand
{
   private UISelectMany<Dependency> arguments;
   private UIInput<Boolean> removeUnmanaged;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder.getUIContext());
      InputComponentFactory factory = builder.getInputComponentFactory();
      arguments = factory.createSelectMany("arguments", 'd', Dependency.class).setLabel("Coordinates").setRequired(true)
               .setDescription(
                        "The coordinates of the managed arguments to be removed [groupId :artifactId {:version :scope :packaging}]");
      removeUnmanaged = factory.createInput("removeUnmanaged", 'r', Boolean.class).setLabel("Remove managed arguments")
               .setDefaultValue(false)
               .setDescription(
                        "Also remove any related arguments from the current project if they are now un-managed, if possible.");

      arguments.setValueChoices(project.getFacet(DependencyFacet.class).getManagedDependencies());
      builder.add(arguments).add(removeUnmanaged);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(RemoveManagedDependenciesCommandImpl.class)
               .description("Remove one or more managed arguments from the current project.")
               .name("Project: Remove Managed Dependencies")
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
         for (Dependency gav : arguments.getValue())
         {
            deps.removeManagedDependency(gav);
            if (removeUnmanaged.getValue() && !deps.hasEffectiveManagedDependency(gav))
            {
               deps.removeDependency(gav);
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
      return SimpleContainer
               .getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }
}