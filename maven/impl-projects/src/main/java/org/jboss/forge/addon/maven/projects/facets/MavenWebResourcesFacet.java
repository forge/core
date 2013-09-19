/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.plugins.Configuration;
import org.jboss.forge.addon.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.addon.maven.plugins.MavenPlugin;
import org.jboss.forge.addon.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;

@Dependent
@FacetConstraint({ MavenFacet.class, PackagingFacet.class })
public class MavenWebResourcesFacet extends AbstractFacet<Project> implements WebResourcesFacet
{

   @Override
   public DirectoryResource getWebRootDirectory()
   {
      Project project = getFaceted();
      MavenPluginFacet mavenPluginFacet = project.getFacet(MavenPluginFacet.class);
      final String webappFolderName;
      Coordinate mvnWarPluginDep = CoordinateBuilder.create("org.apache.maven.plugins:maven-war-plugin");
      if (mavenPluginFacet.hasPlugin(mvnWarPluginDep))
      {
         MavenPlugin warPlugin = mavenPluginFacet.getPlugin(mvnWarPluginDep);
         Configuration config = warPlugin.getConfig();
         if (config.hasConfigurationElement("warSourceDirectory"))
         {
            webappFolderName = config.getConfigurationElement("warSourceDirectory").getText();
         }
         else
         {
            webappFolderName = "src" + File.separator + "main" + File.separator + "webapp";
         }
      }
      else
      {
         webappFolderName = "src" + File.separator + "main" + File.separator + "webapp";
      }
      DirectoryResource projectRoot = project.getProjectRoot();
      return projectRoot.getChildDirectory(webappFolderName);
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
      Project project = getFaceted();
      String packagingType = project.getFacet(PackagingFacet.class).getPackagingType();

      return packagingType.equals("war");
   }

   @Override
   public boolean install()
   {
      if (!this.isInstalled())
      {
         getFaceted().getFacet(PackagingFacet.class).setPackagingType("war");
         for (DirectoryResource folder : this.getWebRootDirectories())
         {
            folder.mkdirs();
         }

         MavenPluginFacet plugins = getFaceted().getFacet(MavenPluginFacet.class);
         Coordinate mvnWarPluginDep = CoordinateBuilder.create().setGroupId("org.apache.maven.plugins")
                  .setArtifactId("maven-war-plugin")
                  .setVersion("2.4");

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
