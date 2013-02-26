/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.git.gitignore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.forge.git.GitFacet;
import org.jboss.forge.git.GitUtils;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.util.Streams;

@Alias("forge.vcs.git.ignore")
@RequiresFacet(GitFacet.class)
public class GitIgnoreFacet extends BaseFacet
{
   public static final String GITIGNORE = ".gitignore";

   private static final String GLOBAL_TEMPLATES = "Global";

   @Inject
   private GitIgnoreConfig config;

   @Inject
   private ResourceFactory factory;

   @Inject
   private Shell shell;

   @Override
   public boolean install()
   {
      try
      {
         DirectoryResource cloneDir = cloneDir();
         String repo = config.remoteRepository();
         ShellMessages.info(shell, "Cloning " + repo + " into " + cloneDir.getFullyQualifiedName());
         Git git = GitUtils.clone(cloneDir, repo);
         GitUtils.close(git);
         return true;
      }
      catch (Exception e)
      {
         ShellMessages.error(shell, "Failed to checkout gitignore: " + e);
         return false;
      }
   }

   @Override
   public boolean isInstalled()
   {
      File clone = config.localRepository();
      Resource<File> cloneDir = factory.getResourceFrom(clone);
      return cloneDir.exists() && cloneDir.getChild(".git").exists();
   }

   /**
    * List all available gitignore templates.
    */
   public List<GitIgnoreTemplateGroup> list()
   {
      List<GitIgnoreTemplateGroup> result = new ArrayList<GitIgnoreTemplateGroup>(2);
      DirectoryResource languages = cloneDir();
      result.add(new GitIgnoreTemplateGroup("Languages", listGitignores(languages)));
      result.add(new GitIgnoreTemplateGroup("Globals", listGitignores(languages.getChildDirectory(GLOBAL_TEMPLATES))));
      return result;
   }

   /**
    * Read the content of a gitignore template
    *
    * @param template Template name.
    * @return Template content as string.
    */
   @SuppressWarnings("unchecked")
   public String contentOf(String template)
   {
      DirectoryResource[] candidates = new DirectoryResource[] {
               cloneDir(), cloneDir().getChildDirectory(GLOBAL_TEMPLATES)
      };
      for (DirectoryResource dir : candidates)
      {
         if (listGitignores(dir).contains(template))
         {
            FileResource<?> file = dir.getChildOfType(FileResource.class, template + GITIGNORE);
            return Streams.toString(file.getResourceInputStream());
         }
      }
      return "";
   }

   /**
    * Update the templates from the remote repository.
    *
    * @throws IOException Failure reading the git repository.
    * @throws GitAPIException Git failure.
    */
   public void update() throws IOException, GitAPIException
   {
      Git git = GitUtils.git(cloneDir());
      GitUtils.pull(git, 10000);
      GitUtils.close(git);
   }

   private List<String> listGitignores(DirectoryResource dir)
   {
      List<String> result = new LinkedList<String>();
      ResourceFilter filter = new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            return resource.getName().endsWith(GITIGNORE);
         }
      };
      for (Resource<?> resource : dir.listResources(filter))
      {
         String name = resource.getName();
         String cut = name.substring(0, name.indexOf(GITIGNORE));
         result.add(cut);
      }
      return result;
   }

   private DirectoryResource cloneDir()
   {
      return new DirectoryResource(factory, config.localRepository());
   }

}
