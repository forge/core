/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.plugins;

import java.util.List;

/**
 * Builds a {@link Configuration} object
 * 
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
