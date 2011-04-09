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

package org.jboss.seam.forge.maven.mavenplugins;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.seam.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.seam.forge.maven.plugins.MavenPluginConfiguration;
import org.jboss.seam.forge.maven.plugins.MavenPluginConfigurationElement;
import org.jboss.seam.forge.maven.plugins.MavenPluginConfigurationElementBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class MavenPluginConfigurationImplTest
{

   private MavenPluginConfiguration pluginConfiguration;

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
      List<MavenPluginConfigurationElement> plugins = pluginConfiguration.listConfigurationElements();
      assertThat(plugins.size(), is(1));
      MavenPluginConfigurationElement configurationElement = plugins.get(0);
      assertThat(configurationElement.getName(), is("reportPlugins"));
      assertFalse(configurationElement.isPlugin());
   }

   @Test
   public void testAddConfigurationElement() throws Exception
   {
      List<MavenPluginConfigurationElement> plugins = pluginConfiguration.listConfigurationElements();
      int pluginSize = plugins.size();
      assertThat(pluginSize, is(1));

      MavenPluginConfigurationElementBuilder element =
              MavenPluginConfigurationElementBuilder.create()
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
