/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.building.ProjectBuilder;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Lists;

/**
 * Executes Build commands
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(PackagingFacet.class)
public class BuildCommand extends AbstractProjectCommand
{
   private UIInputMany<String> arguments;
   private UIInput<Boolean> notest;
   private UIInput<Boolean> quiet;
   private UIInputMany<String> profile;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      arguments = factory.createInputMany("arguments", String.class);
      notest = factory.createInput("notest", Boolean.class).setLabel("No Test");
      quiet = factory.createInput("quiet", 'q', Boolean.class).setLabel("Quiet").setDescription("Quiet output");
      profile = factory.createInputMany("profile", String.class);
      builder.add(arguments).add(profile).add(notest).add(quiet);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Build").description("Build this project")
               .category(Categories.create("Project", "Build"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIOutput output = context.getUIContext().getProvider().getOutput();
      Project project = getSelectedProject(context);
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      ProjectBuilder builder = packaging.createBuilder();

      if (arguments.getValue() != null && arguments.getValue().iterator().hasNext())
      {
         List<String> args = new ArrayList<>();
         for (String val : arguments.getValue())
         {
            args.add(val);
         }
         builder.addArguments(args.toArray(new String[args.size()]));
      }

      if (notest.getValue())
      {
         builder.runTests(false);
      }

      if (profile.hasValue())
      {
         List<String> list = Lists.toList(profile.getValue());
         builder.profiles(list.toArray(new String[list.size()]));
      }

      builder.quiet(quiet.getValue());

      try
      {
         builder.build(output.out(), output.err());
      }
      catch (Exception e)
      {
         return Results.fail(e.getMessage(), e);
      }
      finally
      {
         output.out().flush();
         output.err().flush();
      }

      return Results.success("Build Success");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }
}
