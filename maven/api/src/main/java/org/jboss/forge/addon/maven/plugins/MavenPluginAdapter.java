/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;

/**
 * A plugin adapter for {@link Plugin} and {@link MavenPlugin}
 * 
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */

public class MavenPluginAdapter extends org.apache.maven.model.Plugin implements MavenPlugin
{
   private static final long serialVersionUID = 2502801162956631981L;

   public MavenPluginAdapter(final MavenPlugin mavenPlugin)
   {
      Coordinate coordinate = mavenPlugin.getCoordinate();

      setGroupId(coordinate.getGroupId());
      setArtifactId(coordinate.getArtifactId());
      setVersion(coordinate.getVersion());
      setConfiguration(parseConfig(mavenPlugin.getConfig()));
      setExecutions(transformExecutions(mavenPlugin));
      if (mavenPlugin.isExtensionsEnabled())
      {
         setExtensions(true);
      }
      setDependencies(transformDependencies(mavenPlugin));
   }

   private List<org.apache.maven.model.Dependency> transformDependencies(MavenPlugin mavenPlugin)
   {
      List<org.apache.maven.model.Dependency> dependencies = new ArrayList<org.apache.maven.model.Dependency>();
      for (Dependency dependency : mavenPlugin.getDirectDependencies())
      {
         org.apache.maven.model.Dependency pluginDependency = new org.apache.maven.model.Dependency();
         pluginDependency.setArtifactId(dependency.getCoordinate().getArtifactId());
         pluginDependency.setGroupId(dependency.getCoordinate().getGroupId());
         pluginDependency.setVersion(dependency.getCoordinate().getVersion());
         pluginDependency.setScope(dependency.getScopeType());
         if (dependency.getExcludedCoordinates() != null)
         {
            pluginDependency.setExclusions(transformExclusions(dependency.getExcludedCoordinates()));
         }
         dependencies.add(pluginDependency);
      }
      return dependencies;
   }

   private List<Exclusion> transformExclusions(List<Coordinate> excludedDependencies)
   {
      List<Exclusion> result = new ArrayList<Exclusion>(excludedDependencies.size());
      for (Coordinate dependency : excludedDependencies)
      {
         Exclusion exclusion = new Exclusion();
         exclusion.setArtifactId(dependency.getArtifactId());
         exclusion.setGroupId(dependency.getGroupId());
         result.add(exclusion);
      }
      return result;
   }

   private List<PluginExecution> transformExecutions(final MavenPlugin mavenPlugin)
   {
      List<PluginExecution> executions = new ArrayList<PluginExecution>();

      for (Execution execution : mavenPlugin.listExecutions())
      {
         PluginExecution pluginExecution = new PluginExecution();
         pluginExecution.setId(execution.getId());
         pluginExecution.setPhase(execution.getPhase());
         pluginExecution.setGoals(execution.getGoals());
         pluginExecution.setConfiguration(parseConfig(execution.getConfig()));
         executions.add(pluginExecution);
      }

      return executions;

   }

   private Xpp3Dom parseConfig(final Configuration configuration)
   {
      if ((configuration == null) || (!configuration.hasConfigurationElements()))
      {
         return null;
      }

      try
      {
         return Xpp3DomBuilder.build(
                  new ByteArrayInputStream(configuration.toString().getBytes()), "UTF-8");
      }
      catch (Exception ex)
      {
         throw new RuntimeException("Exception while parsing configuration", ex);
      }
   }

   public MavenPluginAdapter(final org.apache.maven.model.Plugin plugin)
   {
      org.apache.maven.model.Plugin clone = plugin.clone();

      setGroupId(clone.getGroupId());
      setArtifactId(clone.getArtifactId());
      setVersion(clone.getVersion());
      setConfiguration(plugin.getConfiguration());
      setExecutions(clone.getExecutions());
      setExtensions(clone.getExtensions());
      setDependencies(clone.getDependencies());
   }

   @Override
   public List<Execution> listExecutions()
   {
      List<Execution> executions = new ArrayList<Execution>();

      for (PluginExecution pluginExecution : getExecutions())
      {
         ExecutionBuilder executionBuilder = ExecutionBuilder.create()
                  .setId(pluginExecution.getId()).setPhase(pluginExecution.getPhase());
         for (String goal : pluginExecution.getGoals())
         {
            executionBuilder.addGoal(goal);
         }
         if (pluginExecution.getConfiguration() != null)
         {
            executionBuilder.setConfig(new ConfigurationImpl((Xpp3Dom) pluginExecution.getConfiguration()));
         }
         executions.add(executionBuilder);
      }

      return executions;
   }

   @Override
   public Configuration getConfig()
   {
      Xpp3Dom dom = (Xpp3Dom) super.getConfiguration();

      return new ConfigurationImpl(dom);
   }

   @Override
   public Coordinate getCoordinate()
   {
      return CoordinateBuilder.create()
               .setGroupId(getGroupId())
               .setArtifactId(getArtifactId())
               .setVersion(getVersion());
   }

   @Override
   public boolean isExtensionsEnabled()
   {
      return isExtensions();
   }

   @Override
   public List<Dependency> getDirectDependencies()
   {
      List<Dependency> dependencies = new ArrayList<Dependency>();
      for (org.apache.maven.model.Dependency pluginDependency : getDependencies())
      {
         DependencyBuilder builder = DependencyBuilder.create()
                  .setArtifactId(pluginDependency.getArtifactId())
                  .setGroupId(pluginDependency.getGroupId())
                  .setVersion(pluginDependency.getVersion())
                  .setPackaging(pluginDependency.getType())
                  .setScopeType(pluginDependency.getScope());
         dependencies.add(builder);
      }
      return dependencies;
   }

   public void setConfig(Configuration configuration)
   {
      setConfiguration(parseConfig(configuration));
   }
}
