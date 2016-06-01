/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui.dependencies;

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
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

@FacetConstraint(DependencyFacet.class)
public class HasManagedDependenciesCommandImpl extends AbstractProjectCommand implements HasManagedDependenciesCommand
{
   private UIInputMany<Dependency> arguments;
   private UIInput<Boolean> effective;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      arguments = factory.createInputMany("arguments", 'd', Dependency.class).setLabel("Coordinates").setRequired(true)
               .setDescription(
                        "The coordinates of the arguments to be checked [groupId :artifactId {:version :scope :packaging}]");
      effective = factory.createInput("effective", 'e', Boolean.class).setLabel("Effective");
      builder.add(arguments).add(effective);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(HasManagedDependenciesCommandImpl.class)
               .description("Check one or more managed dependencies in the current project.")
               .name("Project: Has Managed Dependencies")
               .category(Categories.create("Project", "Manage"));
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
      return SimpleContainer
               .getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }
}