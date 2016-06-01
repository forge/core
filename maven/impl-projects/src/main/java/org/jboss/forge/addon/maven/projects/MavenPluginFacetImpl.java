/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Repository;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.util.Dependencies;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.plugins.MavenPlugin;
import org.jboss.forge.addon.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.addon.maven.projects.facets.exceptions.PluginNotFoundException;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.furnace.util.Strings;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */

public class MavenPluginFacetImpl extends AbstractFacet<Project>implements MavenPluginFacet
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

   @Override
   public List<MavenPlugin> listConfiguredPlugins()
   {
      return listConfiguredPlugins(false, false, null);
   }

   @Override
   public List<MavenPlugin> listConfiguredPlugins(org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return listConfiguredPlugins(false, false, profile);
   }

   @Override
   public List<MavenPlugin> listConfiguredEffectivePlugins()
   {
      return listConfiguredPlugins(false, true, null);
   }

   @Override
   public List<MavenPlugin> listConfiguredEffectivePlugins(org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return listConfiguredPlugins(false, true, profile);
   }

   @Override
   public List<MavenPlugin> listConfiguredManagedPlugins()
   {
      return listConfiguredPlugins(true, false, null);
   }

   @Override
   public List<MavenPlugin> listConfiguredManagedPlugins(org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return listConfiguredPlugins(true, false, profile);
   }

   @Override
   public List<MavenPlugin> listConfiguredEffectiveManagedPlugins()
   {
      return listConfiguredPlugins(true, true, null);
   }

   @Override
   public List<MavenPlugin> listConfiguredEffectiveManagedPlugins(org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return listConfiguredPlugins(true, true, profile);
   }

   @Override
   public void addPlugin(final MavenPlugin plugin)
   {
      addPlugin(plugin, false, null);
   }

   @Override
   public void addPlugin(MavenPlugin plugin, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      addPlugin(plugin, false, profile);
   }

   @Override
   public void addManagedPlugin(final MavenPlugin plugin)
   {
      addPlugin(plugin, true, null);
   }

   @Override
   public void addManagedPlugin(MavenPlugin plugin, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      addPlugin(plugin, true, profile);
   }

   @Override
   public MavenPlugin getPlugin(final Coordinate coordinate)
   {
      return getPlugin(coordinate, false, false, null);
   }

   @Override
   public MavenPlugin getPlugin(Coordinate coordinate, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return getPlugin(coordinate, false, false, profile);
   }

   @Override
   public MavenPlugin getEffectivePlugin(final Coordinate dependency)
   {
      return getPlugin(dependency, false, true, null);
   }

   @Override
   public MavenPlugin getEffectivePlugin(Coordinate coordinate, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return getPlugin(coordinate, false, true, profile);
   }

   @Override
   public MavenPlugin getManagedPlugin(final Coordinate dependency)
   {
      return getPlugin(dependency, true, false, null);
   }

   @Override
   public MavenPlugin getManagedPlugin(Coordinate coordinate, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return getPlugin(coordinate, true, false, profile);
   }

   @Override
   public MavenPlugin getEffectiveManagedPlugin(final Coordinate dependency)
   {
      return getPlugin(dependency, true, true, null);
   }

   @Override
   public MavenPlugin getEffectiveManagedPlugin(Coordinate coordinate,
            org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return getPlugin(coordinate, true, true, profile);
   }

   @Override
   public boolean hasPlugin(final Coordinate dependency)
   {
      return hasPlugin(dependency, false, false, null);
   }

   @Override
   public boolean hasPlugin(Coordinate coordinate, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return hasPlugin(coordinate, false, false, profile);
   }

   @Override
   public boolean hasEffectivePlugin(final Coordinate dependency)
   {
      return hasPlugin(dependency, false, true, null);
   }

   @Override
   public boolean hasEffectivePlugin(Coordinate coordinate, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return hasPlugin(coordinate, false, true, profile);
   }

   @Override
   public boolean hasManagedPlugin(final Coordinate dependency)
   {
      return hasPlugin(dependency, true, false, null);
   }

   @Override
   public boolean hasManagedPlugin(Coordinate coordinate, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return hasPlugin(coordinate, true, false, profile);
   }

   @Override
   public boolean hasEffectiveManagedPlugin(final Coordinate managedDependency)
   {
      return hasPlugin(managedDependency, true, true, null);
   }

   @Override
   public boolean hasEffectiveManagedPlugin(Coordinate coordinate, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      return hasPlugin(coordinate, true, true, profile);
   }

   @Override
   public void removePlugin(final Coordinate dependency)
   {
      removePlugin(dependency, false, null);
   }

   @Override
   public void removePlugin(Coordinate coordinate, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      removePlugin(coordinate, false, profile);
   }

   @Override
   public void removeManagedPlugin(final Coordinate dependency)
   {
      removePlugin(dependency, true, null);
   }

   @Override
   public void removeManagedPlugin(Coordinate coordinate, org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      removePlugin(coordinate, true, profileParam);

   }

   @Override
   public void updatePlugin(final MavenPlugin plugin)
   {
      updatePlugin(plugin, false, null);
   }

   @Override
   public void updatePlugin(MavenPlugin plugin, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      updatePlugin(plugin, false, profile);
   }

   @Override
   public void updateManagedPlugin(final MavenPlugin plugin)
   {
      updatePlugin(plugin, true, null);
   }

   @Override
   public void updateManagedPlugin(MavenPlugin plugin, org.jboss.forge.addon.maven.profiles.Profile profile)
   {
      updatePlugin(plugin, true, profile);
   }

   @Override
   public void addPluginRepository(final String id, final String url)
   {
      if (!hasPluginRepository(url))
      {
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
         Model pom = maven.getModel();
         Repository repo = new Repository();
         repo.setId(id);
         repo.setUrl(url);
         pom.getPluginRepositories().add(repo);
         maven.setModel(pom);
      }
   }

   @Override
   public void addPluginRepository(String id, String url, org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      if (profileParam == null)
         addPluginRepository(id, url);
      else
      {
         if (!hasPluginRepository(url, profileParam))
         {
            MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
            Model pom = maven.getModel();
            Profile profile = getProfile(pom, profileParam);
            if (profile == null)
            {
               profile = profileParam.getAsMavenProfile();
               pom.getProfiles().add(profile);
            }
            Repository repo = new Repository();
            repo.setId(id);
            repo.setUrl(url);
            profile.addPluginRepository(repo);
            maven.setModel(pom);
         }
      }
   }

   @Override
   public boolean hasPluginRepository(final String url)
   {
      if (url != null)
      {
         String trimmedUrl = url.trim();
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
         Model pom = maven.getModel();
         for (Repository repo : pom.getPluginRepositories())
         {
            if (repo.getUrl().trim().equals(trimmedUrl))
            {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public boolean hasPluginRepository(String url, org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      if (profileParam == null)
         return hasPluginRepository(url);
      else
      {
         if (url != null)
         {
            String trimmedUrl = url.trim();
            MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
            Model pom = maven.getModel();
            Profile profile = getProfile(pom, profileParam);
            if (profile != null)
            {
               for (Repository repo : profile.getPluginRepositories())
               {
                  if (repo.getUrl().trim().equals(trimmedUrl))
                  {
                     return true;
                  }
               }
            }
         }
         return false;
      }
   }

   @Override
   public DependencyRepository removePluginRepository(final String url)
   {
      if (url != null)
      {
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
         Model pom = maven.getModel();
         List<Repository> repos = pom.getPluginRepositories();
         for (Repository repo : repos)
         {
            if (repo.getUrl().equals(url.trim()))
            {
               repos.remove(repo);
               maven.setModel(pom);
               return new DependencyRepository(repo.getId(), repo.getUrl());
            }
         }
      }
      return null;
   }

   @Override
   public DependencyRepository removePluginRepository(String url,
            org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      if (profileParam == null)
         return removePluginRepository(url);
      else
      {
         if (url != null)
         {
            String trimmedUrl = url.trim();
            MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
            Model pom = maven.getModel();
            Profile profile = getProfile(pom, profileParam);
            if (profile != null)
            {
               List<Repository> repos = profile.getPluginRepositories();
               for (Repository repo : repos)
               {
                  if (repo.getUrl().equals(trimmedUrl))
                  {
                     repos.remove(repo);
                     maven.setModel(pom);
                     return new DependencyRepository(repo.getId(), repo.getUrl());
                  }
               }
            }
         }

      }
      return null;
   }

   @Override
   public List<DependencyRepository> getPluginRepositories()
   {
      List<DependencyRepository> results = new ArrayList<>();
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
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
   public List<DependencyRepository> getPluginRepositories(org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      if (profileParam == null)
         return getPluginRepositories();
      else
      {
         List<DependencyRepository> results = new ArrayList<>();
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
         Model pom = maven.getModel();
         Profile profile = getProfile(pom, profileParam);
         if (profile != null)
         {
            for (Repository repo : profile.getPluginRepositories())
            {
               results.add(new DependencyRepository(repo.getId(), repo.getUrl()));
            }
         }
         return Collections.unmodifiableList(results);
      }
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

   // Private methods
   private List<PluginExecution> mergePluginsExecutions(final Map<String, PluginExecution> dominant,
            final Map<String, PluginExecution> recessive)
   {
      List<PluginExecution> executions = new ArrayList<>();
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
         Map<String, Boolean> hasGoals = new HashMap<>();
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

   private List<org.apache.maven.model.Plugin> getPluginsPOM(boolean managedPlugin, boolean effectivePlugin,
            org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      MavenFacet mavenCoreFacet = getFaceted().getFacet(MavenFacet.class);
      BuildBase build = getBuild(mavenCoreFacet.getModel(), profileParam);
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

   private List<MavenPlugin> listConfiguredPlugins(boolean managedPlugin, boolean effectivePlugin,
            org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      List<MavenPlugin> plugins = new ArrayList<>();
      for (org.apache.maven.model.Plugin plugin : getPluginsPOM(managedPlugin, effectivePlugin, profileParam))
      {
         plugins.add(new MavenPluginAdapter(plugin));
      }
      return plugins;
   }

   private void addPlugin(final MavenPlugin plugin, boolean managedPlugin,
            org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      MavenFacet mavenCoreFacet = getFaceted().getFacet(MavenFacet.class);
      Model pom = mavenCoreFacet.getModel();
      BuildBase build = getBuild(pom, profileParam);
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
      mavenCoreFacet.setModel(pom);
   }

   private BuildBase getBuild(Model pom, org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      BuildBase build;
      if (profileParam == null)
      {
         // No Profile ID specified, use pom's <build>
         build = pom.getBuild();
         if (build == null)
         {
            pom.setBuild(new Build());
            build = pom.getBuild();
         }
      }
      else
      {
         Profile profile = getProfile(pom, profileParam);
         if (profile == null)
         {
            profile = profileParam.getAsMavenProfile();
            profile.setBuild(new Build());
            pom.getProfiles().add(profile);
         }
         build = profile.getBuild();
         if (build == null)
         {
            profile.setBuild(new Build());
            build = profile.getBuild();
         }
      }
      return build;
   }

   private MavenPlugin getPlugin(final Coordinate dependency, boolean managedPlugin, boolean effectivePlugin,
            org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      String groupId = dependency.getGroupId();
      groupId = (groupId == null) || groupId.equals("") ? DEFAULT_GROUPID : groupId;

      for (MavenPlugin mavenPlugin : listConfiguredPlugins(managedPlugin, effectivePlugin, profileParam))
      {
         Coordinate temp = mavenPlugin.getCoordinate();
         if (Dependencies.areEquivalent(temp, CoordinateBuilder.create(dependency).setGroupId(groupId)))
         {
            return mavenPlugin;
         }
      }

      throw new PluginNotFoundException(groupId, dependency.getArtifactId());
   }

   private Profile getProfile(Model model, org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      Profile result = null;
      if (profileParam != null)
      {
         for (Profile profile : model.getProfiles())
         {
            if (profileParam.getId().equals(profile.getId()))
            {
               result = profile;
               break;
            }
         }
      }
      return result;
   }

   private boolean hasPlugin(final Coordinate dependency, boolean managedPlugin, boolean effectivePlugin,
            org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      try
      {
         getPlugin(dependency, managedPlugin, effectivePlugin, profileParam);
         return true;
      }
      catch (PluginNotFoundException ex)
      {
         return false;
      }
   }

   private void removePlugin(final Coordinate dependency, boolean managedPlugin,
            org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      // Get plugin
      MavenPlugin pluginToRemove = null;
      if (managedPlugin && hasManagedPlugin(dependency, profileParam))
      {
         pluginToRemove = getManagedPlugin(dependency, profileParam);
      }
      else if (hasPlugin(dependency))
      {
         pluginToRemove = getPlugin(dependency, profileParam);
      }
      // Remove if it exists
      if (pluginToRemove != null)
      {
         MavenFacet mavenCoreFacet = getFaceted().getFacet(MavenFacet.class);
         Model pom = mavenCoreFacet.getModel();
         BuildBase build = getBuild(pom, profileParam); // We know for sure it isnt null because the plugin exists
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
         mavenCoreFacet.setModel(pom);
      }
   }

   private void updatePlugin(final MavenPlugin plugin, boolean managedPlugin,
            org.jboss.forge.addon.maven.profiles.Profile profileParam)
   {
      this.removePlugin(plugin.getCoordinate(), managedPlugin, profileParam);
      if (!this.hasPlugin(plugin.getCoordinate(), managedPlugin, false, profileParam))
      {
         this.addPlugin(plugin, managedPlugin, profileParam);
      }
   }
}
