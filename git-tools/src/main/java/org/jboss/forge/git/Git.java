/*
 * JBoss, Home of Professional Open Source
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
package org.jboss.forge.git;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.SetupCommand;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 *
 */
@Alias("git")
public class Git implements Plugin
{
   @Inject
   private Shell shell;

   @Inject
   private Event<InstallFacets> event;

   @Inject
   private Project project;

   @SetupCommand
   public void setup(PipeOut out)
   {
      if (!project.hasFacet(GitFacet.class))
         event.fire(new InstallFacets(GitFacet.class));
      else
         ShellMessages.info(out, "Git repository exists already.");
   }

   @Command("clone")
   public void gitClone(PipeOut out,
            @Option(description = "git repo uri") String uri,
            @Option(description = "target directory") Resource<?> folder) throws Exception
   {
      if (folder instanceof FileResource<?>)
      {
         if (!folder.exists())
         {
            ((FileResource<?>) folder).mkdirs();
            folder = folder.reify(DirectoryResource.class);
         }
         GitUtils.clone((DirectoryResource) folder, uri);
         shell.setCurrentResource(folder);
      }
      else
      {
         throw new RuntimeException();
      }
   }

   @Command("git-checkout")
   public void gitCheckout(
            PipeOut out,
            @Option(description = "branch name", defaultValue = "master") String ref,
            @Option(name = "createBranch", shortName = "b") boolean createBranch,
            @Option(name = "track", shortName = "t", description = "remote tracking mode", defaultValue = "master") SetupUpstreamMode mode,
            @Option(name = "force") boolean force) throws Exception
   {
      GitUtils.checkout(GitUtils.git(shell.getCurrentProject().getProjectRoot()), ref, createBranch, mode, force);
   }
}
