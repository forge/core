/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.facets;

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
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.facets.exceptions.PluginNotFoundException;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */

@Dependent
@Alias("forge.maven.MavenPluginFacet")
@RequiresFacet(MavenCoreFacet.class)
public class MavenPluginFacetImpl extends BaseFacet implements MavenPluginFacet, Facet
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
      try
      {
         project.getFacet(MavenCoreFacet.class);
         return true;
      }
      catch (FacetNotFoundException e)
      {
         return false;
      }
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
      MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
      Build build = mavenCoreFacet.getPOM().getBuild();
      List<org.apache.maven.model.Plugin> pomPlugins = getPluginsPOM(build, managedPlugin);
      for (org.apache.maven.model.Plugin plugin : pomPlugins)
      {
         MavenPluginAdapter adapter = new MavenPluginAdapter(plugin);
         MavenPluginBuilder pluginBuilder = MavenPluginBuilder
                  .create()
                  .setDependency(
                           DependencyBuilder.create().setGroupId(plugin.getGroupId())
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
      MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenCoreFacet.getPOM();
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
      mavenCoreFacet.setPOM(pom);
   }

   private MavenPlugin getPlugin(final Dependency dependency, boolean managedPlugin)
   {
      String groupId = dependency.getGroupId();
      groupId = (groupId == null) || groupId.equals("") ? DEFAULT_GROUPID : groupId;

      for (MavenPlugin mavenPlugin : listConfiguredPlugins(managedPlugin))
      {
         Dependency temp = mavenPlugin.getDependency();
         if (DependencyBuilder.areEquivalent(temp, DependencyBuilder.create(dependency).setGroupId(groupId)))
         {
            return mavenPlugin;
         }
      }

      throw new PluginNotFoundException(groupId, dependency.getArtifactId());
   }

   public boolean hasPlugin(final Dependency dependency, boolean managedPlugin)
   {
      try
      {
         getPlugin(dependency, managedPlugin);
         return true;
      }
      catch (PluginNotFoundException ex)
      {
         return false;
      }
   }

   private void removePlugin(final Dependency dependency, boolean managedPlugin)
   {
      MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenCoreFacet.getPOM();
      Build build = pom.getBuild();
      List<org.apache.maven.model.Plugin> pomPlugins = getPluginsPOM(build, managedPlugin);
      Iterator<Plugin> it = pomPlugins.iterator();
      while (it.hasNext())
      {
         org.apache.maven.model.Plugin pomPlugin = it.next();
         Dependency pluginDep = DependencyBuilder.create().setGroupId(pomPlugin.getGroupId())
                  .setArtifactId(pomPlugin.getArtifactId());

         if (DependencyBuilder.areEquivalent(pluginDep, dependency))
         {
            it.remove();
         }
      }
      mavenCoreFacet.setPOM(pom);
   }

   private void updatePlugin(final MavenPlugin plugin, boolean managedPlugin)
   {
      MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenCoreFacet.getPOM();
      Build build = pom.getBuild();
      List<org.apache.maven.model.Plugin> pomPlugins = getPluginsPOM(build, managedPlugin);
      for (org.apache.maven.model.Plugin pomPlugin : pomPlugins)
      {
         Dependency pluginDep = DependencyBuilder.create().setGroupId(pomPlugin.getGroupId())
                  .setArtifactId(pomPlugin.getArtifactId());

         if (DependencyBuilder.areEquivalent(pluginDep, plugin.getDependency()))
         {
            MavenPluginAdapter adapter = new MavenPluginAdapter(plugin);
            pomPlugin.setConfiguration(adapter.getConfiguration());
            mavenCoreFacet.setPOM(pom);
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
   public void addPluginRepository(final KnownRepository repository)
   {
      addPluginRepository(repository.name(), repository.getUrl());
   }

   @Override
   public void addPluginRepository(final String name, final String url)
   {
      if (!hasPluginRepository(url))
      {
         MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
         Model pom = maven.getPOM();
         Repository repo = new Repository();
         repo.setId(name);
         repo.setUrl(url);
         pom.getPluginRepositories().add(repo);
         maven.setPOM(pom);
      }
   }

   @Override
   public boolean hasPluginRepository(final KnownRepository repository)
   {
      return hasPluginRepository(repository.getUrl());
   }

   @Override
   public boolean hasPluginRepository(final String url)
   {
      if (url != null)
      {
         MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
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
         MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
         Model pom = maven.getPOM();
         List<Repository> repos = pom.getPluginRepositories();
         for (Repository repo : repos)
         {
            if (repo.getUrl().equals(url.trim()))
            {
               repos.remove(repo);
               maven.setPOM(pom);
               return new DependencyRepositoryImpl(repo.getId(), repo.getUrl());
            }
         }
      }
      return null;
   }

   @Override
   public List<DependencyRepository> getPluginRepositories()
   {
      List<DependencyRepository> results = new ArrayList<DependencyRepository>();
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      List<Repository> repos = pom.getPluginRepositories();

      if (repos != null)
      {
         for (Repository repo : repos)
         {
            results.add(new DependencyRepositoryImpl(repo.getId(), repo.getUrl()));
         }
      }
      return Collections.unmodifiableList(results);
   }

}
