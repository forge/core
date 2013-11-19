/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.servlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.shrinkwrap.descriptor.api.webapp.WebAppCommonDescriptor;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractServletFacet<DESCRIPTOR extends WebAppCommonDescriptor> extends AbstractJavaEEFacet
         implements ServletFacet<DESCRIPTOR>
{
   @Inject
   public AbstractServletFacet(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public boolean isInstalled()
   {
      Project project = getFaceted();
      DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
      return super.isInstalled() && webRoot.exists();
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         Project project = getFaceted();
         DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
         if (!webRoot.exists())
         {
            webRoot.mkdirs();
         }
      }
      return super.install();
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      Project project = getFaceted();
      DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
      return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "web.xml");
   }

   /**
    * List all servlet resource files.
    */
   @Override
   public List<Resource<?>> getResources()
   {
      DirectoryResource webRoot = getFaceted().getFacet(WebResourcesFacet.class).getWebRootDirectory();
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
      DirectoryResource webRoot = getFaceted().getFacet(WebResourcesFacet.class).getWebRootDirectory();
      return listChildrenRecursively(webRoot, filter);
   }

   @Override
   public DirectoryResource getWebInfFolder()
   {
      return getFaceted().getFacet(WebResourcesFacet.class).getWebRootDirectory().getChildDirectory("WEB-INF");
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
