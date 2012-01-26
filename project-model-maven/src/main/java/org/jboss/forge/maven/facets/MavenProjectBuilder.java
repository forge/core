/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
