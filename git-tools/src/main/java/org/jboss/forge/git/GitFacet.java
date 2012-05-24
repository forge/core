package org.jboss.forge.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.resources.FileResource;

import javax.inject.Inject;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 *
 */
public class GitFacet extends BaseFacet
{
   private static final Dependency GIT_DEPENDENCY = DependencyBuilder.create()
         .setGroupId("org.eclipse.jgit")
         .setArtifactId("org.eclipse.jgit.pgm");

   @Inject
   public DependencyInstaller installer;

   @Override
   public boolean install()
   {
      installer.install(project, GIT_DEPENDENCY);

      // init git repo
      FileResource<?> gitDir = project.getProjectRoot().getChildDirectory(".git").reify(FileResource.class);
      if(!gitDir.exists())
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
      return installer.isInstalled(project, GIT_DEPENDENCY) &&
            project.getProjectRoot().getChildDirectory(".git").exists();
   }

}