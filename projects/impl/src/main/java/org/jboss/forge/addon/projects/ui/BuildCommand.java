/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.building.BuildException;
import org.jboss.forge.addon.projects.building.ProjectBuilder;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class BuildCommand extends AbstractProjectCommand
{

   @Inject
   @WithAttributes(label = "Arguments")
   private UIInputMany<String> arguments;

   @Inject
   @WithAttributes(label = "No Test")
   private UIInput<Boolean> notest;

   @Inject
   @WithAttributes(label = "Profile")
   private UIInput<String> profile;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // profile.setCompleter(new UICompleter<String>()
      // {
      // @Override
      // public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String
      // value)
      // {
      // MavenFacet mavenCoreFacet = getSelectedProject(context).getFacet(MavenFacet.class);
      // List<String> profiles = new ArrayList<String>();
      // List<Profile> profileList = mavenCoreFacet.getPOM().getProfiles();
      // for (Profile profile : profileList)
      // {
      // profiles.add(profile.getId());
      // }
      //
      // return profiles;
      // }
      // });
      builder.add(arguments).add(notest).add(profile);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Build").description("Build this project")
               .category(Categories.create("Project", "Build"));
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      UIOutput output = context.getProvider().getOutput();
      Project project = getSelectedProject(context);
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      ProjectBuilder builder = packaging.createBuilder();

      if (arguments.getValue() != null && arguments.getValue().iterator().hasNext())
      {
         List<String> args = new ArrayList<String>();
         for (String val : arguments.getValue())
         {
            args.add(val);
         }
         builder.addArguments(args.toArray(new String[args.size()]));
      }
      else
      {
         builder.addArguments("install");
      }

      if (notest.getValue())
      {
         builder.runTests(false);
      }

      if (profile.getValue() != null)
      {
         builder.addArguments("-P" + profile.getValue());
      }

      try
      {
         builder.build(output.out(), output.err());
      }
      catch (BuildException e)
      {
         return Results.fail("Build failed.", e);
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
}
