package org.jboss.forge.maven.plugins;

import org.jboss.forge.projects.Project;

/**
 * Responsible for installing a given {@link MavenPlugin} into the current project. Resolves available plugins.
 *
 * @author <a href="mailto:salmon_charles@gmail.com">charless</a>
 *
 */
public interface MavenPluginInstaller
{
   /**
    * Install given {@link MavenPlugin}.
    */
   MavenPlugin install(Project project, MavenPlugin plugin);

   /**
    * Install given managed {@link MavenPlugin}.
    */
   MavenPlugin installManaged(Project project, MavenPlugin plugin);

   /**
    * Returns whether or not the given {@link MavenPlugin} is installed.
    */
   boolean isInstalled(Project project, MavenPlugin plugin);

}
