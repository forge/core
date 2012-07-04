package org.jboss.forge.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.Alias;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * 
 */
@Alias("forge.vcs.git")
public class GitFacet extends BaseFacet
{
   @Override
   public boolean install()
   {
      // init git repo
      FileResource<?> gitDir = project.getProjectRoot().getChildDirectory(".git").reify(FileResource.class);
      if (!gitDir.exists())
      {
         InitCommand init = Git.init();
         init.setDirectory(project.getProjectRoot().getUnderlyingResourceObject());
         init.call();
      }

      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return project.getProjectRoot().getChildDirectory(".git").exists();
   }

}