/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.plugins;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Exclusion;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */

public class MavenPluginAdapter extends org.apache.maven.model.Plugin implements MavenPlugin
{
   private static final long serialVersionUID = 2502801162956631981L;

   public MavenPluginAdapter(final MavenPlugin mavenPlugin)
   {
      Dependency dependency = mavenPlugin.getDependency();

      setGroupId(dependency.getGroupId());
      setArtifactId(dependency.getArtifactId());
      setVersion(dependency.getVersion());
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
         pluginDependency.setArtifactId(dependency.getArtifactId());
         pluginDependency.setGroupId(dependency.getGroupId());
         pluginDependency.setVersion(dependency.getVersion());
         pluginDependency.setScope(dependency.getScopeType());
         pluginDependency.setExclusions(transformExclusions(dependency.getExcludedDependencies()));
         dependencies.add(pluginDependency);
      }
      return dependencies;
   }

   private List<Exclusion> transformExclusions(List<Dependency> excludedDependencies)
   {
      List<Exclusion> result = new ArrayList<Exclusion>(excludedDependencies.size());
      for (Dependency dependency : excludedDependencies)
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

      for (Execution execution : mavenPlugin.listExecutions()) {
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
      if ((configuration == null) || (!configuration.hasConfigurationElements())) {
         return null;
      }

      try {
         return Xpp3DomBuilder.build(
                  new ByteArrayInputStream(configuration.toString().getBytes()), "UTF-8");
      }
      catch (Exception ex) {
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
   }

   @Override
   public List<Execution> listExecutions()
   {
      List<Execution> executions = new ArrayList<Execution>();

      for (PluginExecution pluginExecution : getExecutions()) {
         ExecutionBuilder executionBuilder = ExecutionBuilder.create()
                  .setId(pluginExecution.getId()).setPhase(pluginExecution.getPhase());
         for (String goal : pluginExecution.getGoals()) {
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
   
   
   public void setConfig(Configuration configuration) {
      setConfiguration(parseConfig(configuration));
   }

   @Override
   public Dependency getDependency()
   {
      return DependencyBuilder.create()
               .setGroupId(getGroupId())
               .setArtifactId(getArtifactId())
               .setVersion(getVersion());
   }

    @Override
    public boolean isExtensionsEnabled() {
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
                  .setPackagingType(pluginDependency.getType())
                  .setScopeType(pluginDependency.getScope());
         dependencies.add(builder);
      }
      return dependencies;
   }

}
