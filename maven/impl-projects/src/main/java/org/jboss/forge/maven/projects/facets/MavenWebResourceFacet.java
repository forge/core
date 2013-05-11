/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.projects.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.jboss.forge.dependencies.Coordinate;
import org.jboss.forge.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.facets.AbstractFacet;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.maven.projects.MavenFacet;
import org.jboss.forge.maven.projects.MavenPluginFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.facets.PackagingFacet;
import org.jboss.forge.projects.facets.WebResourceFacet;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.FileResource;

@Dependent
public class MavenWebResourceFacet extends AbstractFacet<Project> implements WebResourceFacet
{

   @Override
   public DirectoryResource getWebRootDirectory()
   {
      return getOrigin().getProjectRoot()
               .getChildDirectory("src" + File.separator + "main" + File.separator + "webapp");
   }

   @Override
   public List<DirectoryResource> getWebRootDirectories()
   {
      List<DirectoryResource> result = new ArrayList<DirectoryResource>();
      result.add(getWebRootDirectory());
      return result;
   }

   @Override
   public boolean isInstalled()
   {
      Project project = getOrigin();
      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      String packagingType = project.getFacet(PackagingFacet.class).getPackagingType();

      return mavenFacet.isInstalled()
               && packagingType.equals("war");
   }

   @Override
   public boolean install()
   {
      if (!this.isInstalled())
      {
         for (DirectoryResource folder : this.getWebRootDirectories())
         {
            folder.mkdirs();
         }

         MavenPluginFacet plugins = getOrigin().getFacet(MavenPluginFacet.class);
         Coordinate mvnWarPluginDep = CoordinateBuilder.create().setGroupId("org.apache.maven.plugins")
                  .setArtifactId("maven-war-plugin")
                  .setVersion("2.3");

         MavenPlugin plugin;
         if (!plugins.hasPlugin(mvnWarPluginDep))
         {
            plugin = MavenPluginBuilder.create().setCoordinate(mvnWarPluginDep);
            plugins.addPlugin(plugin);
         }
         else
         {
            plugin = plugins.getPlugin(mvnWarPluginDep);
         }

         if (plugin.getConfig() == null)
         {

         }

         if (!plugin.getConfig().hasConfigurationElement("failOnMissingWebXml"))
         {
            plugin.getConfig().addConfigurationElement(
                     ConfigurationElementBuilder.create().setName("failOnMissingWebXml").setText("false"));
         }
         else
         {
            ConfigurationElementBuilder configElement = ConfigurationElementBuilder.createFromExisting(plugin
                     .getConfig().getConfigurationElement("failOnMissingWebXml"));
            plugin.getConfig().removeConfigurationElement("failOnMissingWebXml");
            plugin.getConfig().addConfigurationElement(configElement);
         }

         plugins.removePlugin(mvnWarPluginDep);
         plugins.addPlugin(plugin);
      }
      return true;
   }

   @Override
   public FileResource<?> getWebResource(final String relativePath)
   {
      return (FileResource<?>) getWebRootDirectory().getChild(relativePath);
   }

   @Override
   public FileResource<?> createWebResource(final char[] data, final String relativePath)
   {
      FileResource<?> file = (FileResource<?>) getWebRootDirectory().getChild(relativePath);
      file.setContents(data);
      return file;
   }

   @Override
   public FileResource<?> createWebResource(final String data, final String relativePath)
   {
      return createWebResource(data.toCharArray(), relativePath);
   }

}
