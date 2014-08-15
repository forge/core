/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.maven.environment.Network;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.building.BuildException;
import org.jboss.forge.addon.projects.building.ProjectBuilder;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class MavenProjectBuilder implements ProjectBuilder
{
   private final Environment environment;
   private final Project project;

   private boolean runTests = true;
   private boolean quiet;
   private final List<String> args = new ArrayList<>();

   public MavenProjectBuilder(final Environment environment, final Project project)
   {
      this.environment = environment;
      this.project = project;
   }

   @Override
   public ProjectBuilder addArguments(final String... args)
   {
      this.args.addAll(Arrays.asList(args));
      return this;
   }

   @Override
   public ProjectBuilder runTests(final boolean test)
   {
      this.runTests = test;
      return this;
   }

   @Override
   public ProjectBuilder quiet(boolean quiet)
   {
      this.quiet = quiet;
      return this;
   }

   @Override
   public Resource<?> build()
   {
      return build(System.out, System.err);
   }

   @Override
   public Resource<?> build(PrintStream out, PrintStream err) throws BuildException
   {
      List<String> selected = new ArrayList<>();
      selected.addAll(Arrays.asList("clean", "package"));

      if ((args != null) && (!args.isEmpty()))
      {
         selected.clear();
         selected.addAll(args);
      }

      if (Network.isOffline(environment))
      {
         selected.add("--offline");
      }

      if (!runTests)
      {
         selected.add("-DskipTests=true");
         selected.add("-Dmaven.test.skip=true");
      }
      if (quiet)
      {
         selected.add("-q");
      }
      boolean success = project.getFacet(MavenFacet.class).executeMavenEmbedded(selected, out, err);

      if (success)
      {
         return project.getFacet(PackagingFacet.class).getFinalArtifact();
      }
      else
      {
         throw new BuildException("Build failed.");
      }
   }
}
