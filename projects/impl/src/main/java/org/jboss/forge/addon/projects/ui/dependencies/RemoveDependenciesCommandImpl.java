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
public class RemoveDependenciesCommandImpl extends AbstractProjectCommand implements RemoveDependenciesCommand
{
   private UISelectMany<Dependency> arguments;
   private UIInput<Boolean> removeManaged;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder.getUIContext());
      InputComponentFactory factory = builder.getInputComponentFactory();
      arguments = factory.createSelectMany("arguments", 'd', Dependency.class).setLabel("Coordinates").setRequired(true)
               .setDescription(
                        "The coordinates of the arguments to be removed [groupId :artifactId {:version :scope :packaging}]");
      removeManaged = factory.createInput("removeManaged", 'r', Boolean.class).setLabel("Remove managed arguments")
               .setDefaultValue(false)
               .setDescription("Also remove any related managed arguments from the current project, if possible.");

      arguments.setValueChoices(project.getFacet(DependencyFacet.class).getDependencies());
      builder.add(arguments).add(removeManaged);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(RemoveDependenciesCommandImpl.class)
               .description("Remove one or more arguments from the current project.")
               .name("Project: Remove Dependencies")
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
            deps.removeDependency(gav);
            if (removeManaged.getValue())
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
      return SimpleContainer
               .getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }
}