/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.servlet;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;
import org.jboss.forge.resources.UnknownFileResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.servlet")
@RequiresFacet({ MetadataFacet.class, WebResourceFacet.class, DependencyFacet.class })
@RequiresPackagingType({ PackagingType.WAR, PackagingType.BUNDLE })
public class ServletFacetImpl extends BaseJavaEEFacet implements ServletFacet
{
   @Inject
   public ServletFacetImpl(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays
               .asList((Dependency) DependencyBuilder.create("org.jboss.spec.javax.servlet:jboss-servlet-api_3.0_spec")
                        .setScopeType(ScopeType.PROVIDED));
   }

   @Override
   public boolean isInstalled()
   {
      DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
      return webRoot.exists();
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
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
   @Override
   public WebAppDescriptor getConfig()
   {
      DescriptorImporter<WebAppDescriptor> importer = Descriptors.importAs(WebAppDescriptor.class);
      FileResource<?> configFile = getConfigFile();
      InputStream inputStream = configFile.getResourceInputStream();
      WebAppDescriptor descriptor = importer.from(inputStream);
      return descriptor;
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
      DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
      final FileResource<?> child = (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "web.xml");

      if (!child.exists())
      {
         return new UnknownFileResource(child.getResourceFactory(), child.getUnderlyingResourceObject())
         {
            @Override
            public InputStream getResourceInputStream()
            {
               if (!exists())
               {
                  String projectName = project.getFacet(MetadataFacet.class).getProjectName();
                  WebAppDescriptor unit = Descriptors.create(WebAppDescriptor.class)
                           .displayName(projectName)
                           .sessionTimeout(30);
                  return Streams.fromString(unit.exportAsString());
               }
               else
               {
                  return super.getResourceInputStream();
               }
            }

            @Override
            public UnknownFileResource setContents(InputStream data)
            {
               if(!exists())
               {
                  createNewFile();
               }
               return super.setContents(data);
            }

            @Override
            public UnknownFileResource setContents(char[] data)
            {
               if(!exists())
               {
                  createNewFile();
               }
               return super.setContents(data);
            }

            @Override
            public UnknownFileResource setContents(String data)
            {
               if(!exists())
               {
                  createNewFile();
               }
               return super.setContents(data);
            }
         };
      }

      return child;
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
}
