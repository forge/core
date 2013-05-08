package org.jboss.forge.maven;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationImpl;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.maven.plugins.MavenPluginInstaller;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyFilter;
import org.jboss.forge.project.dependencies.DependencyQuery;
import org.jboss.forge.project.dependencies.DependencyQueryBuilder;
import org.jboss.forge.project.dependencies.NonSnapshotDependencyFilter;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;

/**
 * Responsible for installing a given {@link MavenPlugin} into the current project..
 * 
 * @author <a href="mailto:salmon_charles@gmail.com">Charles-Edouard Salmon</a>
 */
public class MavenPluginInstallImpl implements MavenPluginInstaller
{
   private final ShellPrompt prompt;

   /**
    * Merge the plugin definition with existing configuration
    */
   private boolean mergeWithExisting = true;

   /**
    * Filter plugin definition with existing hierarchy configuration All properties having equivalent counterparts in
    * the hierarchy (in plugin or plugin mamnagement sections of the parent) will be removed from the new plugin
    * definition (to preserve hierarchy precedence.
    */
   private boolean preserveHierarchyPrecedence = true;

   @Inject
   public MavenPluginInstallImpl(final ShellPrompt prompt)
   {
      this.prompt = prompt;
   }

   private MavenPlugin install(Project project, final MavenPlugin plugin, boolean managed)
   {
      MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      Dependency pluginCoordinates = DependencyBuilder.create().setGroupId(plugin.getDependency().getGroupId())
               .setArtifactId(plugin.getDependency().getArtifactId());
      MavenPlugin managedPlugin = null;
      if (plugins.hasManagedPlugin(pluginCoordinates))
      {
         managedPlugin = plugins.getManagedPlugin(pluginCoordinates);
      }

      MavenPlugin existing = null;
      // existing represents the plugin(management) as it exists currently throughout the entire hierarchy
      if (managed && plugins.hasEffectiveManagedPlugin(pluginCoordinates))
      {
         existing = plugins.getEffectiveManagedPlugin(pluginCoordinates);
         if (plugins.hasManagedPlugin(pluginCoordinates) && !mergeWithExisting)
         {
            // If no merge, existing should not have any of the direct managed-plugin configuration
            existing = diff(existing, plugins.getManagedPlugin(pluginCoordinates));
         }
      }
      else if (plugins.hasEffectivePlugin(pluginCoordinates))
      {
         existing = plugins.getEffectivePlugin(pluginCoordinates);
         if (plugins.hasPlugin(pluginCoordinates) && !mergeWithExisting)
         {
            // If no merge, existing should not have any of the direct plugin configuration
            existing = diff(existing, plugins.getPlugin(pluginCoordinates));
         }
      }

      MavenPlugin filteredPlugin = plugin;
      // The filtered plugin preserve the hierarchy, by preventing installing properties already defined with the same
      // values
      if (existing != null && preserveHierarchyPrecedence)
      {
         filteredPlugin = diff(plugin, existing);
      }
      // Preserve direct plugin-management inheritance
      if (!managed && managedPlugin != null)
      {
         // The plugin section does not exists but a plugin management section in the direct pom does
         filteredPlugin = diff(filteredPlugin, managedPlugin);
      }

      MavenPlugin mergedPlugin = filteredPlugin;
      // merged plugin is a merge with the direct plugin(management)
      if (mergeWithExisting)
      {
         if (managed && managedPlugin != null)
         {
            mergedPlugin = plugins.merge(mergedPlugin, managedPlugin);
         }
         else if (!managed && plugins.hasPlugin(pluginCoordinates))
         {
            mergedPlugin = plugins.merge(mergedPlugin, plugins.getPlugin(pluginCoordinates));
         }
      }

      // Resolve version
      String versionToInstall = plugin.getDependency().getVersion();
      if (mergedPlugin.getDependency().getVersion() == null)
      {
         // null version means no version was specified or already defined in the hierarchy
         if (versionToInstall == null)
         {
            versionToInstall = promptVersion(deps, pluginCoordinates, null).getVersion();
         }
      }

      // Install the plugin
      MavenPluginAdapter pluginToInstall = new MavenPluginAdapter(mergeWithExisting ? mergedPlugin : filteredPlugin);
      pluginToInstall.setVersion(versionToInstall);
      if (!managed)
      {
         // In case of a direct plugin install
         // We want it's version being managed in the plugin management section
         MavenPluginAdapter mavenManagedPlugin = null;
         if (managedPlugin != null)
         {
            // A plugin managemement section already exists, update version
            mavenManagedPlugin = new MavenPluginAdapter(managedPlugin);
            mavenManagedPlugin.setVersion(pluginToInstall.getVersion());
         }
         else
         {
            // Create a new plugin management, that will handle the minimal configuration: groupId, artifactId and
            // version
            Plugin newManagedPlugin = new Plugin();
            newManagedPlugin.setGroupId(pluginToInstall.getGroupId());
            newManagedPlugin.setArtifactId(pluginToInstall.getArtifactId());
            newManagedPlugin.setVersion(pluginToInstall.getVersion());
            mavenManagedPlugin = new MavenPluginAdapter(newManagedPlugin);
         }

         // Install the plugin management, if needed
         if (mavenManagedPlugin != null)
         {
            addOrUpdatePlugin(deps, plugins, mavenManagedPlugin, true);
            pluginToInstall.setVersion(null); // handled by the plugin management section
         }
      }
      else
      {
         if (existing != null
                  && Strings.areEqual(versionToInstall, existing.getDependency().getVersion()))
         {
            // Same version in the hierarchy, no need to specify the version
            pluginToInstall.setVersion(null);
         }
      }

      return addOrUpdatePlugin(deps, plugins, pluginToInstall, managed);
   }

