package org.jboss.forge.projects.dependencies;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.projects.Project;

/**
 * Responsible for installing a given {@link Dependency} into the current project. Resolves available dependencies.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface DependencyInstaller
{
   /**
    * Install the given {@link Dependency}. This method overwrites existing dependencies, and updates existing managed
    * dependencies.
    */
   Dependency install(Project project, Dependency dependency);

   /**
    * Install a managed {@link Dependency} matching the given {@link Dependency}. This method overwrites existing
    * managed dependencies.
    */
   Dependency installManaged(Project project, Dependency dependency);

   /**
    * Returns <code>true</code> if the given {@link Dependency}. If a version is supplied in the query, it must match
    * the installed version; otherwise, version is ignored.
    */
   boolean isInstalled(Project project, Dependency dependency);
}