/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.plugins.Configuration;
import org.jboss.forge.addon.maven.plugins.MavenPlugin;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.addon.resource.visit.ResourceVisit;
import org.jboss.forge.addon.resource.visit.ResourceVisitor;

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
      DirectoryResource projectRoot = project.getRoot().reify(DirectoryResource.class);
      return projectRoot.getChildDirectory(webappFolderName);
   }

   @Override
   public List<DirectoryResource> getWebRootDirectories()
   {
      List<DirectoryResource> result = new ArrayList<>();
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
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
         Model pom = maven.getModel();
         Properties properties = pom.getProperties();
         properties.setProperty("failOnMissingWebXml", "false");
         maven.setModel(pom);
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

   @Override
   public void visitWebResources(ResourceVisitor visitor)
   {
      for (DirectoryResource root : getWebRootDirectories())
      {
         ResourceVisit visit = new ResourceVisit(root);
         visit.perform(visitor, new ResourceFilter()
         {
            @Override
            public boolean accept(Resource<?> resource)
            {
               return resource instanceof DirectoryResource;
            }
         }, new ResourceFilter()
         {
            @Override
            public boolean accept(Resource<?> type)
            {
               return true;
            }
         });

         if (visit.isTerminated())
            break;
      }
   }

}
