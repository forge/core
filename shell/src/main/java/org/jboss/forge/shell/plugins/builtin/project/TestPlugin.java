/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
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
@Alias("test")
@Topic("Project")
@RequiresProject
@RequiresFacet({ DependencyFacet.class, PackagingFacet.class })
@Help("Execute this project's test suite using the underlying build system.")
public class TestPlugin implements Plugin
{

   private Project project;

   public TestPlugin()
   {
   }

   @Inject
   public TestPlugin(final Project project)
   {
      this.project = project;
   }

   @DefaultCommand
   public void build(final PipeOut out,
            @Option(name = "profile", completer = ProfileCompleter.class) String profile,
            @Option(description = "test arguments") String... args)
   {
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);

      List<String> arguments = new ArrayList<String>();
      arguments.add("test");
      if (args != null)
         arguments.addAll(Arrays.asList(args));

      if (profile != null)
      {
         arguments.add("-P" + profile);
      }

      packaging.executeBuild(arguments.toArray(new String[] {}));
   }

}