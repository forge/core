/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;

/**
 * Implementation of {@link CDIFacet} for spec version 1.0
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class AbstractCDIFacetImpl extends AbstractJavaEEFacet implements CDIFacet
{
   @Inject
   public AbstractCDIFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public BeansDescriptor getConfig()
   {
      DescriptorImporter<BeansDescriptor> importer = Descriptors.importAs(BeansDescriptor.class);
      BeansDescriptor descriptor = importer.fromStream(getConfigFile().getResourceInputStream());
      return descriptor;
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         FileResource<?> descriptor = getConfigFile();
         if (!descriptor.exists())
         {
            if (!descriptor.createNewFile())
            {
               throw new RuntimeException("Failed to create required [" + descriptor.getFullyQualifiedName() + "]");
            }
            String data = getInitialBeansXMLContent();
            descriptor.setContents(data);
         }
      }
      return super.install();
   }

   @Override
   public boolean isInstalled()
   {
      return getConfigFile().exists() && super.isInstalled();
   }

   protected abstract String getInitialBeansXMLContent();

   @Override
   public void saveConfig(BeansDescriptor model)
   {
      String output = model.exportAsString();
      getConfigFile().setContents(output);
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      Project project = getFaceted();
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      if ("war".equals(packaging.getPackagingType()))
      {
         DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
         return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "beans.xml");
      }
      else
      {
         DirectoryResource root = project.getFacet(ResourcesFacet.class).getResourceFolder();
         return (FileResource<?>) root.getChild("META-INF" + File.separator + "beans.xml");
      }
   }

}
