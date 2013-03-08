/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.projects.facets;

import java.util.List;
import java.util.Map;

import org.jboss.forge.dependencies.Coordinate;
import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.DependencyQuery;
import org.jboss.forge.dependencies.DependencyRepository;
import org.jboss.forge.dependencies.DependencyResolver;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.dependencies.collection.DependencyNodeUtil;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface DependencyFacet extends ProjectFacet
{
   /**
    * Add the given {@link Dependency} to this {@link Project}'s immediate list of dependencies. This method does not
    * check for existence of dependencies in the hierarchy, instead, directly adds or replaces a direct dependency.
    * <p/>
    * See also: {@link DependencyBuilder}.
    */
   public void addDirectDependency(Dependency dep);

   /**
    * Add the given managed {@link Dependency} to this {@link Project}'s immediate list of managed dependencies. This
    * method first calls {@link #hasEffectiveManagedDependency(Dependency)} before making changes to the managed
    * dependency list.
    * <p/>
    * See also: {@link DependencyBuilder}.
    */
   public void addManagedDependency(Dependency managedDependency);

   /**
    * Add the given managed {@link Dependency} to this {@link Project}'s immediate list of managed dependencies. This
    * method does not check for existence of managed dependencies in the hierarchy, instead, directly adds or replaces a
    * direct managed dependency.
    * <p/>
    * See also: {@link DependencyBuilder}.
    */
   public void addDirectManagedDependency(Dependency dep);

   /**
    * Add a repository to the project build system. This is where dependencies can be found, downloaded, and installed
    * to the project build script.
    */
   public void addRepository(String name, String url);

   /**
    * Return an immutable list of all direct {@link DependencyNodeUtil} contained within this project. (i.e.: all dependencies
    * for which {@link DependencyFacet#hasDirectDependency(Dependency)} returns true;
    */
   public List<Dependency> getDependencies();

   /**
    * Get a list of this {@link Project}'s dependencies of the given scopes.. See also: {@link DependencyBuilder}. See
    * also: {@link #getDependency(Dependency)}.
    */
   public List<Dependency> getDependenciesInScopes(String... scopes);

   /**
    * Attempt to locate the given {@link Dependency}, if it exists in the {@link Project} direct dependency list, and
    * return it.
    * <p/>
    * See also: {@link DependencyBuilder}. See also: {@link #hasDirectDependency(Dependency)}. <br>
    * <br>
    * <b>Notice:</b> This method checks only the immediate project dependencies, meaning that if a dependency is
    * declared somewhere else in the hierarchy, it will not be detected by this method, even though by
    * {@link #hasDependency(Dependency)} may return true.
    * 
    * @return
    */
   public Dependency getDirectDependency(Dependency dependency);

   /**
    * Return an immutable list of all {@link DependencyNodeUtil} contained anywhere within this project's dependency
    * hierarchy. (i.e.: all dependencies for which {@link DependencyFacet#hasEffectiveDependency(Dependency)} returns
    * true;
    */
   List<Dependency> getEffectiveDependencies();

   /**
    * Get a list of this {@link Project}'s dependencies of the given scopes, from anywhere in the dependency hierarchy.
    * See also: {@link DependencyBuilder}. See also: {@link #getEffectiveDependency(Dependency)}.
    */
   public List<Dependency> getEffectiveDependenciesInScopes(String... scopes);

   /**
    * Attempt to locate the given {@link Dependency}, if it exists anywhere in the {@link Project} dependency hierarchy,
    * and return it.
    * <p/>
    * See also: {@link DependencyBuilder}. See also: {@link #hasEffectiveDependency(Dependency)}.
    * 
    * @return
    */
   Dependency getEffectiveDependency(Dependency dependency);

   /**
    * Searches {@link Project} and returns a managed dependency matching the given {@link Dependency} at any level of
    * the project hierarchy; return null otherwise. This method ignores {@link Dependency#getScopeType()}
    * <p/>
    * See also: {@link DependencyBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks the entire project managed dependency structure, meaning that if a managed
    * dependency is declared somewhere else in the hierarchy, it will not be detected by
    * {@link #getManagedDependency(Dependency)} and will not be removable via
    * {@link #removeManagedDependency(Dependency)}.
    */
   public Dependency getEffectiveManagedDependency(Dependency manDep);

   /**
    * Return an immutable list of all direct managed {@link DependencyNodeUtil} contained within this project. (i.e.: all
    * managed dependencies for which {@link ManagedDependencyFacet#hasManagedDependency(Dependency)} returns true;
    */
   public List<Dependency> getManagedDependencies();

   /**
    * Attempt to locate the given managed {@link Dependency}, if it exists in the {@link Project}, and return it.
    * <p/>
    * See also: {@link DependencyBuilder}. See also: {@link #hasEffectiveManagedDependency(Dependency)}.
    * 
    * @return
    */
   public Dependency getManagedDependency(Dependency managedDependency);

   /**
    * Return a list of all build dependency properties.(Build properties such, as ${my.version}, can be used anywhere in
    * a dependency, and will be expanded during building to their property value.)
    */
   public Map<String, String> getProperties();

   /**
    * Get a build property by name. (Build properties, such as ${my.version}, can be used anywhere in a dependency, and
    * will be expanded during building to their property value.)
    */
   public String getProperty(String name);

   /**
    * Get the list of repositories for which this project is currently configured to use in dependency resolution.
    */
   public List<DependencyRepository> getRepositories();

   /**
    * Return true if this {@link Project} contains a dependency matching the given {@link Dependency}; return false
    * otherwise. This method ignores {@link Dependency#getScopeType()}
    * <p/>
    * See also: {@link DependencyBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks only the immediate project dependencies, meaning that if a dependency is
    * declared somewhere else in the hierarchy, it will not be detected by this method, even though by
    * {@link #hasDependency(Dependency)} may return true.
    */
   public boolean hasDirectDependency(Dependency dependency);

   /**
    * Return true if the given {@link Dependency} exists anywhere in the project dependency hierarchy. See also:
    * {@link DependencyBuilder}. See also: {@link #getEffectiveDependency(Dependency)}.
    */
   boolean hasEffectiveDependency(Dependency dependency);

   /**
    * Return true if this {@link Project} contains a managed dependency matching the given {@link Dependency} at any
    * level of the project hierarchy; return false otherwise. This method ignores {@link Dependency#getScopeType()}
    * <p/>
    * See also: {@link DependencyBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks the entire project managed dependency structure, meaning that if a managed
    * dependency is declared somewhere else in the hierarchy, it will not be detected by
    * {@link #hasManagedDependency(Dependency)} and will not be removable via
    * {@link #removeManagedDependency(Dependency)}.
    */
   public boolean hasEffectiveManagedDependency(Dependency managedDependency);

   /**
    * Return true if this {@link Project} contains a managed dependency matching the given {@link Dependency}; return
    * false otherwise. This method ignores {@link Dependency#getScopeType()}
    * <p/>
    * See also: {@link DependencyBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks only the immediate project managed dependencies, meaning that if a managed
    * dependency is declared somewhere else in the hierarchy, it will not be detected by this method, even though
    * {@link #hasEffectiveManagedDependency(Dependency)} may return true.
    */
   public boolean hasDirectManagedDependency(Dependency managedDependency);

   /**
    * Return true if the given repository URL is already registered in this project's build system.
    */
   public boolean hasRepository(String url);

   /**
    * Remove the given {@link Dependency} from this facet's {@link Project}. This method ignores
    * {@link Dependency#getScopeType()}
    * <p/>
    * See also: {@link DependencyBuilder}.
    * <p/>
    * <b>Notice:</b> This method operates only the immediate project dependencies, meaning that if a dependency is
    * declared somewhere else in the hierarchy, it will not be removable by this method. You should call
    * {@link #hasDirectDependency(Dependency)} first in order to check if the dependency exists in this projects
    * immediate dependencies.
    */
   public void removeDependency(Dependency dependency);

   /**
    * Remove the given managed {@link Dependency} from this facet's {@link Project}. This method ignores
    * {@link Dependency#getScopeType()}
    * <p/>
    * See also: {@link DependencyBuilder}.
    * <p/>
    * <b>Notice:</b> This method operates only the immediate project managed dependencies, meaning that if a managed
    * dependency is declared somewhere else in the hierarchy, it will not be removable by this method. You should call
    * {@link #hasManagedDependency(Dependency)} first in order to check if the managed dependency exists in this
    * projects immediate managed dependencies.
    */
   public void removeManagedDependency(Dependency managedDependency);

   /**
    * Remove a build property by name. (Build properties, such as ${my.version}, can be used anywhere in a dependency,
    * and will be expanded during building to their property value.)
    */
   public String removeProperty(String name);

   /**
    * Remove the given {@link DependencyRepository} from the current project. Return true if the repository was removed;
    * return false otherwise. Return the removed repository, or null if no repository was removed.
    */
   public DependencyRepository removeRepository(String url);

   /**
    * Given a {@link Dependency} with a populated groupId, versionId, and version range, identify the available
    * {@link Coordinate} in all known repositories for this project. By default, SNAPSHOT versions are excluded.
    * 
    * See {@link DependencyFacet#resolveAvailableVersions(String)}. For more comprehensive resolution features, see
    * {@link DependencyResolver}
    */
   public List<Coordinate> resolveAvailableVersions(final Dependency dep);

   /**
    * Given a groupid:versionid:version-range, identify and resolve all matching {@link Coordinate} in all known
    * {@link DependencyRepository} instances for this {@link Project}. By default, SNAPSHOT versions are excluded. For
    * example:
    * <p>
    * <code>dependencyFacet.resolveAvailableVersions("org.jboss.forge:example:[1.0.0,]");</code><br>
    * <code>dependencyFacet.resolveAvailableVersions("org.jboss.forge:example:[1.0.0,)");</code><br>
    * <code>dependencyFacet.resolveAvailableVersions("org.jboss.forge:example:(1.0.0,3.0.0]");</code><br>
    * <code>dependencyFacet.resolveAvailableVersions("org.jboss.forge:example:[1.0.0,3.0.0]");</code>
    * <p>
    * For more comprehensive resolution features, see {@link #resolveAvailableVersions(DependencyQuery)} or
    * {@link DependencyResolver}
    */
   public List<Coordinate> resolveAvailableVersions(final String gavs);

   /**
    * Using the given {@link DependencyQuery}, identify and resolve all matching {@link Coordinate} results in
    * configured {@link DependencyRepository} instances for this {@link Project}. See also,
    * {@link #resolveAvailableVersions(String)} and {@link #resolveAvailableVersions(Dependency)}
    */
   public List<Coordinate> resolveAvailableVersions(DependencyQuery query);

   /**
    * Resolve properties in the given dependency, converting them to their actual values.
    */
   public Dependency resolveProperties(Dependency dependency);

   /**
    * Set a build dependency property. (Build properties, such as ${my.version}, can be used anywhere in a dependency,
    * and will be expanded during building to their property value.)
    */
   public void setProperty(String name, String value);

}
