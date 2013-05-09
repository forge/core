/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.javaee.cdi;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.javaee.BaseJavaEEFacet;
import org.jboss.forge.javaee.spec.CDIFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.dependencies.DependencyInstaller;
import org.jboss.forge.projects.facets.PackagingFacet;
import org.jboss.forge.projects.facets.ResourceFacet;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.FileResource;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;

/**
 * Implementation of {@link CDIFacet}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class CDIFacetImpl extends BaseJavaEEFacet implements CDIFacet
{

   @Inject
   public CDIFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public boolean isInstalled()
   {
      return getConfigFile().exists()
               && super.isInstalled();
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
            String data = Descriptors.create(BeansDescriptor.class).exportAsString();
            descriptor.setContents(data);
         }
      }
      return super.install();
   }

   @Override
   public void saveConfig(BeansDescriptor model)
   {
      String output = model.exportAsString();
      getConfigFile().setContents(output);
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      Project project = getOrigin();
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      if ("war".equals(packaging.getPackagingType()))
      {
         // DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
         // return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "beans.xml");
         throw new UnsupportedOperationException("Not implemented yet");
      }
      else
      {
         DirectoryResource root = project.getFacet(ResourceFacet.class).getResourceFolder();
         return (FileResource<?>) root.getChild("META-INF" + File.separator + "beans.xml");
      }
   }

   @Override
   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList(
               (Dependency) DependencyBuilder.create("javax.enterprise:cdi-api"),
               DependencyBuilder.create("javax.inject:javax.inject"),
               DependencyBuilder.create("org.jboss.spec.javax.interceptor:jboss-interceptors-api_1.1_spec"),
               DependencyBuilder.create("org.jboss.spec.javax.annotation:jboss-annotations-api_1.1_spec")
               );
   }

}
