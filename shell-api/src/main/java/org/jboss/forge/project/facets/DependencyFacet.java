/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.project.facets;

import java.util.List;
import java.util.Map;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.dependencies.DependencyResolver;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface DependencyFacet extends Facet
{
   public enum KnownRepository
   {
      CENTRAL("http://repo1.maven.org/maven2/"),
      JBOSS_NEXUS("http://repository.jboss.org/nexus/content/groups/public"),
      JBOSS_LEGACY("http://repository.jboss.org/maven2"),
      JAVA_NET("http://download.java.net/maven/2/");

      private final String url;

      private KnownRepository(String url)
      {
         this.url = url;
      }

      public String getUrl()
      {
         return url;
      }

      public String getId()
      {
         return this.name();
      }
   }

   /**
    * Return true if this {@link Project} contains a dependency matching the given {@link Dependency} at any level of
    * the project hierarchy; return false otherwise. This method ignores {@link Dependency#getScopeType()}
    * <p/>
    * See also: {@link DependencyBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks the entire project dependency structure, meaning that if a dependency is
    * declared somewhere else in the hierarchy, it will not be detected by {@link #hasDirectDependency(Dependency)} and
    * will not be removable via {@link #removeDependency(Dependency)}.
    */
   public boolean hasDependency(Dependency dependency);

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
    * Add the given {@link Dependency} to this {@link Project}'s immediate list of dependencies. This method first calls
    * {@link #hasDependency(Dependency)} before making changes to the dependency list.
    * <p/>
    * See also: {@link DependencyBuilder}.
    */
   public void addDependency(Dependency dependency);

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
    * Return an immutable list of all direct {@link Dependencies} contained within this project. (i.e.: all dependencies
    * for which {@link DependencyFacet#hasDirectDependency(Dependency)} returns true;
    */
   public List<Dependency> getDependencies();

   /**
    * Attempt to locate the given {@link Dependency}, if it exists in the {@link Project}, and return it.
    * <p/>
    * See also: {@link DependencyBuilder}. See also: {@link #hasDependency(Dependency)}.
    * 
    * @return
    */
   public Dependency getDependency(Dependency dependency);

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
    * Searches {@link Project} and returns a managed dependency matching the given {@link Dependency} at any
    * level of the project hierarchy; return null otherwise. This method ignores {@link Dependency#getScopeType()}
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
    * Return true if this {@link Project} contains a managed dependency matching the given {@link Dependency}; return
    * false otherwise. This method ignores {@link Dependency#getScopeType()}
    * <p/>
    * See also: {@link DependencyBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks only the immediate project managed dependencies, meaning that if a managed
    * dependency is declared somewhere else in the hierarchy, it will not be detected by this method, even though
    * {@link #hasEffectiveManagedDependency(Dependency)} may return true.
    */
   public boolean hasManagedDependency(Dependency managedDependency);

   /**
    * Add the given managed {@link Dependency} to this {@link Project}'s immediate list of managed dependencies. This
    * method first calls {@link #hasEffectiveManagedDependency(Dependency)} before making changes to the managed dependency list.
    * <p/>
    * See also: {@link DependencyBuilder}.
    */
   public void addManagedDependency(Dependency managedDependency);

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
    * Return an immutable list of all direct managed {@link Dependencies} contained within this project. (i.e.: all
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
    * Set a build dependency property. (Build properties, such as ${my.version}, can be used anywhere in a dependency,
    * and will be expanded during building to their property value.)
    */
   public void setProperty(String name, String value);

   /**
    * Get a build property by name. (Build properties, such as ${my.version}, can be used anywhere in a dependency, and
    * will be expanded during building to their property value.)
    */
   public String getProperty(String name);

   /**
    * Remove a build property by name. (Build properties, such as ${my.version}, can be used anywhere in a dependency,
    * and will be expanded during building to their property value.)
    */
   public String removeProperty(String name);

   /**
    * Add a {@link KnownRepository} to the project build system. This is where dependencies can be found, downloaded,
    * and installed to the project build script.
    */
   public void addRepository(KnownRepository repository);

   /**
    * Add a repository to the project build system. This is where dependencies can be found, downloaded, and installed
    * to the project build script.
    */
   public void addRepository(String name, String url);

   /**
    * Return true if the given {@link KnownRepository} is already registered in this project's build system.
    */
   public boolean hasRepository(KnownRepository repository);

   /**
    * Return true if the given repository URL is already registered in this project's build system.
    */
   public boolean hasRepository(String url);

   /**
    * Remove the given {@link DependencyRepository} from the current project. Return true if the repository was removed;
    * return false otherwise. Return the removed repository, or null if no repository was removed.
    */
   public DependencyRepository removeRepository(String url);

   /**
    * Get the list of repositories for which this project is currently configured to use in dependency resolution.
    */
   public List<DependencyRepository> getRepositories();

   /**
    * Given a groupid:versionid:version-range, identify the available artifacts in all known repositories for this
    * project. For example:
    * <p>
    * <code>dependencyFacet.resolveAvailableVersions("org.jboss.forge:example:[1.0.0,]");</code><br>
    * <code>dependencyFacet.resolveAvailableVersions("org.jboss.forge:example:[1.0.0,)");</code><br>
    * <code>dependencyFacet.resolveAvailableVersions("org.jboss.forge:example:(1.0.0,3.0.0]");</code><br>
    * <code>dependencyFacet.resolveAvailableVersions("org.jboss.forge:example:[1.0.0,3.0.0]");</code>
    * <p>
    * For more comprehensive resolution features, see {@link DependencyResolver}
    */
   public List<Dependency> resolveAvailableVersions(final String gavs);

   /**
    * Given a {@link Dependency} with a populated groupId, versionId, and version range, identify the available
    * artifacts in all known repositories for this project.
    * 
    * See {@link DependencyFacet#resolveAvailableVersions(String)}. For more comprehensive resolution features, see
    * {@link DependencyResolver}
    */
   public List<Dependency> resolveAvailableVersions(final Dependency dep);


}
