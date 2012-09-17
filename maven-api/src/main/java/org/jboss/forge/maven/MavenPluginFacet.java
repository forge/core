/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven;

import java.util.List;

import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyRepository;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
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

   List<MavenPlugin> listConfiguredPlugins();

   boolean hasPlugin(Dependency dependency);

   MavenPlugin getPlugin(Dependency dependency);

   void addPlugin(MavenPlugin plugin);

   void removePlugin(Dependency dependency);
   
   void updatePlugin(final MavenPlugin plugin);

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
}
