/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Repository;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.dependencies.Coordinate;
import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.DependencyRepository;
import org.jboss.forge.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.dependencies.util.Dependencies;
import org.jboss.forge.facets.AbstractFacet;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.projects.Project;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */

@Dependent
public class MavenPluginFacetImpl extends AbstractFacet<Project> implements MavenPluginFacet
{
   private static final String DEFAULT_GROUPID = "org.apache.maven.plugins";

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return getOrigin().hasFacet(MavenFacet.class);
   }

   private List<org.apache.maven.model.Plugin> getPluginsPOM(Build build, boolean managedPlugin)
   {
      if (build != null)
      {
         if (managedPlugin)
         {
            PluginManagement pluginManagement = build.getPluginManagement();
            if (pluginManagement != null)
            {
               return pluginManagement.getPlugins();
            }
         }
         else
         {
            return build.getPlugins();
         }
      }
      return Collections.emptyList();
   }

   private List<MavenPlugin> listConfiguredPlugins(boolean managedPlugin)
   {
      List<MavenPlugin> plugins = new ArrayList<MavenPlugin>();
      MavenFacet mavenCoreFacet = getOrigin().getFacet(MavenFacet.class);
      Build build = mavenCoreFacet.getPOM().getBuild();
      List<org.apache.maven.model.Plugin> pomPlugins = getPluginsPOM(build, managedPlugin);
      for (org.apache.maven.model.Plugin plugin : pomPlugins)
      {
         MavenPluginAdapter adapter = new MavenPluginAdapter(plugin);
         MavenPluginBuilder pluginBuilder = MavenPluginBuilder
                  .create()
                  .setCoordinate(
                           CoordinateBuilder.create().setGroupId(plugin.getGroupId())
                                    .setArtifactId(plugin.getArtifactId()).setVersion(plugin.getVersion()))

                  .setConfiguration(adapter.getConfig());
         for (Execution execution : adapter.listExecutions())
         {
            pluginBuilder.addExecution(execution);
         }
         plugins.add(pluginBuilder);
      }
      return plugins;
   }

   private void addPlugin(final MavenPlugin plugin, boolean managedPlugin)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      Build build = pom.getBuild();
      if (build == null)
         build = new Build();
      if (managedPlugin)
      {
         PluginManagement pluginManagement = build.getPluginManagement();
         if (pluginManagement == null)
         {
            pluginManagement = new PluginManagement();
            build.setPluginManagement(pluginManagement);
         }
         pluginManagement.addPlugin(new MavenPluginAdapter(plugin));
      }
      else
      {
         build.addPlugin(new MavenPluginAdapter(plugin));
      }
      pom.setBuild(build);
      maven.setPOM(pom);
   }

   private MavenPlugin getPlugin(final Dependency dependency, boolean managedPlugin)
   {
      String groupId = dependency.getCoordinate().getGroupId();
      groupId = (groupId == null) || groupId.equals("") ? DEFAULT_GROUPID : groupId;

      for (MavenPlugin mavenPlugin : listConfiguredPlugins(managedPlugin))
      {
         Coordinate temp = mavenPlugin.getCoordinate();
         if (temp.equals(CoordinateBuilder.create(temp).setGroupId(groupId)))
         {
            return mavenPlugin;
         }
      }

      throw new ContainerException("Plugin " + dependency.getCoordinate() + " was not found");
   }

   public boolean hasPlugin(final Dependency dependency, boolean managedPlugin)
   {
      try
      {
         getPlugin(dependency, managedPlugin);
         return true;
      }
      catch (ContainerException ex)
      {
         return false;
      }
   }

   private void removePlugin(final Dependency dependency, boolean managedPlugin)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      Build build = pom.getBuild();
      List<org.apache.maven.model.Plugin> pomPlugins = getPluginsPOM(build, managedPlugin);
      Iterator<Plugin> it = pomPlugins.iterator();
      while (it.hasNext())
      {
         org.apache.maven.model.Plugin pomPlugin = it.next();
         Dependency pluginDep = DependencyBuilder.create().setGroupId(pomPlugin.getGroupId())
                  .setArtifactId(pomPlugin.getArtifactId());

         if (Dependencies.areEquivalent(pluginDep, dependency))
         {
            it.remove();
         }
      }
      maven.setPOM(pom);
   }

   private void updatePlugin(final MavenPlugin plugin, boolean managedPlugin)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      Build build = pom.getBuild();
      List<org.apache.maven.model.Plugin> pomPlugins = getPluginsPOM(build, managedPlugin);
      for (org.apache.maven.model.Plugin pomPlugin : pomPlugins)
      {
         Coordinate pluginCoord = CoordinateBuilder.create().setGroupId(pomPlugin.getGroupId())
                  .setArtifactId(pomPlugin.getArtifactId());

         if (pluginCoord.equals(plugin.getCoordinate()))
         {
            MavenPluginAdapter adapter = new MavenPluginAdapter(plugin);
            pomPlugin.setConfiguration(adapter.getConfiguration());
            maven.setPOM(pom);
            break;
         }
      }
   }

   @Override
   public List<MavenPlugin> listConfiguredPlugins()
   {
      return listConfiguredPlugins(false);
   }

   @Override
   public List<MavenPlugin> listConfiguredManagedPlugins()
   {
      return listConfiguredPlugins(true);
   }

   @Override
   public void addPlugin(final MavenPlugin plugin)
   {
      addPlugin(plugin, false);
   }

   @Override
   public void addManagedPlugin(final MavenPlugin plugin)
   {
      addPlugin(plugin, true);
   }

   @Override
   public MavenPlugin getPlugin(final Dependency dependency)
   {
      return getPlugin(dependency, false);
   }

   @Override
   public MavenPlugin getManagedPlugin(final Dependency dependency)
   {
      return getPlugin(dependency, true);
   }

   @Override
   public boolean hasPlugin(final Dependency dependency)
   {
      return hasPlugin(dependency, false);
   }

   @Override
   public boolean hasManagedPlugin(final Dependency dependency)
   {
      return hasPlugin(dependency, true);
   }

   @Override
   public void removePlugin(final Dependency dependency)
   {
      removePlugin(dependency, false);
   }

   @Override
   public void removeManagedPlugin(final Dependency dependency)
   {
      removePlugin(dependency, true);
   }

   @Override
   public void updatePlugin(final MavenPlugin plugin)
   {
      updatePlugin(plugin, false);
   }

   @Override
   public void updateManagedPlugin(final MavenPlugin plugin)
   {
      updatePlugin(plugin, true);
   }

   @Override
   public void addPluginRepository(final String name, final String url)
   {
      if (!hasPluginRepository(url))
      {
         MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
         Model pom = maven.getPOM();
         Repository repo = new Repository();
         repo.setId(name);
         repo.setUrl(url);
         pom.getPluginRepositories().add(repo);
         maven.setPOM(pom);
      }
   }

   @Override
   public boolean hasPluginRepository(final String url)
   {
      if (url != null)
      {
         MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
         Model pom = maven.getPOM();
         List<Repository> repositories = pom.getPluginRepositories();
         if (repositories != null)
         {
            for (Repository repo : repositories)
            {
               if (repo.getUrl().trim().equals(url.trim()))
               {
                  repositories.remove(repo);
                  maven.setPOM(pom);
                  return true;
               }
            }
         }
      }
      return false;
   }

   @Override
   public DependencyRepository removePluginRepository(final String url)
   {
      if (url != null)
      {
         MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
         Model pom = maven.getPOM();
         List<Repository> repos = pom.getPluginRepositories();
         for (Repository repo : repos)
         {
            if (repo.getUrl().equals(url.trim()))
            {
               repos.remove(repo);
               maven.setPOM(pom);
               return new DependencyRepository(repo.getId(), repo.getUrl());
            }
         }
      }
      return null;
   }

   @Override
   public List<DependencyRepository> getPluginRepositories()
   {
      List<DependencyRepository> results = new ArrayList<DependencyRepository>();
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      List<Repository> repos = pom.getPluginRepositories();

      if (repos != null)
      {
         for (Repository repo : repos)
         {
            results.add(new DependencyRepository(repo.getId(), repo.getUrl()));
         }
      }
      return Collections.unmodifiableList(results);
   }

   @Override
   public void setOrigin(final Project project)
   {
      super.setOrigin(project);
   }

}
