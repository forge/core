/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin.project;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.build.BuildException;
import org.jboss.forge.project.build.ProjectBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("build")
@Topic("Project")
@RequiresProject
@RequiresFacet({ DependencyFacet.class, PackagingFacet.class })
@Help("Perform a build using the underlying build system.")
public class BuildPlugin implements Plugin
{

   private Project project;

   public BuildPlugin()
   {
   }

   @Inject
   public BuildPlugin(final Project project)
   {
      this.project = project;
   }

   @DefaultCommand
   public void build(final PipeOut out,
            @Option(name = "notest", flagOnly = true) final boolean notest,
            @Option(name = "profile", completer = ProfileCompleter.class) final String profile,
            @Option(description = "build arguments") final String... args)
   {
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);

      ProjectBuilder builder = packaging.createBuilder();

      if (args == null)
      {
         builder.addArguments("install");
      }
      else
      {
         builder.addArguments(args);
      }

      if (notest)
      {
         builder.runTests(false);
      }

      if (profile != null)
      {
         builder.addArguments("-P" + profile);
      }

      try
      {
         builder.build();
      }
      catch (BuildException e)
      {
         throw new RuntimeException("Build failed.", e);
      }
   }

}