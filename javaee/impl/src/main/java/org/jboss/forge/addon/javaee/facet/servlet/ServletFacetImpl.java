/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.facet.servlet;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.facet.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.facet.ServletFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.WebResourceFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.container.util.Streams;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;

public class ServletFacetImpl extends AbstractJavaEEFacet implements ServletFacet
{
   private static final Dependency JAVAX_SERVLET_API = DependencyBuilder
            .create("org.jboss.spec.javax.servlet:jboss-servlet-api_3.0_spec");

   @Inject
   private FacetFactory facetFactory;

   @Inject
   public ServletFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> map = new LinkedHashMap<Dependency, List<Dependency>>();
      map.put(JAVAX_SERVLET_API, Arrays.asList(JAVAX_SERVLET_API));
      return map;
   }

   @Override
   public boolean isInstalled()
   {
      Project project = getOrigin();
      if (!project.hasFacet(WebResourceFacet.class))
      {
         return false;
      }
      DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
      return webRoot.exists();
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         Project project = getOrigin();
         // Install required facets
         WebResourceFacet webFacet = facetFactory.install(WebResourceFacet.class, project);
         DirectoryResource webRoot = webFacet.getWebRootDirectory();
         if (!webRoot.exists())
         {
            webRoot.mkdirs();
         }
      }
      return super.install();
   }

   /*
    * Facet Methods
    */
   @SuppressWarnings("resource")
   @Override
   public WebAppDescriptor getConfig()
   {
      DescriptorImporter<WebAppDescriptor> importer = Descriptors.importAs(WebAppDescriptor.class);
      FileResource<?> configFile = getConfigFile();
      InputStream inputStream = configFile.getResourceInputStream();
      try
      {
         WebAppDescriptor descriptor = importer.fromStream(inputStream);
         return descriptor;
      }
      finally
      {
         Streams.closeQuietly(inputStream);
      }
   }

   @Override
   public void saveConfig(final WebAppDescriptor descriptor)
   {
      String output = descriptor.exportAsString();

      FileResource<?> configFile = getConfigFile();
      configFile.setContents(output);
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      Project project = getOrigin();
      DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
      final FileResource<?> child = (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "web.xml");

      if (!child.exists())
      {
         String projectName = project.getFacet(MetadataFacet.class).getProjectName();
         WebAppDescriptor unit = Descriptors.create(WebAppDescriptor.class)
                  .displayName(projectName)
                  .createSessionConfig()
                  .sessionTimeout(30).up();
         child.setContents(unit.exportAsString());
      }

      return child;
   }

   /**
    * List all servlet resource files.
    */
   @Override
   public List<Resource<?>> getResources()
   {
      DirectoryResource webRoot = getOrigin().getFacet(WebResourceFacet.class).getWebRootDirectory();
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
      DirectoryResource webRoot = getOrigin().getFacet(WebResourceFacet.class).getWebRootDirectory();
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

}
