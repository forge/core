/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class MavenPluginImpl implements MavenPlugin
{

   private Coordinate coordinate;
   private Configuration configuration;
   private final List<Execution> executions = new ArrayList<Execution>();
   private boolean extensions;
   private List<Dependency> pluginDependencies = new ArrayList<Dependency>();

   public MavenPluginImpl()
   {
   }

   public MavenPluginImpl(final MavenPlugin plugin)
   {
      setCoordinate(plugin.getCoordinate());
      setConfiguration(plugin.getConfig());
   }

   @Override
   public Coordinate getCoordinate()
   {
      return coordinate;
   }

   public void setCoordinate(Coordinate coordinate)
   {
      this.coordinate = coordinate;
   }

   @Override
   public Configuration getConfig()
   {
      if (configuration == null)
      {
         configuration = ConfigurationBuilder.create();
      }
      return configuration;
   }

   @Override
   public List<Execution> listExecutions()
   {
      return executions;
   }

   @Override
   public boolean isExtensionsEnabled()
   {
      return extensions;
   }

   @Override
   public List<Dependency> getDirectDependencies()
   {
      return pluginDependencies;
   }

   @Override
   public String toString()
   {
      StringBuilder b = new StringBuilder("<plugin>");
      appendCoordinates(b, coordinate, true);

      if (extensions)
      {
         b.append("<extensions>true</extensions>");
      }

      if (configuration != null)
      {
         b.append(configuration.toString());
      }

      if (executions.size() > 0)
      {
         b.append("<executions>");
         for (Execution execution : executions)
         {
            b.append(execution.toString());
         }
         b.append("</executions>");
      }

      if (pluginDependencies.size() > 0)
      {
         b.append("<dependencies>");
         for (Dependency pluginDependency : pluginDependencies)
         {
            b.append("<dependency>");
            appendDependency(b, pluginDependency);
            b.append("</dependency>");
         }
         b.append("</dependencies>");
      }

      b.append("</plugin>");
      return b.toString();
   }

   public void setConfiguration(final Configuration configuration)
   {
      this.configuration = configuration;
   }

   public void addExecution(final Execution execution)
   {
      executions.add(execution);
   }

   public void setExtenstions(boolean extenstions)
   {
      this.extensions = extenstions;
   }

   public void addPluginDependency(final Dependency dependency)
   {
      pluginDependencies.add(dependency);
   }

   private void appendDependency(StringBuilder builder, Dependency appendDependency)
   {
      appendCoordinates(builder, appendDependency.getCoordinate(), true);
      if (!appendDependency.getExcludedCoordinates().isEmpty())
      {
         builder.append("<exclusions>");
         for (Coordinate exclusion : appendDependency.getExcludedCoordinates())
         {
            appendExclusion(builder, exclusion);
         }
         builder.append("</exclusions>");
      }
   }

   private void appendExclusion(StringBuilder builder, Coordinate exclusion)
   {
      builder.append("<exclusion>");
      appendCoordinates(builder, exclusion, false);
      builder.append("</exclusion>");
   }

   private void appendCoordinates(StringBuilder builder, Coordinate coordinate, boolean withVersion)
   {
      if (coordinate.getGroupId() != null)
      {
         builder.append("<groupId>").append(coordinate.getGroupId()).append("</groupId>");
      }

      if (coordinate.getArtifactId() != null)
      {
         builder.append("<artifactId>").append(coordinate.getArtifactId()).append("</artifactId>");
      }

      if (withVersion && coordinate.getVersion() != null)
      {
         builder.append("<version>").append(coordinate.getVersion()).append("</version>");
      }
   }

}
