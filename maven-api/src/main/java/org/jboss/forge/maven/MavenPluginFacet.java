/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven;

import java.util.List;

import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 * @author <a href="mailto:salmon_charles@gmail.com">Charles-Edouard Salmon</a>
 */
public interface MavenPluginFacet extends Facet
{
   public enum KnownRepository
   {
      CENTRAL("http://repo1.maven.org/maven2/"),
      JBOSS_NEXUS("http://repository.jboss.org/nexus/content/groups/public"),
      JAVA_NET("http://download.java.net/maven/2/");

      private final String url;

      private KnownRepository(final String url)
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
    * Add the given {@link MavenPlugin} to this {@link Project}'s immediate list of plugins. This method does not
    * check for existence of plugins in the hierarchy, instead, directly adds or replaces a direct plugin.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   public void addPlugin(MavenPlugin plugin);
   
   /**
    * Add the given managed {@link MavenPlugin} to this {@link Project}'s immediate list of managed plugins. This
    * method does not check for existence of managed plugins in the hierarchy, instead, directly adds or replaces a
    * direct managed plugin.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   public void addManagedPlugin(MavenPlugin plugin);
   
   /**
    * Attempt to locate a plugin given it's {@link Dependency}, if it exists in the {@link Project} direct dependency list, and
    * return it. 
    * <p/>
    * See also: {@link MavenPluginBuilder}. See also: {@link #hasPlugin(Plugin)}. <br>
    * <br>
    * <b>Notice:</b> This method checks only the immediate project plugins, meaning that if a dependency is
    * declared somewhere else in the hierarchy, it will not be detected by this method.
    * 
    */
   public MavenPlugin getPlugin(Dependency dependency);
   
   /**
    * Attempt to locate a plugin given it's {@link Dependency}, if it exists anywhere in the {@link Project} plugin hierarchy,
    * and return it.
    * <p/>
    * See also: {@link MavenPluginBuilder}. See also: {@link #hasEffectivePlugin(Dependency)}.
    * 
    * @return
    */
   public MavenPlugin getEffectivePlugin(Dependency dependency);
   
   /**
    * Attempt to locate a managed a plugin given it's {@link Dependency}, if it exists in the {@link Project}, and return it.
    * <p/>
    * See also: {@link MavenPluginBuilder}. See also: {@link #hasManagedPlugin(Dependency)}.
    * 
    * @return
    */
   public MavenPlugin getManagedPlugin(Dependency dependency);
   
   /**
    * Searches {@link Project} and returns a managed plugin matching the given {@link Dependency} at any level of
    * the project hierarchy; return null otherwise.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   public MavenPlugin getEffectiveManagedPlugin(Dependency manDep);
   
   /**
    * Return an immutable list of all direct {@link MavenPlugin} contained within this project. (i.e.: all plugins
    * for which {@link #hasPlugin(Dependency)} returns true;
    */
   public List<MavenPlugin> listConfiguredPlugins();
   
   /**
    * Return an immutable list of all {@link MavenPlugin} contained within this project, including the hierarchy (i.e.: all plugins
    * for which {@link #hasEffectivePlugin(Dependency)} returns true;
    */
   public List<MavenPlugin> listConfiguredEffectivePlugins();

   /**
    * Return an immutable list of all direct managed {@link MavenPlugin} contained within this project. (i.e.: all
    * managed plugins for which {@link #hasManagedPlugin(Dependency)} returns true;
    */
   public List<MavenPlugin> listConfiguredManagedPlugins();
   
   /**
    * Return an immutable list of all managed {@link MavenPlugin} contained within this project, including the hierarchy. (i.e.: all
    * managed plugins for which {@link #hasEffectiveManagedPlugin(Dependency)} returns true;
    */
   public List<MavenPlugin> listConfiguredEffectiveManagedPlugins();
   
   /**
    * Return true if this {@link Project} contains a plugin matching the given {@link Dependency}; return false
    * otherwise. 
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks only the immediate project plugins, meaning that if a plugin is
    * declared somewhere else in the hierarchy, it will not be detected by this method, even though
    * {@link #hasEffectivePlugin(Dependency)} may return true.
    */
   public boolean hasPlugin(final Dependency dependency);
   
   /**
    * Return true if the given {@link Dependency} exists anywhere in the project dependency hierarchy. See also:
    * {@link MavenPluginBuilder}. See also: {@link #getEffectivePlugin(Dependency)}.
    */
   public boolean hasEffectivePlugin(final Dependency dependency);

   /**
    * Return true if this {@link Project} contains a managed plugin matching the given {@link Dependency}; return
    * false otherwise. 
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method checks only the immediate project managed plugins, meaning that if a managed
    * plugin is declared somewhere else in the hierarchy, it will not be detected by this method, even though
    * {@link #hasEffectiveManagedPlugin(Dependency)} may return true.
    */
   public boolean hasManagedPlugin(final Dependency dependency);
   
   /**
    * Return true if this {@link Project} contains a managed plugin matching the given {@link Dependency} at any
    * level of the project hierarchy; return false otherwise. 
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    */
   public boolean hasEffectiveManagedPlugin(final Dependency managedDependency);
   
   /**
    * Remove the plugin given it's {@link Dependency} from this {@link Project}. 
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method operates only the immediate project dependencies, meaning that if a plugin is
    * declared somewhere else in the hierarchy, it will not be removable by this method. You should call
    * {@link #hasPlugin(Dependency)} first in order to check if the plugin exists in this projects
    * immediate plugins list.
    */
   public void removePlugin(Dependency dependency);
   
   /**
    * Remove the given managed plugin given it's {@link Dependency} from this {@link Project}. 
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    * <p/>
    * <b>Notice:</b> This method operates only the immediate project managed plugins, meaning that if a managed
    * plugin is declared somewhere else in the hierarchy, it will not be removable by this method. You should call
    * {@link #hasManagedPlugin(Dependency)} first in order to check if the managed plugin exists in this
    * projects immediate managed plugins.
    */
   public void removeManagedPlugin(Dependency dependency);
   
   /**
    * Update the given {@link MavenPlugin} to this {@link Project}'s immediate list of plugins. This method does not
    * check for existence of plugins in the hierarchy, instead, directly updates a direct plugin.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   public void updatePlugin(final MavenPlugin plugin);
   
   /**
    * Update the given managed {@link MavenPlugin} to this {@link Project}'s immediate list of managed plugins. This
    * method first calls {@link #hasEffectiveManagedPlugin(MavenPlugin)} before making changes to the managed
    * plugin list.
    * <p/>
    * See also: {@link MavenPluginBuilder}.
    */
   public void updateManagedPlugin(final MavenPlugin plugin);

   /**
    * Add a {@link KnownRepository} to the project build system. This is where dependencies can be found, downloaded,
    * and installed to the project build script.
    */
   public void addPluginRepository(KnownRepository repository);

   /**
    * Add a repository to the project build system. This is where dependencies can be found, downloaded, and installed
    * to the project build script.
    */
   public void addPluginRepository(String name, String url);

   /**
    * Return true if the given {@link KnownRepository} is already registered in this project's build system.
    */
   public boolean hasPluginRepository(KnownRepository repository);

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
   
   /**
    * Merge two plugins, with one having dominance in the case of collision.
    * <p/>
    * <b>Notice:</b> To be merged, the two plugins dependencies must be equivalent (see {@link DependencyBuilder.areEquivalent})
    */
   public MavenPlugin merge(final MavenPlugin dominant, final MavenPlugin recessive);
}
