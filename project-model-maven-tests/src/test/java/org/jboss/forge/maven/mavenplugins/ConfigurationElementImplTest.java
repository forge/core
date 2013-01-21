/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.mavenplugins;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationElementNotFoundException;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.junit.Test;

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
