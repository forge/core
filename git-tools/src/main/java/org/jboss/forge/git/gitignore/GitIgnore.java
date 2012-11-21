/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.git.gitignore;

import java.io.IOException;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.forge.git.GitFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.events.PickupResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;

/**
 * Creates .gitignore files based on <a href="https://github.com/github/gitignore.git">GitHub templates</a>.
 * Credits to the <a href="https://github.com/simonwhitaker/gitignore-boilerplates">gibo</a>
 * shell script by Simon Whitaker for the idea.
 * 
 * @author <a href="mailto:thomas.hug@gmail.com">Thomas Hug</a>
 */
@Alias("gitignore")
@Help("Creates .gitignore files based on template files from https://github.com/github/gitignore.git.")
@RequiresProject
@RequiresFacet({GitFacet.class, GitIgnoreFacet.class})
public class GitIgnore implements Plugin
{
   @Inject
   private GitIgnoreConfig config;
   
   @Inject
   private Shell shell;
   
   @Inject
   private ShellPrompt prompt;
   
   @Inject
   private ResourceFactory factory;
   
   @Inject
   private Event<InstallFacets> request;
   
   @Inject
   private Event<PickupResource> pickUp;
   
   @Inject
   private Project project;

   @SetupCommand(help = "Clones the .gitignore template repository " +
   		"into a local destination.")
   public void setup() {
      try
      {
         promptCloneDir();
         promptRepository();
         request.fire(new InstallFacets(GitIgnoreFacet.class));
      }
      catch (Exception e)
      {
         ShellMessages.error(shell, "Failed to create gitignore repository: " + e.getMessage());
      }
   }

   @Command(value = "list-templates", help = "List all available .gitignore templates")
   public void list(PipeOut out)
   {
      ShellMessages.info(shell, "Installed .gitignore templates:");
      for (GitIgnoreTemplateGroup group : project.getFacet(GitIgnoreFacet.class).list())
      {
         out.println("============= " + group.getName() +  " =============");
         for (String template : group.getTemplates())
         {
            out.println(template);
         }
      }
   }
   
   @Command(value = "create", help = "Create a .gitignore from templates")
   public void create(PipeOut out,
            @Option(required = true,
                    completer = GitIgnoreTemplateCompleter.class)
            String... templates)
   {
      try
      {
         GitIgnoreFacet facet = project.getFacet(GitIgnoreFacet.class);
         GitIgnoreResource resource = gitIgnoreResource();
         StringBuffer buffer = new StringBuffer();
         for (String template : templates)
         {
            String content = facet.contentOf(template);
            buffer.append(content);
         }
         String content = buffer.toString();
         resource.setContents(content);
         ShellMessages.success(shell, "Wrote to .gitignore. Content:");
         out.println(content);
         pickUp.fire(new PickupResource(resource));
      }
      catch (Exception e)
      {
         ShellMessages.error(shell, "Failed writing .gitignore: " + e.getMessage());
      }
   }
   
   @Command(value = "update-repo", help = "Update the local template repository")
   public void update()
   {
      try
      {
         project.getFacet(GitIgnoreFacet.class).update();
         ShellMessages.success(shell, "Local gitignore repository updated.");
      }
      catch (IOException e)
      {
         ShellMessages.error(shell, "Error reading local repository: " + e.getMessage());
      }
      catch (GitAPIException e)
      {
         ShellMessages.error(shell, "Error pulling remote repository: " + e.getMessage());
      }
   }
   
   @Command(value = "edit", help = "Change the .gitignore file")
   public void edit()
   {
      pickUp.fire(new PickupResource(gitIgnoreResource()));
   }
   
   private void promptCloneDir()
   {
      FileResource<?> checkout = prompt.promptFile("Where should the gitignore" +
            " template repository be installed at?", defaultDirectory());
      if (checkout.exists()) {
         validate(checkout);
      } else {
         checkout.mkdir();
      }
      config.setLocalRepository(checkout.getFullyQualifiedName());
   }
   
   private void promptRepository()
   {
      String repo = prompt.prompt("Do you want to provide a different repository" +
      		" location for gitignore templates?", config.defaultRemoteRepository());
      config.setRemoteRepository(repo);
   }

   private FileResource<?> defaultDirectory()
   {
      return (FileResource<?>) factory.getResourceFrom(config.defaultLocalRepository());
   }

   private void validate(FileResource<?> clone)
   {
      if (!clone.isDirectory())
      {
         throw new IllegalArgumentException("File " + clone + " is not a directory.");
      }
      if (!clone.listResources().isEmpty())
      {
         throw new IllegalArgumentException("Directory " + clone + " is not empty");
      }
   }
   
   private GitIgnoreResource gitIgnoreResource()
   {
      GitIgnoreResource resource = project.getProjectRoot().getChildOfType(GitIgnoreResource.class, ".gitignore");
      if (!resource.exists()) {
         resource.createNewFile();
      }
      return resource;
   }

}
