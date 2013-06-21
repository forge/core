/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Repository;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.util.Dependencies;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.plugins.Execution;
import org.jboss.forge.addon.maven.plugins.MavenPlugin;
import org.jboss.forge.addon.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.addon.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.maven.projects.facets.exceptions.PluginNotFoundException;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.parser.java.util.Strings;

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
      return getFaceted().hasFacet(MavenFacet.class);
   }

   private List<org.apache.maven.model.Plugin> getPluginsPOM(boolean managedPlugin, boolean effectivePlugin)
   {
      MavenFacet mavenCoreFacet = getFaceted().getFacet(MavenFacet.class);
      Build build = mavenCoreFacet.getPOM().getBuild();
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

   private List<MavenPlugin> listConfiguredPlugins(boolean managedPlugin, boolean effectivePlugin)
   {
      List<MavenPlugin> plugins = new ArrayList<MavenPlugin>();
      List<org.apache.maven.model.Plugin> pomPlugins = getPluginsPOM(managedPlugin, effectivePlugin);
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
      MavenFacet mavenCoreFacet = getFaceted().getFacet(MavenFacet.class);
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

   private MavenPlugin getPlugin(final Coordinate dependency, boolean managedPlugin, boolean effectivePlugin)
   {
      String groupId = dependency.getGroupId();
      groupId = (groupId == null) || groupId.equals("") ? DEFAULT_GROUPID : groupId;

      for (MavenPlugin mavenPlugin : listConfiguredPlugins(managedPlugin, effectivePlugin))
      {
         Coordinate temp = mavenPlugin.getCoordinate();
         if (Dependencies.areEquivalent(temp, CoordinateBuilder.create(dependency).setGroupId(groupId)))
         {
            return mavenPlugin;
         }
      }

      throw new PluginNotFoundException(groupId, dependency.getArtifactId());
   }

   public boolean hasPlugin(final Coordinate dependency, boolean managedPlugin, boolean effectivePlugin)
   {
      try
      {
         getPlugin(dependency, managedPlugin, effectivePlugin);
         return true;
      }
      catch (PluginNotFoundException ex)
      {
         return false;
      }
   }

   private void removePlugin(final Coordinate dependency, boolean managedPlugin)
   {
      // Get plugin
      MavenPlugin pluginToRemove = null;
      if (managedPlugin && hasManagedPlugin(dependency))
      {
         pluginToRemove = getManagedPlugin(dependency);
      }
      else if (hasPlugin(dependency))
      {
         pluginToRemove = getPlugin(dependency);
      }
      // Remove if it exists
      if (pluginToRemove != null)
      {
         MavenFacet mavenCoreFacet = getFaceted().getFacet(MavenFacet.class);
         Model pom = mavenCoreFacet.getPOM();
         Build build = pom.getBuild(); // We know for sure it isnt null because the plugin exists
         if (managedPlugin)
         {
            PluginManagement pluginManagement = build.getPluginManagement(); // We know for sure it isnt null because
                                                                             // the plugin exists
            pluginManagement.removePlugin(new MavenPluginAdapter(pluginToRemove));
         }
         else
         {
            build.removePlugin(new MavenPluginAdapter(pluginToRemove));
         }
         pom.setBuild(build);
         mavenCoreFacet.setPOM(pom);
      }
   }

   private void updatePlugin(final MavenPlugin plugin, boolean managedPlugin)
   {
      this.removePlugin(plugin.getCoordinate(), managedPlugin);
      if (!this.hasPlugin(plugin.getCoordinate(), managedPlugin, false))
      {
         this.addPlugin(plugin, managedPlugin);
      }
   }

   @Override
   public List<MavenPlugin> listConfiguredPlugins()
   {
      return listConfiguredPlugins(false, false);
   }

   @Override
   public List<MavenPlugin> listConfiguredEffectivePlugins()
   {
      return listConfiguredPlugins(false, true);
   }

   @Override
   public List<MavenPlugin> listConfiguredManagedPlugins()
   {
      return listConfiguredPlugins(true, false);
   }

   @Override
   public List<MavenPlugin> listConfiguredEffectiveManagedPlugins()
   {
      return listConfiguredPlugins(true, true);
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
   public MavenPlugin getPlugin(final Coordinate coordinate)
   {
      return getPlugin(coordinate, false, false);
   }

   @Override
   public MavenPlugin getEffectivePlugin(final Coordinate dependency)
   {
      return getPlugin(dependency, false, true);
   }

   @Override
   public MavenPlugin getManagedPlugin(final Coordinate dependency)
   {
      return getPlugin(dependency, true, false);
   }

   @Override
   public MavenPlugin getEffectiveManagedPlugin(final Coordinate dependency)
   {
      return getPlugin(dependency, true, true);
   }

   @Override
   public boolean hasPlugin(final Coordinate dependency)
   {
      return hasPlugin(dependency, false, false);
   }

   @Override
   public boolean hasEffectivePlugin(final Coordinate dependency)
   {
      return hasPlugin(dependency, false, true);
   }

   @Override
   public boolean hasManagedPlugin(final Coordinate dependency)
   {
      return hasPlugin(dependency, true, false);
   }

   @Override
   public boolean hasEffectiveManagedPlugin(final Coordinate managedDependency)
   {
      return hasPlugin(managedDependency, true, true);
   }

   @Override
   public void removePlugin(final Coordinate dependency)
   {
      removePlugin(dependency, false);
   }

   @Override
   public void removeManagedPlugin(final Coordinate dependency)
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
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
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
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
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
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
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
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
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
   public MavenPlugin merge(final MavenPlugin dominant, final MavenPlugin recessive)
   {
      MavenPluginAdapter merged = new MavenPluginAdapter(dominant);
      if (Dependencies.areEquivalent(dominant.getCoordinate(), recessive.getCoordinate()))
      {
         MavenPluginAdapter recessiveAdaptater = new MavenPluginAdapter(recessive);
         // Merge the configurations
         Xpp3Dom mergedDomConfig = Xpp3Dom.mergeXpp3Dom((Xpp3Dom) merged.getConfiguration(),
                  (Xpp3Dom) recessiveAdaptater.getConfiguration());
         merged.setConfiguration(mergedDomConfig);
         // Merge the executions
         List<PluginExecution> mergedExecutions = mergePluginsExecutions(merged.getExecutionsAsMap(),
                  recessiveAdaptater.getExecutionsAsMap());
         merged.setExecutions(mergedExecutions);
         // Merge dependencies; only version required, we already know that groupId and artifactId are equals
         if (Strings.isNullOrEmpty(merged.getVersion()))
         {
            merged.setVersion(recessiveAdaptater.getVersion());
         }
         // Extension flag
         if (Strings.isNullOrEmpty(merged.getExtensions()))
         {
            merged.setExtensions(recessiveAdaptater.getExtensions());
         }
         // Inherited flag
         if (Strings.isNullOrEmpty(merged.getInherited()))
         {
            merged.setExtensions(recessiveAdaptater.getInherited());
         }
      }
      return merged;
   }

   private List<PluginExecution> mergePluginsExecutions(final Map<String, PluginExecution> dominant,
            final Map<String, PluginExecution> recessive)
   {
      List<PluginExecution> executions = new ArrayList<PluginExecution>();
      // Create a list of dominant executions, with the configurations merged with recessive if needed
      for (Map.Entry<String, PluginExecution> entry : dominant.entrySet())
      {
         PluginExecution pluginExecution = entry.getValue();
         PluginExecution mergedPluginExecution = new PluginExecution();
         mergedPluginExecution.setId(pluginExecution.getId());
         // Phase
         if (Strings.isNullOrEmpty(pluginExecution.getPhase()) && recessive.containsKey(entry.getKey()))
         {
            mergedPluginExecution.setPhase(recessive.get(entry.getKey()).getPhase());
         }
         else
         {
            mergedPluginExecution.setPhase(pluginExecution.getPhase());
         }
         // Goals
         Map<String, Boolean> hasGoals = new HashMap<String, Boolean>();
         for (String goal : pluginExecution.getGoals())
         {
            mergedPluginExecution.addGoal(goal);
            hasGoals.put(goal, new Boolean(true));
         }
         if (recessive.containsKey(entry.getKey()))
         {
            for (String goal : recessive.get(entry.getKey()).getGoals())
            {
               if (!hasGoals.containsKey(goal))
               {
                  mergedPluginExecution.addGoal(goal);
               }
            }
         }
         // Configurations
         if (pluginExecution.getConfiguration() != null)
         {
            if (recessive.containsKey(entry.getKey())
                     && recessive.get(entry.getKey()).getConfiguration() != null)
            {
               // Merge configurations
               Xpp3Dom mergedDomConfig = Xpp3Dom.mergeXpp3Dom((Xpp3Dom) pluginExecution.getConfiguration(),
                        (Xpp3Dom) recessive.get(entry.getKey()).getConfiguration());
               mergedPluginExecution.setConfiguration(mergedDomConfig);
            }
            else
            {
               // Keep master config
               mergedPluginExecution.setConfiguration(pluginExecution.getConfiguration());
            }
         }
         executions.add(mergedPluginExecution);
      }
      // Add the executions of the recessive that are not defined in dominant
      for (Map.Entry<String, PluginExecution> entry : recessive.entrySet())
      {
         if (!dominant.containsKey(entry.getKey()))
         {
            PluginExecution pluginExecution = entry.getValue();
            PluginExecution mergedPluginExecution = new PluginExecution();
            mergedPluginExecution.setId(pluginExecution.getId());
            mergedPluginExecution.setPhase(pluginExecution.getPhase());
            // Goals
            for (String goal : pluginExecution.getGoals())
            {
               mergedPluginExecution.addGoal(goal);
            }
            // Configuration
            if (pluginExecution.getConfiguration() != null)
            {
               mergedPluginExecution.setConfiguration(pluginExecution.getConfiguration());
            }
            executions.add(mergedPluginExecution);
         }
      }
      return executions;
   }
}
