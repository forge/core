/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.spec.javaee;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.servlet")
@RequiresFacet({ MetadataFacet.class, WebResourceFacet.class, DependencyFacet.class })
@RequiresPackagingType({PackagingType.WAR, PackagingType.BUNDLE})
public class ServletFacetImpl extends BaseFacet implements ServletFacet
{

   private static final Dependency dep =
            DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0:1.0.0.Final:provided:basic");

   @Override
   public WebAppDescriptor getConfig()
   {
      DescriptorImporter<WebAppDescriptor> importer = Descriptors.importAs(WebAppDescriptor.class);
      WebAppDescriptor descriptor = importer.from(getConfigFile().getResourceInputStream());
      return descriptor;
   }

   @Override
   public void saveConfig(final WebAppDescriptor descriptor)
   {
      String output = descriptor.exportAsString();
      getConfigFile().setContents(output);
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
      return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "web.xml");
   }

   /**
    * List all servlet resource files.
    */
   @Override
   public List<Resource<?>> getResources()
   {
      DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
      return listChildrenRecursively(webRoot);
   }

   private List<Resource<?>> listChildrenRecursively(final DirectoryResource webRoot)
   {
      return listChildrenRecursively(webRoot, new ResourceFilter()
      {
         @Override
         public boolean accept(final Resource<?> resource)
         {
            return true;
         }
      });
   }

   @Override
   public List<Resource<?>> getResources(final ResourceFilter filter)
   {
      DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
      return listChildrenRecursively(webRoot, filter);
   }

   private List<Resource<?>> listChildrenRecursively(final DirectoryResource current, final ResourceFilter filter)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      List<Resource<?>> list = current.listResources();
      if (list != null)
      {
         for (Resource<?> file : list)
         {
            if (file instanceof DirectoryResource)
            {
               result.addAll(listChildrenRecursively((DirectoryResource) file, filter));
            }
            if (filter.accept(file))
               result.add(file);
         }
      }
      return result;
   }

   /*
    * Facet Methods
    */
   @Override
   public boolean isInstalled()
   {
      DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
      return webRoot.exists() && getConfigFile().exists() && project.getFacet(DependencyFacet.class).hasDependency(dep);
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         String projectName = project.getFacet(MetadataFacet.class).getProjectName();

         project.getFacet(DependencyFacet.class).addDependency(dep);

         DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
         if (!webRoot.exists())
         {
            webRoot.mkdirs();
         }

         FileResource<?> descriptor = getConfigFile();
         if (!descriptor.exists())
         {
            WebAppDescriptor unit = Descriptors.create(WebAppDescriptor.class)
                     .displayName(projectName)
                     .sessionTimeout(30)
                     .welcomeFile("/index.html");

            unit.errorPage(404, "/404.html");
            unit.errorPage(500, "/500.html");

            descriptor.setContents(unit.exportAsString());
         }

         ((FileResource<?>) webRoot.getChild("404.html")).setContents(getClass().getResourceAsStream(
                  "/org/jboss/forge/web/404.html"));
         ((FileResource<?>) webRoot.getChild("500.html")).setContents(getClass().getResourceAsStream(
                  "/org/jboss/forge/web/500.html"));
         ((FileResource<?>) webRoot.getChild("index.html")).setContents(getClass().getResourceAsStream(
                  "/org/jboss/forge/web/index.html"));
         ((FileResource<?>) webRoot.getChild("forge-logo.png")).setContents(getClass().getResourceAsStream(
                  "/org/jboss/forge/web/forge-logo.png"));
         ((FileResource<?>) webRoot.getChild("forge-style.css")).setContents(getClass().getResourceAsStream(
                  "/org/jboss/forge/web/forge-style.css"));
         ((FileResource<?>) webRoot.getChild("jboss-community.png")).setContents(getClass().getResourceAsStream(
                  "/org/jboss/forge/web/jboss-community.png"));
         ((FileResource<?>) webRoot.getChild("bkg.gif")).setContents(getClass().getResourceAsStream(
                  "/org/jboss/forge/web/bkg.gif"));
         ((FileResource<?>) webRoot.getChild("favicon.ico")).setContents(getClass().getResourceAsStream(
                  "/org/jboss/forge/web/favicon.ico"));
      }
      return true;
   }
}
