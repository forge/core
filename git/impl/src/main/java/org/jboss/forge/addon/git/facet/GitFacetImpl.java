/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.facet;

import static org.jboss.forge.addon.git.constants.GitConstants.GIT_DIRECTORY;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * 
 */
public class GitFacetImpl extends AbstractFacet<Project>implements GitFacet
{
   static final Logger logger = Logger.getLogger(GitFacetImpl.class.getName());

   @Override
   public boolean install()
   {
      Project project = getFaceted();
      // init git repo
      DirectoryResource rootDirectory = project.getRoot().reify(DirectoryResource.class);
      FileResource<?> gitDir = rootDirectory.getChild(GIT_DIRECTORY).reify(FileResource.class);
      if (!gitDir.exists())
      {
         InitCommand init = Git.init();
         init.setDirectory(rootDirectory.getUnderlyingResourceObject());

         try
         {
            init.call().close();
         }
         catch (Exception e)
         {
            logger.log(Level.SEVERE, "Error while initializing directory", e);
         }
      }

      return true;
   }

   @Override
   public boolean isInstalled()
   {
      DirectoryResource root = getFaceted().getRoot().reify(DirectoryResource.class);
      return root != null && root.getChildDirectory(GIT_DIRECTORY).exists();
   }

}
