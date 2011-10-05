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

import java.util.List;

import org.jboss.forge.project.dependencies.Dependency;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class MavenPluginBuilder implements MavenPlugin, PluginElement
{
   private MavenPluginImpl plugin = new MavenPluginImpl();

   private MavenPluginBuilder()
   {}

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
   public Dependency getDependency()
   {
      return plugin.getDependency();
   }

   public MavenPluginBuilder setDependency(final Dependency dependency)
   {
      plugin.setDependency(dependency);
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

   public MavenPluginBuilder setExtensions(boolean extensions) {
       plugin.setExtenstions(extensions);
       return this;
   }

    @Override
    public boolean isExtensionsEnabled() {
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
      if (plugin.getConfig() != null) {
         builder = ConfigurationBuilder.create(plugin.getConfig(), this);
      }
      else {
         builder = ConfigurationBuilder.create(this);
      }

      plugin.setConfiguration(builder);

      return builder;
   }
}
