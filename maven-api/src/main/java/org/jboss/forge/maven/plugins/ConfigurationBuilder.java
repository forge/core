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

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class ConfigurationBuilder implements Configuration
{
   private final ConfigurationImpl mavenPluginConfiguration = new ConfigurationImpl();
   private MavenPluginBuilder origin;

   @Override
   public ConfigurationElement getConfigurationElement(final String element)
   {
      return mavenPluginConfiguration.getConfigurationElement(element);
   }

   @Override
   public boolean hasConfigurationElement(final String configElement)
   {
      return mavenPluginConfiguration.hasConfigurationElement(configElement);
   }

   @Override
   public boolean hasConfigurationElements()
   {
      return mavenPluginConfiguration.hasConfigurationElements();
   }

   @Override
   public List<ConfigurationElement> listConfigurationElements()
   {
      return mavenPluginConfiguration.listConfigurationElements();
   }

   @Override
   public Configuration addConfigurationElement(final ConfigurationElement element)
   {
      return mavenPluginConfiguration.addConfigurationElement(element);
   }

   @Override
   public void removeConfigurationElement(final String elementName)
   {
      mavenPluginConfiguration.removeConfigurationElement(elementName);
   }

   @Override
   public String toString()
   {
      return mavenPluginConfiguration.toString();
   }

   private ConfigurationBuilder()
   {

   }

   public ConfigurationElementBuilder createConfigurationElement(final String name)
   {
      ConfigurationElementBuilder builder = ConfigurationElementBuilder.create(this);
      builder.setName(name);
      mavenPluginConfiguration.addConfigurationElement(builder);
      return builder;
   }

   private ConfigurationBuilder(final MavenPluginBuilder pluginBuilder)
   {
      origin = pluginBuilder;
   }

   private ConfigurationBuilder(final Configuration existingConfig, final MavenPluginBuilder pluginBuilder)
   {
      origin = pluginBuilder;
      for (ConfigurationElement element : existingConfig.listConfigurationElements())
      {
         mavenPluginConfiguration.addConfigurationElement(element);
      }
   }

   public static ConfigurationBuilder create()
   {
      return new ConfigurationBuilder();
   }

   public static ConfigurationBuilder create(final MavenPluginBuilder pluginBuilder)
   {
      return new ConfigurationBuilder(pluginBuilder);
   }

   public static ConfigurationBuilder create(final Configuration existingConfig, final MavenPluginBuilder pluginBuilder)
   {
      return new ConfigurationBuilder(existingConfig, pluginBuilder);
   }

   public MavenPluginBuilder getOrigin()
   {
      return origin;
   }
}