   @Override
   public MavenPlugin install(Project project, MavenPlugin plugin)
   {
      return install(project, plugin, false);
   }

   @Override
   public MavenPlugin installManaged(Project project, MavenPlugin plugin)
   {
      return install(project, plugin, true);
   }

   @Override
   public boolean isInstalled(Project project, MavenPlugin plugin)
   {
      MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
      return plugins.hasEffectivePlugin(plugin.getDependency());
   }

   private MavenPlugin addOrUpdatePlugin(DependencyFacet deps, MavenPluginFacet plugins,
            MavenPluginAdapter pluginToInstall, boolean managed)
   {

      Dependency pluginCoordinates = DependencyBuilder.create().setGroupId(pluginToInstall.getGroupId())
               .setArtifactId(pluginToInstall.getArtifactId());
      if (managed)
      {
         if (plugins.hasManagedPlugin(pluginCoordinates))
         {
            plugins.updateManagedPlugin(pluginToInstall);
         }
         else
         {
            plugins.addManagedPlugin(pluginToInstall);
         }
      }
      else
      {
         if (plugins.hasPlugin(pluginCoordinates))
         {
            plugins.updatePlugin(pluginToInstall);
         }
         else
         {
            plugins.addPlugin(pluginToInstall);
         }
      }

      return pluginToInstall;
   }

   private Dependency promptVersion(final DependencyFacet deps, final Dependency dependency, DependencyFilter filter)
   {
      Dependency result = dependency;
      final List<Dependency> versions = deps.resolveAvailableVersions(DependencyQueryBuilder.create(dependency)
               .setFilter(filter == null ? new NonSnapshotDependencyFilter() : filter));
      if (versions.size() > 0)
      {
         Dependency deflt = versions.get(versions.size() - 1);
         result = prompt.promptChoiceTyped("Use which version of '" + dependency.getArtifactId()
                  + "' ?", versions, deflt);
      }
      return result;
   }

