/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.dev.mvn;

import javax.inject.Inject;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author Mike Brock .
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("mvn")
@Topic("Project")
@RequiresProject
@RequiresFacet(MavenCoreFacet.class)
public class MvnShellPlugin implements Plugin
{
   private final Project project;

   @Inject
   public MvnShellPlugin(final Project project)
   {
      this.project = project;
   }

   @DefaultCommand
   public void run(final PipeOut out, final String... parms)
   {
      project.getFacet(MavenCoreFacet.class).executeMaven(out, parms);
   }
}
