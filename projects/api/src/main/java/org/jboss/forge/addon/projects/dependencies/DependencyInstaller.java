/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.dependencies;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;

/**
 * Responsible for installing a given {@link Dependency} into the specified project. Resolves available dependency
 * versions against the project's known dependency hierarchy to avoid installing duplicates.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface DependencyInstaller
{
   /**
    * Install the given {@link Dependency}. This method overwrites existing dependencies, and updates existing managed
    * dependencies. If a version range is supplied, or dependency version is omitted, a the highest matching dependency
    * version will be installed.
    * <p>
    * <b>Project requires: {@link DependencyFacet}
    */
   Dependency install(Project project, Dependency dependency);

   /**
    * Install a the given {@link Dependency} as a managed dependency. This method overwrites existing managed
    * dependencies. If a version range is supplied, or dependency version is omitted, a the highest matching dependency
    * version will be installed.
    * <p>
    * <b>Project requires: {@link DependencyFacet}
    */
   Dependency installManaged(Project project, Dependency dependency);

   /**
    * Returns <code>true</code> if the given {@link Project} contains the requested {@link Dependency}. If a version is
    * supplied in the query, the dependency must match the installed version; otherwise, version is ignored.
    * <p>
    * <b>Project requires: {@link DependencyFacet}
    */
   boolean isInstalled(Project project, Dependency dependency);

   /**
    * Returns <code>true</code> if the given {@link Project} contains the requested managed {@link Dependency}. If a
    * version is supplied in the query, the managed dependency must match the installed version; otherwise, version is
    * ignored.
    * <p>
    * <b>Project requires: {@link DependencyFacet}
    */
   boolean isManaged(Project origin, Dependency dependency);
}