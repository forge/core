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

package org.jboss.forge.maven.plugins;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

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
      setExtensions(mavenPlugin.isExtensionsEnabled());
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
                  new ByteArrayInputStream(
                           configuration.toString().getBytes()), "UTF-8");
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
}
