/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

import java.util.List;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class MavenPluginBuilder implements MavenPlugin, PluginElement
{
   private MavenPluginImpl plugin = new MavenPluginImpl();

   private MavenPluginBuilder()
   {
   }

   private MavenPluginBuilder(final MavenPlugin plugin)
   {
      this.plugin = new MavenPluginImpl(plugin);
   }

   public static MavenPluginBuilder create()
   {
      return new MavenPluginBuilder();
   }

   public static MavenPluginBuilder create(final MavenPlugin plugin)
   {
      return new MavenPluginBuilder(plugin);
   }

   public MavenPluginBuilder setConfiguration(final Configuration configuration)
   {
      plugin.setConfiguration(configuration);
      return this;
   }

   @Override
   public Coordinate getCoordinate()
   {
      return plugin.getCoordinate();
   }

   public MavenPluginBuilder setCoordinate(final Coordinate coordinate)
   {
      plugin.setCoordinate(coordinate);
      return this;
   }

   @Override
   public Configuration getConfig()
   {
      if (plugin.getConfig() == null)
      {
         plugin.setConfiguration(ConfigurationBuilder.create());
      }
      return plugin.getConfig();
   }

   @Override
   public List<Execution> listExecutions()
   {
      return plugin.listExecutions();
   }

   public MavenPluginBuilder addExecution(final Execution execution)
   {
      plugin.addExecution(execution);
      return this;
   }

   public MavenPluginBuilder addPluginDependency(final Dependency pluginDependency)
   {
      plugin.addPluginDependency(pluginDependency);
      return this;
   }

   public MavenPluginBuilder setExtensions(boolean extensions)
   {
      plugin.setExtenstions(extensions);
      return this;
   }

   @Override
   public boolean isExtensionsEnabled()
   {
      return plugin.isExtensionsEnabled();
   }

   @Override
   public String toString()
   {
      return plugin.toString();
   }

   public ConfigurationBuilder createConfiguration()
   {
      ConfigurationBuilder builder;
      if (plugin.getConfig() != null)
      {
         builder = ConfigurationBuilder.create(plugin.getConfig(), this);
      }
      else
      {
         builder = ConfigurationBuilder.create(this);
      }

      plugin.setConfiguration(builder);

      return builder;
   }

   @Override
   public List<Dependency> getDirectDependencies()
   {
      return plugin.getDirectDependencies();
   }

}
