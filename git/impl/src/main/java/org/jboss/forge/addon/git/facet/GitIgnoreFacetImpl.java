/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.git.facet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.git.GitUtils;
import org.jboss.forge.addon.git.gitignore.GitIgnoreConfig;
import org.jboss.forge.addon.git.gitignore.GitIgnoreTemplateGroup;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.DirectoryResourceImpl;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.furnace.util.Streams;

@FacetConstraint(GitFacet.class)
public class GitIgnoreFacetImpl extends AbstractFacet<Project> implements GitIgnoreFacet
{
   public static final String GITIGNORE = ".gitignore";

   private static final String GLOBAL_TEMPLATES = "Global";

   @Inject
   private GitIgnoreConfig config;
   
   @Inject
   private GitUtils gitUtils;

   @Inject
   private ResourceFactory factory;

   @Override
   public boolean install()
   {
      try
      {
         DirectoryResource cloneDir = cloneDir();
         String repo = config.remoteRepository();
// TODO        ShellMessages.info(shell, "Cloning " + repo + " into " + cloneDir.getFullyQualifiedName());
         Git git = gitUtils.clone(cloneDir, repo);
         gitUtils.close(git);
         return true;
      }
      catch (Exception e)
      {
// TODO         ShellMessages.error(shell, "Failed to checkout gitignore: " + e);
         return false;
      }
   }

   @Override
   public boolean isInstalled()
   {
      File clone = config.localRepository();
      Resource<File> cloneDir = factory.create(clone);
      return cloneDir.exists() && cloneDir.getChild(".git").exists();
   }

   @Override
   public List<GitIgnoreTemplateGroup> list()
   {
      List<GitIgnoreTemplateGroup> result = new ArrayList<>(2);
      DirectoryResource languages = cloneDir();
      result.add(new GitIgnoreTemplateGroup("Languages", listGitignores(languages)));
      result.add(new GitIgnoreTemplateGroup("Globals", listGitignores(languages.getChildDirectory(GLOBAL_TEMPLATES))));
      return result;
   }

   @Override
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

   @Override
   public void update() throws IOException, GitAPIException
   {
      Git git = gitUtils.git(cloneDir());
      gitUtils.pull(git, 10000);
      gitUtils.close(git);
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
      return new DirectoryResourceImpl(factory, config.localRepository());
   }

}
