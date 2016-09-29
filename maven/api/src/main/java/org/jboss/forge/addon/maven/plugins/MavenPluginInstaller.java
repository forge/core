/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

import org.jboss.forge.addon.projects.Project;

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
