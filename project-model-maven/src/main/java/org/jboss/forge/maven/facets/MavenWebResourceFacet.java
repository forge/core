/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
@Alias("forge.maven.WebResourceFacet")
@RequiresPackagingType(PackagingType.WAR)
@RequiresFacet({ JavaSourceFacet.class, PackagingFacet.class, MavenPluginFacet.class })
public class MavenWebResourceFacet extends BaseFacet implements WebResourceFacet, Facet
{
   private Project project;

   @Override
   public DirectoryResource getWebRootDirectory()
   {
      MavenPluginFacet mavenPluginFacet = project.getFacet(MavenPluginFacet.class);
      final String webappFolderName;
      Dependency mvnWarPluginDep = DependencyBuilder.create("org.apache.maven.plugins:maven-war-plugin");
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
   public void setProject(final Project project)
   {
      this.project = project;
   }

   @Override
   public Project getProject()
   {
      return project;
   }

   @Override
   public boolean isInstalled()
   {
      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      PackagingType packagingType = project.getFacet(PackagingFacet.class).getPackagingType();

      return mavenFacet.isInstalled()
               && packagingType.equals(PackagingType.WAR);
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

         MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
         DependencyBuilder mvnWarPluginDep = DependencyBuilder.create("org.apache.maven.plugins:maven-war-plugin")
                  .setVersion("2.4");

         MavenPlugin plugin;
         if (!plugins.hasPlugin(mvnWarPluginDep))
         {
            plugin = MavenPluginBuilder.create().setDependency(mvnWarPluginDep);
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
