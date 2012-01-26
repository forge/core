/*
 * JBoss, by Red Hat.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
   {}

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

      if (args == null) {
         builder.addArguments("install");
      }
      else {
         builder.addArguments(args);
      }

      if (notest) {
         builder.runTests(false);
      }

      if (profile != null) {
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