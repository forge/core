/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.git.facet;

import static org.jboss.forge.addon.git.constants.GitConstants.GIT_DIRECTORY;

import javax.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.git.GitUtils;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * 
 */
public class GitFacetImpl extends AbstractFacet<Project> implements GitFacet
{

   @Inject
   private GitUtils gitUtils;

   @Override
   public boolean install()
   {
      Project project = getFaceted();
      // init git repo
      final DirectoryResource rootDirectory = project.getRootDirectory();
      FileResource<?> gitDir = rootDirectory.getChildDirectory(GIT_DIRECTORY).reify(FileResource.class);
      if (!gitDir.exists())
      {
         InitCommand init = Git.init();
         init.setDirectory(rootDirectory.getUnderlyingResourceObject());

         try
         {
            gitUtils.close(init.call());
         }
         catch (GitAPIException e)
         {
         }
      }

      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return getFaceted().getRootDirectory().getChildDirectory(GIT_DIRECTORY).exists();
   }

}
