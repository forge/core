/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.facets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.build.BuildException;
import org.jboss.forge.project.build.ProjectBuilder;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.resources.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MavenProjectBuilder implements ProjectBuilder
{
   private final ForgeEnvironment environment;
   private final Project project;

   private boolean runTests = true;
   private final List<String> args = new ArrayList<String>();

   @Inject
   public MavenProjectBuilder(final ForgeEnvironment environment, final Project project)
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
   public Resource<?> build()
   {
      List<String> selected = new ArrayList<String>();
      selected.addAll(Arrays.asList("clean", "package"));

      if ((args != null) && (!args.isEmpty()))
      {
         selected.clear();
         selected.addAll(args);
      }

      if (!environment.isOnline())
      {
         selected.add("--offline");
      }

      if (!runTests)
      {
         selected.add("-DskipTests=true");
         selected.add("-Dmaven.test.skip=true");
      }

      boolean success = project.getFacet(MavenCoreFacet.class).executeMaven(selected);

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
