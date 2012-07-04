package org.jboss.forge.git;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.Alias;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.vcs.git.api")
public class GitAPIFacet extends BaseFacet
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
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return installer.isInstalled(project, GIT_DEPENDENCY);
   }

}