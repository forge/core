/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.forge.maven;

import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyRepository;

import java.util.List;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public interface MavenPluginFacet extends Facet
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


   List<MavenPlugin> listConfiguredPlugins();

   boolean hasPlugin(Dependency dependency);

   MavenPlugin getPlugin(Dependency dependency);

   void addPlugin(MavenPlugin plugin);

   void removePlugin(Dependency dependency);

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
    * Remove the given {@link org.jboss.forge.project.dependencies.DependencyRepository} from the current project. Return true if the repository was removed;
    * return false otherwise. Return the removed repository, or null if no repository was removed.
    */
   public DependencyRepository removePluginRepository(String url);

   /**
    * Get the list of plugin repositories for which this project is currently configured to use in dependency resolution.
    */
   public List<DependencyRepository> getPluginRepositories();
}
