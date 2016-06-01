/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.facet;

import static org.jboss.forge.addon.git.constants.GitConstants.GITIGNORE;
import static org.jboss.forge.addon.git.constants.GitConstants.GIT_DIRECTORY;
import static org.jboss.forge.addon.git.constants.GitConstants.GLOBAL_TEMPLATES;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.git.GitUtils;
import org.jboss.forge.addon.git.gitignore.GitIgnoreConfig;
import org.jboss.forge.addon.git.gitignore.GitIgnoreTemplateGroup;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Streams;

@FacetConstraint(GitFacet.class)
public class GitIgnoreFacetImpl extends AbstractFacet<Project>implements GitIgnoreFacet
{
   private GitIgnoreConfig config;
   private GitUtils gitUtils;
   private ResourceFactory factory;

   @Override
   public boolean install()
   {
      try
      {
         DirectoryResource cloneDir = cloneDir();
         String repo = getIgnoreConfig().remoteRepository();
         Git git = getGitUtils().clone(cloneDir, repo);
         getGitUtils().close(git);
         return true;
      }
      catch (Exception e)
      {
         return false;
      }
   }

   @Override
   public boolean isInstalled()
   {
      File clone = getIgnoreConfig().localRepository();
      Resource<File> cloneDir = getResourceFactory().create(clone);
      return cloneDir.exists() && cloneDir.getChild(GIT_DIRECTORY).exists();
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
      try (Git git = getGitUtils().git(cloneDir()))
      {
         getGitUtils().pull(git, 10000);
      }
   }

   private List<String> listGitignores(DirectoryResource dir)
   {
      List<String> result = new LinkedList<>();
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
      return getResourceFactory().create(DirectoryResource.class, getIgnoreConfig().localRepository());
   }

   private ResourceFactory getResourceFactory()
   {
      if (factory == null)
      {
         AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
         factory = addonRegistry.getServices(ResourceFactory.class).get();
      }
      return factory;
   }

   private GitIgnoreConfig getIgnoreConfig()
   {
      if (config == null)
      {
         AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
         config = addonRegistry.getServices(GitIgnoreConfig.class).get();
      }
      return config;
   }

   private GitUtils getGitUtils()
   {
      if (gitUtils == null)
      {
         AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
         gitUtils = addonRegistry.getServices(GitUtils.class).get();
      }
      return gitUtils;
   }

}