   /**
    * Remove from the dominant plugin all the properties that are identical in the recessive plugin If a property exist
    * in the recessive plugin but not in dominant, it will be ignored <b>Important: the artifactId and groupId
    * properties are not affected</b>
    */
   private MavenPluginAdapter diff(final MavenPlugin dominant, final MavenPlugin recessive)
   {
      MavenPluginAdapter merged = new MavenPluginAdapter(dominant);
      if (DependencyBuilder.areEquivalent(dominant.getDependency(), recessive.getDependency()))
      {
         // Version
         if (Strings.areEqual(dominant.getDependency().getVersion(), recessive.getDependency().getVersion()))
         {
            // Remove version as dominant and recessive have the same one
            merged.setVersion(null);
         }

         // Extension
         if (dominant.isExtensionsEnabled() == recessive.isExtensionsEnabled())
         {
            // Remove extension as dominant and recessive have the same one
            merged.setExtensions(null);
         }
         // Config
         Map<String, String> cfgElmtsRefMap = new HashMap<String, String>();
         Configuration mergedConfiguration = merged.getConfig();
         if (dominant.getConfig() != null && recessive.getConfig() != null)
         {
            for (ConfigurationElement e : dominant.getConfig().listConfigurationElements())
            {
               // FIXME: recursively do a diff of childrens, if any
               cfgElmtsRefMap.put(e.getName(), e.toString());
            }
            for (ConfigurationElement e : recessive.getConfig().listConfigurationElements())
            {
               if (cfgElmtsRefMap.containsKey(e.getName()))
               {
                  if (Strings.areEqual(cfgElmtsRefMap.get(e.getName()), e.toString()))
                  {
                     // Remove the configuration element as dominant and recessive have the same element
                     mergedConfiguration.removeConfigurationElement(e.getName());
                  }
               }
            }
         }
         merged.setConfig(mergedConfiguration);
         // Executions
         Map<String, PluginExecution> dominantExec = new MavenPluginAdapter(dominant).getExecutionsAsMap();
         Map<String, PluginExecution> recessiveExec = new MavenPluginAdapter(recessive).getExecutionsAsMap();
         Map<String, PluginExecution> mergedExec = merged.getExecutionsAsMap();
         if (dominantExec != null && recessiveExec != null)
         {
            for (Map.Entry<String, PluginExecution> entry : recessiveExec.entrySet())
            {
               PluginExecution pluginExecutionRecessive = entry.getValue();
               PluginExecution pluginExecutionDominant = dominantExec.get(entry.getKey());
               if (pluginExecutionRecessive != null && pluginExecutionDominant != null)
               {
                  PluginExecution pluginExecutionMerged = mergedExec.get(entry.getKey());
                  // Phase
                  if (Strings.areEqual(pluginExecutionRecessive.getPhase(), pluginExecutionDominant.getPhase()))
                  {
                     // Remove the phase as dominant and recessive are identical
                     pluginExecutionMerged.setPhase(null);
                  }
                  // Goals
                  Map<String, Boolean> hasGoals = new HashMap<String, Boolean>();
                  for (String goal : pluginExecutionRecessive.getGoals())
                  {
                     hasGoals.put(goal, new Boolean(true));
                  }
                  for (String goal : pluginExecutionDominant.getGoals())
                  {
                     if (hasGoals.get(goal))
                     {
                        // Remove the goal as dominant and recessive have the same goal
                        pluginExecutionMerged.removeGoal(goal);
                     }
                  }
                  // Configurations
                  Map<String, String> cfgExecElmtsRefMap = new HashMap<String, String>();
                  if (pluginExecutionRecessive.getConfiguration() != null
                           && pluginExecutionDominant.getConfiguration() != null)
                  {
                     Configuration pluginExecutionRecessiveCfg = new ConfigurationImpl(
                              (Xpp3Dom) pluginExecutionRecessive.getConfiguration());
                     Configuration pluginExecutionDominantCfg = new ConfigurationImpl(
                              (Xpp3Dom) pluginExecutionDominant.getConfiguration());
                     Configuration pluginExecutionMergedCfg = new ConfigurationImpl(
                              (Xpp3Dom) pluginExecutionMerged.getConfiguration());

                     for (ConfigurationElement e : pluginExecutionDominantCfg.listConfigurationElements())
                     {
                        // FIXME: recursively do a diff of childrens, if any
                        cfgExecElmtsRefMap.put(e.getName(), e.toString());
                     }
                     for (ConfigurationElement e : pluginExecutionRecessiveCfg.listConfigurationElements())
                     {
                        if (cfgExecElmtsRefMap.containsKey(e.getName()))
                        {
                           if (Strings.areEqual(cfgExecElmtsRefMap.get(e.getName()), e.toString()))
                           {
                              // Remove the execution configuration element as dominant and recessive have the same
                              // element
                              pluginExecutionMergedCfg.removeConfigurationElement(e.getName());
                           }
                        }
                     }
                     if ((pluginExecutionMergedCfg == null) || (!pluginExecutionMergedCfg.hasConfigurationElements()))
                     {
                        pluginExecutionMerged.setConfiguration(null);
                     }
                     try
                     {
                        pluginExecutionMerged.setConfiguration(Xpp3DomBuilder.build(
                                 new ByteArrayInputStream(pluginExecutionMergedCfg.toString().getBytes()), "UTF-8"));
                     }
                     catch (Exception ex)
                     {
                        throw new RuntimeException("Exception while parsing configuration", ex);
                     }

                  }
               }
            }
         }
      }
      return merged;
   }

}
