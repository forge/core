/*
 *
 *  * JBoss, Home of Professional Open Source
 *  * Copyright 2011, Red Hat, Inc., and individual contributors
 *  * by the @authors tag. See the copyright.txt in the distribution for a
 *  * full listing of individual contributors.
 *  *
 *  * This is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU Lesser General Public License as
 *  * published by the Free Software Foundation; either version 2.1 of
 *  * the License, or (at your option) any later version.
 *  *
 *  * This software is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this software; if not, write to the Free
 *  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.jboss.forge.maven.mavenplugins;

import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementNotFoundException;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ConfigurationElementImplTest
{
   private MavenPlugin sitePlugin;

   @Test
   public void testHasChildByContentWithConfigElements()
   {
      sitePlugin = createSitePluginWithConfigElements();
      ConfigurationElement reportPlugins = sitePlugin.getConfig().getConfigurationElement("reportPlugins");
      assertThat(reportPlugins.hasChildByContent("findbugs-maven-plugin"), is(true));
   }

   @Test(expected = RuntimeException.class)
   public void testHasChildByContentWithChildPlugin()
   {
      sitePlugin = createSitePluginWithChildPlugin();
      sitePlugin.getConfig().getConfigurationElement("reportPlugins").hasChildByContent("findbugs-maven-plugin");
   }

   @Test
   public void testHasChildByContentNotExisting()
   {
      sitePlugin = createSitePluginWithConfigElements();
      boolean found = sitePlugin.getConfig().getConfigurationElement("reportPlugins").hasChildByContent("abc");
      assertThat(found, is(false));
   }

   @Test
   public void testGetChildByContent() {
      sitePlugin = createSitePluginWithConfigElements();
      ConfigurationElement findbugsPlugin = sitePlugin.getConfig().getConfigurationElement("reportPlugins").getChildByContent("findbugs-maven-plugin");
      assertNotNull(findbugsPlugin);
      assertThat(findbugsPlugin.getName(), is("plugin"));
      assertThat(findbugsPlugin.getChildren().size(), is(3));
   }

   @Test(expected = ConfigurationElementNotFoundException.class)
   public void testGetChildByContentNotExisting() {
      sitePlugin = createSitePluginWithConfigElements();
      sitePlugin.getConfig().getConfigurationElement("reportPlugins").getChildByContent("abc");
   }

   @Test
   public void testHasChildByName()
   {
      sitePlugin = createSitePluginWithConfigElements();
      ConfigurationElement reportPlugins = sitePlugin.getConfig().getConfigurationElement("reportPlugins");
      assertThat(reportPlugins.hasChildByName("artifactId"), is(true));
   }

   @Test
   public void testHasChildByNameNotExisting()
   {
      sitePlugin = createSitePluginWithConfigElements();
      boolean found = sitePlugin.getConfig().getConfigurationElement("reportPlugins").hasChildByName("abc");
      assertThat(found, is(false));
   }

   @Test
   public void testGetChildByName() {
      sitePlugin = createSitePluginWithConfigElements();
      ConfigurationElement findbugsPlugin = sitePlugin.getConfig().getConfigurationElement("reportPlugins").getChildByName("artifactId");
      assertNotNull(findbugsPlugin);
      assertThat(findbugsPlugin.getName(), is("artifactId"));
      assertThat(findbugsPlugin.getText(), is("findbugs-maven-plugin"));
   }

   @Test
   public void testGetChildByNameWithChilds() {
      sitePlugin = createSitePluginWithConfigElements();
      ConfigurationElement findbugsPlugin = sitePlugin.getConfig().getConfigurationElement("reportPlugins").getChildByName("plugin");
      assertNotNull(findbugsPlugin);
      assertThat(findbugsPlugin.getName(), is("plugin"));
      assertThat(findbugsPlugin.hasChilderen(), is(true));
   }

   @Test(expected = ConfigurationElementNotFoundException.class)
   public void testGetChildByNameNotExisting() {
      sitePlugin = createSitePluginWithConfigElements();
      sitePlugin.getConfig().getConfigurationElement("reportPlugins").getChildByName("abc");
   }

   private MavenPlugin createSitePluginWithConfigElements()
   {


      return MavenPluginBuilder.create()
              .setDependency(
                      DependencyBuilder.create()
                              .setGroupId("org.apache.maven.plugins")
                              .setArtifactId("maven-site-plugin")
                              .setVersion("3.0")
              )
              .createConfiguration().createConfigurationElement("reportPlugins")
              .addChild("plugin")
              .addChild("groupId").setText("org.codehaus.mojo").getParentElement()
              .addChild("artifactId").setText("findbugs-maven-plugin").getParentElement()
              .addChild("version").setText("2.3.2").getParentElement()
              .getParentElement().getParentPluginConfig().getOrigin();

   }

   private MavenPlugin createSitePluginWithChildPlugin()
   {
      MavenPluginBuilder findbugsPlugin = MavenPluginBuilder.create()
              .setDependency(
                      DependencyBuilder.create()
                              .setGroupId("org.codehaus.mojo")
                              .setArtifactId("findbugs-maven-plugin")
                              .setVersion("2.3.2")
              )
              .createConfiguration()
              .createConfigurationElement("xmlOutput")
              .setText("true").getParentPluginConfig().getOrigin();

      return MavenPluginBuilder.create()
              .setDependency(
                      DependencyBuilder.create()
                              .setGroupId("org.apache.maven.plugins")
                              .setArtifactId("maven-site-plugin")
                              .setVersion("3.0")
              )
              .createConfiguration().createConfigurationElement("reportPlugins").addChild(findbugsPlugin).getParentPluginConfig().getOrigin();

   }
}
