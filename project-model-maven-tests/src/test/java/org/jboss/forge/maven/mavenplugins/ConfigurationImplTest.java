/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.mavenplugins;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class ConfigurationImplTest
{

   private Configuration pluginConfiguration;

   @Before
   public void setup() throws Exception
   {
      MavenPluginAdapter adapter = new MavenPluginAdapter(createMavenPlugin());
      pluginConfiguration = adapter.getConfig();
   }

   @Test
   public void testHasConfigurationElement() throws Exception
   {
      boolean hasElement = pluginConfiguration.hasConfigurationElement("reportPlugins");
      assertTrue(hasElement);
   }

   @Test
   public void testHasNotConfigurationElement() throws Exception
   {
      boolean hasElement = pluginConfiguration.hasConfigurationElement("unknown");
      assertFalse(hasElement);
   }

   @Test
   public void testListConfigurationElements() throws Exception
   {
      List<ConfigurationElement> plugins = pluginConfiguration.listConfigurationElements();
      assertThat(plugins.size(), is(1));
      ConfigurationElement configurationElement = plugins.get(0);
      assertThat(configurationElement.getName(), is("reportPlugins"));
      assertFalse(configurationElement.isPlugin());
   }

   @Test
   public void testAddConfigurationElement() throws Exception
   {
      List<ConfigurationElement> plugins = pluginConfiguration.listConfigurationElements();
      int pluginSize = plugins.size();
      assertThat(pluginSize, is(1));

      ConfigurationElementBuilder element =
              ConfigurationElementBuilder.create()
                      .setName("testElement");

      pluginConfiguration.addConfigurationElement(element);

      assertThat(pluginConfiguration.listConfigurationElements().size(), is(pluginSize + 1));
   }

   private Plugin createMavenPlugin() throws Exception
   {
      Plugin plugin = new Plugin();
      plugin.setGroupId("org.apache.maven.plugins");
      plugin.setGroupId("maven-site-plugin");
      plugin.setVersion("3.0");
      Xpp3Dom dom;
      dom = Xpp3DomBuilder.build(
              new ByteArrayInputStream(
                      ("<configuration>" +
                              "   <reportPlugins>" +
                              "       <plugin>" +
                              "           <groupId>" + plugin.getGroupId() + "</groupId>" +
                              "           <artifactId>" + plugin.getArtifactId() + "</artifactId>" +
                              "           <version>" + plugin.getVersion() + "</version>" +
                              "       </plugin>" +
                              "   </reportPlugins>" +
                              "</configuration>").getBytes()),
              "UTF-8");

      plugin.setConfiguration(dom);
      return plugin;
   }
}
