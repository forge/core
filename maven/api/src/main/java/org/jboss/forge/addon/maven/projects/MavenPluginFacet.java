/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.util.List;

import org.apache.maven.model.Plugin;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.util.Dependencies;
import org.jboss.forge.addon.maven.plugins.MavenPlugin;
import org.jboss.forge.addon.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.addon.maven.profiles.Profile;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public interface MavenPluginFacet extends ProvidedProjectFacet
{

   /**
    * Add the given {@link MavenPlugin} to this {@link Project}'s immediate list of plugins. This method does not check
    * for existence of plugins in the hierarchy, instead, directly adds or replaces a direct plugin.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   void addPlugin(MavenPlugin plugin);

   /**
    * Add the given {@link MavenPlugin} to this {@link Project}'s list of plugins under the specified profile. This
    * method does not check for existence of plugins in the hierarchy, instead, directly adds or replaces a direct
    * plugin.
    * <p/>
    * If the profileId doesn't exist, a new <profile> will be created, otherwise it will be reused.
    *
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   void addPlugin(MavenPlugin plugin, Profile profile);

   /**
    * Add the given managed {@link MavenPlugin} to this {@link Project}'s immediate list of managed plugins. This method
    * does not check for existence of managed plugins in the hierarchy, instead, directly adds or replaces a direct
    * managed plugin.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   void addManagedPlugin(MavenPlugin plugin);

   /**
    * Add the given managed {@link MavenPlugin} to this {@link Project}'s list of managed plugins under the specified
    * profile. This method does not check for existence of managed plugins in the hierarchy, instead, directly adds or
    * replaces a direct managed plugin.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   void addManagedPlugin(MavenPlugin plugin, Profile profile);

   /**
    * Attempt to locate a plugin given it's {@link Coordinate}, if it exists in the {@link Project} direct dependency
    * list, and return it.
    * <p/>
    * See also: {@link MavenPluginBuilder}. See also: {@link #hasPlugin(Plugin)}. <br>
    * <br>
    * <b>Notice:</b> This method checks only the immediate project plugins, meaning that if a dependency is declared
    * somewhere else in the hierarchy, it will not be detected by this method.
    *
    */
   MavenPlugin getPlugin(Coordinate coordinate);

   /**
    * Attempt to locate a plugin given it's {@link Coordinate}, if it exists in the {@link Project} direct dependency
    * list, and return it.
    * <p/>
    * See also: {@link MavenPluginBuilder}. See also: {@link #hasPlugin(Plugin)}. <br>
    * <br>
    * <b>Notice:</b> This method checks only the immediate project plugins, meaning that if a dependency is declared
    * somewhere else in the hierarchy, it will not be detected by this method.
    *
    */
   MavenPlugin getPlugin(Coordinate coordinate, Profile profile);

   /**
    * Attempt to locate a plugin given it's {@link Coordinate}, if it exists anywhere in the {@link Project} plugin
    * hierarchy, and return it.
    * <p/>
    * See also: {@link MavenPluginBuilder}. See also: {@link #hasEffectivePlugin(Coordinate)}.
    *
    * @return
    */
   MavenPlugin getEffectivePlugin(Coordinate coordinate);

   /**
    * Attempt to locate a plugin given it's {@link Coordinate}, if it exists anywhere in the {@link Project} plugin
    * hierarchy, and return it.
    * <p/>
    * See also: {@link MavenPluginBuilder}. See also: {@link #hasEffectivePlugin(Coordinate)}.
    *
    * @return
    */
   MavenPlugin getEffectivePlugin(Coordinate coordinate, Profile profile);

   /**
    * Attempt to locate a managed a plugin given it's {@link Coordinate}, if it exists in the {@link Project}, and
    * return it.
    * <p/>
    * See also: {@link MavenPluginBuilder}. See also: {@link #hasManagedPlugin(Coordinate)}.
    *
    * @return
    */
   MavenPlugin getManagedPlugin(Coordinate coordinate);

   /**
    * Attempt to locate a managed a plugin given it's {@link Coordinate}, if it exists in the {@link Project}, and
    * return it.
    * <p/>
    * See also: {@link MavenPluginBuilder}. See also: {@link #hasManagedPlugin(Coordinate)}.
    *
    * @return
    */
   MavenPlugin getManagedPlugin(Coordinate coordinate, Profile profile);

   /**
    * Searches {@link Project} and returns a managed plugin matching the given {@link Coordinate} at any level of the
    * project hierarchy; return null otherwise.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   MavenPlugin getEffectiveManagedPlugin(Coordinate coordinate);

   /**
    * Searches {@link Project} and returns a managed plugin matching the given {@link Coordinate} at any level of the
    * project hierarchy; return null otherwise.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   MavenPlugin getEffectiveManagedPlugin(Coordinate coordinate, Profile profile);

   /**
    * Return an immutable list of all direct {@link MavenPlugin} contained within this project. (i.e.: all plugins for
    * which {@link #hasPlugin(Coordinate)} returns true;
    */
   List<MavenPlugin> listConfiguredPlugins();

   /**
    * Return an immutable list of all direct {@link MavenPlugin} contained within this project. (i.e.: all plugins for
    * which {@link #hasPlugin(Coordinate)} returns true;
    */
   List<MavenPlugin> listConfiguredPlugins(Profile profile);

   /**
    * Return an immutable list of all {@link MavenPlugin} contained within this project, including the hierarchy (i.e.:
    * all plugins for which {@link #hasEffectivePlugin(Coordinate)} returns true;
    */
   List<MavenPlugin> listConfiguredEffectivePlugins();

   /**
    * Return an immutable list of all {@link MavenPlugin} contained within this project, including the hierarchy (i.e.:
    * all plugins for which {@link #hasEffectivePlugin(Coordinate)} returns true;
    */
   List<MavenPlugin> listConfiguredEffectivePlugins(Profile profile);

   /**
    * Return an immutable list of all direct managed {@link MavenPlugin} contained within this project. (i.e.: all
    * managed plugins for which {@link #hasManagedPlugin(Coordinate)} returns true;
    */
   List<MavenPlugin> listConfiguredManagedPlugins();

   /**
    * Return an immutable list of all direct managed {@link MavenPlugin} contained within this project. (i.e.: all
    * managed plugins for which {@link #hasManagedPlugin(Coordinate)} returns true;
    */
   List<MavenPlugin> listConfiguredManagedPlugins(Profile profile);

   /**
    * Return an immutable list of all managed {@link MavenPlugin} contained within this project, including the
    * hierarchy. (i.e.: all managed plugins for which {@link #hasEffectiveManagedPlugin(Coordinate)} returns true;
    */
   List<MavenPlugin> listConfiguredEffectiveManagedPlugins();

   /**
    * Return an immutable list of all managed {@link MavenPlugin} contained within this project, including the
    * hierarchy. (i.e.: all managed plugins for which {@link #hasEffectiveManagedPlugin(Coordinate)} returns true;
    */
   List<MavenPlugin> listConfiguredEffectiveManagedPlugins(Profile profile);

   /**
    * Return true if this {@link Project} contains a plugin matching the given {@link Coordinate}; return false
    * otherwise.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks only the immediate project plugins, meaning that if a plugin is declared
    * somewhere else in the hierarchy, it will not be detected by this method, even though
    * {@link #hasEffectivePlugin(Coordinate)} may return true.
    */
   boolean hasPlugin(final Coordinate coordinate);

   /**
    * Return true if this {@link Project} contains a plugin matching the given {@link Coordinate}; return false
    * otherwise.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks only the immediate project plugins, meaning that if a plugin is declared
    * somewhere else in the hierarchy, it will not be detected by this method, even though
    * {@link #hasEffectivePlugin(Coordinate)} may return true.
    */
   boolean hasPlugin(final Coordinate coordinate, Profile profile);

   /**
    * Return true if the given {@link Coordinate} exists anywhere in the project dependency hierarchy. See also:
    * {@link MavenPluginBuilder}. See also: {@link #getEffectivePlugin(Coordinate)}.
    */
   boolean hasEffectivePlugin(final Coordinate coordinate);

   /**
    * Return true if the given {@link Coordinate} exists anywhere in the project dependency hierarchy. See also:
    * {@link MavenPluginBuilder}. See also: {@link #getEffectivePlugin(Coordinate)}.
    */
   boolean hasEffectivePlugin(final Coordinate coordinate, Profile profile);

   /**
    * Return true if this {@link Project} contains a managed plugin matching the given {@link Coordinate}; return false
    * otherwise.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks only the immediate project managed plugins, meaning that if a managed plugin is
    * declared somewhere else in the hierarchy, it will not be detected by this method, even though
    * {@link #hasEffectiveManagedPlugin(Coordinate)} may return true.
    */
   boolean hasManagedPlugin(final Coordinate coordinate);

   /**
    * Return true if this {@link Project} contains a managed plugin matching the given {@link Coordinate}; return false
    * otherwise.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks only the immediate project managed plugins, meaning that if a managed plugin is
    * declared somewhere else in the hierarchy, it will not be detected by this method, even though
    * {@link #hasEffectiveManagedPlugin(Coordinate)} may return true.
    */
   boolean hasManagedPlugin(final Coordinate coordinate, Profile profile);

   /**
    * Return true if this {@link Project} contains a managed plugin matching the given {@link Coordinate} at any level
    * of the project hierarchy; return false otherwise.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    */
   boolean hasEffectiveManagedPlugin(final Coordinate coordinate);

   /**
    * Return true if this {@link Project} contains a managed plugin matching the given {@link Coordinate} at any level
    * of the project hierarchy; return false otherwise.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    */
   boolean hasEffectiveManagedPlugin(final Coordinate coordinate, Profile profile);

   /**
    * Remove the plugin given it's {@link Coordinate} from this {@link Project}.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method operates only the immediate project dependencies, meaning that if a plugin is declared
    * somewhere else in the hierarchy, it will not be removable by this method. You should call
    * {@link #hasPlugin(Coordinate)} first in order to check if the plugin exists in this projects immediate plugins
    * list.
    */
   void removePlugin(Coordinate coordinate);

   /**
    * Remove the plugin given it's {@link Coordinate} from this {@link Project}.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method operates only the immediate project dependencies, meaning that if a plugin is declared
    * somewhere else in the hierarchy, it will not be removable by this method. You should call
    * {@link #hasPlugin(Coordinate)} first in order to check if the plugin exists in this projects immediate plugins
    * list.
    */
   void removePlugin(Coordinate coordinate, Profile profile);

   /**
    * Remove the given managed plugin given it's {@link Coordinate} from this {@link Project}.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method operates only the immediate project managed plugins, meaning that if a managed plugin
    * is declared somewhere else in the hierarchy, it will not be removable by this method. You should call
    * {@link #hasManagedPlugin(Coordinate)} first in order to check if the managed plugin exists in this projects
    * immediate managed plugins.
    */
   void removeManagedPlugin(Coordinate coordinate);

   /**
    * Remove the given managed plugin given it's {@link Coordinate} from this {@link Project}.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method operates only the immediate project managed plugins, meaning that if a managed plugin
    * is declared somewhere else in the hierarchy, it will not be removable by this method. You should call
    * {@link #hasManagedPlugin(Coordinate)} first in order to check if the managed plugin exists in this projects
    * immediate managed plugins.
    */
   void removeManagedPlugin(Coordinate coordinate, Profile profile);

   /**
    * Update the given {@link MavenPlugin} to this {@link Project}'s immediate list of plugins. This method does not
    * check for existence of plugins in the hierarchy, instead, directly updates a direct plugin.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   void updatePlugin(final MavenPlugin plugin);

   /**
    * Update the given {@link MavenPlugin} to this {@link Project}'s immediate list of plugins. This method does not
    * check for existence of plugins in the hierarchy, instead, directly updates a direct plugin.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   void updatePlugin(final MavenPlugin plugin, Profile profile);

   /**
    * Update the given managed {@link MavenPlugin} to this {@link Project}'s immediate list of managed plugins. This
    * method first calls {@link #hasEffectiveManagedPlugin(MavenPlugin)} before making changes to the managed plugin
    * list.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   void updateManagedPlugin(final MavenPlugin plugin);

   /**
    * Update the given managed {@link MavenPlugin} to this {@link Project}'s immediate list of managed plugins. This
    * method first calls {@link #hasEffectiveManagedPlugin(MavenPlugin)} before making changes to the managed plugin
    * list.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   void updateManagedPlugin(final MavenPlugin plugin, Profile profile);

   /**
    * Add a repository to the project build system. This is where dependencies can be found, downloaded, and installed
    * to the project build script.
    */
   void addPluginRepository(String name, String url);

   /**
    * Return true if the given repository URL is already registered in this project's build system.
    */
   boolean hasPluginRepository(String url);

   /**
    * Remove the given {@link org.jboss.forge.project.dependencies.DependencyRepository} from the current project.
    * Return true if the repository was removed; return false otherwise. Return the removed repository, or null if no
    * repository was removed.
    */
   DependencyRepository removePluginRepository(String url);

   /**
    * Get the list of plugin repositories for which this project is currently configured to use in dependency
    * resolution.
    */
   List<DependencyRepository> getPluginRepositories();

   /**
    * Add a repository to the project build system. This is where dependencies can be found, downloaded, and installed
    * to the project build script.
    */
   void addPluginRepository(String name, String url, Profile profile);

   /**
    * Return true if the given repository URL is already registered in this project's build system.
    */
   boolean hasPluginRepository(String url, Profile profile);

   /**
    * Remove the given {@link org.jboss.forge.project.dependencies.DependencyRepository} from the current project.
    * Return true if the repository was removed; return false otherwise. Return the removed repository, or null if no
    * repository was removed.
    */
   DependencyRepository removePluginRepository(String url, Profile profile);

   /**
    * Get the list of plugin repositories for which this project is currently configured to use in dependency
    * resolution.
    */
   List<DependencyRepository> getPluginRepositories(Profile profile);

   /**
    * Merge two plugins, with one having dominance in the case of collision.
    * <p/>
    * <b>Notice:</b> To be merged, the two plugins dependencies must be equivalent (see
    * {@link Dependencies#areEquivalent(Coordinate, Coordinate)})
    */
   MavenPlugin merge(final MavenPlugin dominant, final MavenPlugin recessive);
}
