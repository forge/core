/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.projects;

import java.util.List;

import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.DependencyRepository;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.projects.ProjectFacet;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public interface MavenPluginFacet extends ProjectFacet
{

   List<MavenPlugin> listConfiguredPlugins();

   boolean hasPlugin(Dependency dependency);

   MavenPlugin getPlugin(Dependency dependency);

   void addPlugin(MavenPlugin plugin);

   void removePlugin(Dependency dependency);

   void updatePlugin(final MavenPlugin plugin);

   List<MavenPlugin> listConfiguredManagedPlugins();

   boolean hasManagedPlugin(Dependency dependency);

   MavenPlugin getManagedPlugin(Dependency dependency);

   void addManagedPlugin(MavenPlugin plugin);

   void removeManagedPlugin(Dependency dependency);

   void updateManagedPlugin(final MavenPlugin plugin);

   /**
    * Add a repository to the project build system. This is where dependencies can be found, downloaded, and installed
    * to the project build script.
    */
   public void addPluginRepository(String name, String url);

   /**
    * Return true if the given repository URL is already registered in this project's build system.
    */
   public boolean hasPluginRepository(String url);

   /**
    * Remove the given {@link org.jboss.forge.project.dependencies.DependencyRepository} from the current project.
    * Return true if the repository was removed; return false otherwise. Return the removed repository, or null if no
    * repository was removed.
    */
   public DependencyRepository removePluginRepository(String url);

   /**
    * Get the list of plugin repositories for which this project is currently configured to use in dependency
    * resolution.
    */
   public List<DependencyRepository> getPluginRepositories();
}
