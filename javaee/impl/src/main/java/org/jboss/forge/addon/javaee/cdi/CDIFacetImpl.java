/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.facets.CDIFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourceFacet;
import org.jboss.forge.addon.projects.facets.WebResourceFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;

/**
 * Implementation of {@link CDIFacet}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class CDIFacetImpl extends AbstractJavaEEFacet implements CDIFacet
{
   private static final Dependency JBOSS_ANNOTATION_API = DependencyBuilder
            .create("org.jboss.spec.javax.annotation:jboss-annotations-api_1.1_spec");
   private static final Dependency JAVAX_INTERCEPTOR_API = DependencyBuilder
            .create("org.jboss.spec.javax.interceptor:jboss-interceptors-api_1.1_spec");
   private static final Dependency JAVAX_INJECT = DependencyBuilder.create("javax.inject:javax.inject");
   private static final Dependency JAVAX_ANNOTATION_API = DependencyBuilder.create("javax.annotation:jsr250-api");
   private static final Dependency CDI_API = DependencyBuilder.create("javax.enterprise:cdi-api");

   @Inject
   public CDIFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public boolean isInstalled()
   {
      return getConfigFile().exists() && super.isInstalled();
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
      Project project = getFaceted();
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      if ("war".equals(packaging.getPackagingType()))
      {
         DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
         return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "beans.xml");
      }
      else
      {
         DirectoryResource root = project.getFacet(ResourceFacet.class).getResourceFolder();
         return (FileResource<?>) root.getChild("META-INF" + File.separator + "beans.xml");
      }
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      Map<Dependency, List<Dependency>> result = new LinkedHashMap<Dependency, List<Dependency>>();

      result.put(CDI_API, Arrays.asList(CDI_API));
      result.put(JAVAX_ANNOTATION_API, Arrays.asList(JAVAX_ANNOTATION_API, JBOSS_ANNOTATION_API));
      result.put(JAVAX_INJECT, Arrays.asList(JAVAX_INJECT));
      result.put(JAVAX_INTERCEPTOR_API, Arrays.asList(JAVAX_INTERCEPTOR_API));

      return result;
   }

}
