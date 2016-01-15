/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import java.io.PrintStream;
import java.util.Collections;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.projects.stacks.Stack;
import org.jboss.forge.addon.projects.stacks.StackFacet;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ProjectListStackCommand implements UICommand
{
   private UIInput<Boolean> all;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      all = builder.getInputComponentFactory().createInput("all", Boolean.class)
               .setLabel("Show all available stacks").setDescription("Show all available stacks");
      builder.add(all);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class)
               .get();
      UIOutput output = context.getUIContext().getProvider().getOutput();
      PrintStream out = output.out();
      Iterable<StackFacet> facets = Collections.emptyList();
      if (all.getValue())
      {
         facets = SimpleContainer.getServices(getClass().getClassLoader(), StackFacet.class);
      }
      else
      {
         Project project = Projects.getSelectedProject(projectFactory, context.getUIContext());
         if (project != null)
         {
            facets = project.getFacets(StackFacet.class);
         }
      }
      for (StackFacet stackFacet : facets)
      {
         Stack stack = stackFacet.getStack();
         out.printf("- %s %n", stack.getName());

         out.println("\t -> Includes: ");
         for (Class<? extends ProjectFacet> facet : stack.getIncludedFacets())
         {
            out.printf("\t\t - %s %n", facet.getName());
         }
         out.println("\t -> Excludes: ");
         for (Class<? extends ProjectFacet> facet : stack.getExcludedFacets())
         {
            out.printf("\t\t - %s %n", facet.getName());
         }
      }
      return Results.success();
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Project: List Stacks")
               .category(Categories.create("Project", "Stack"));
   }
}
