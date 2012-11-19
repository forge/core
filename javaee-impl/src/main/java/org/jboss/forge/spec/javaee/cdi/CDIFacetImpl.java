/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.cdi;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.packaging.events.PackagingChanged;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.cdi.beans.BeansDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.cdi")
@RequiresFacet({ ResourceFacet.class, PackagingFacet.class })
public class CDIFacetImpl extends BaseJavaEEFacet implements CDIFacet
{
   @Inject
   public CDIFacetImpl(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public boolean isInstalled()
   {
      return getConfigFile(project).exists()
               && super.isInstalled();
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         FileResource<?> descriptor = getConfigFile(project);
         if (!descriptor.exists())
         {
            if (!descriptor.createNewFile())
            {
               throw new RuntimeException("Failed to create required [" + descriptor.getFullyQualifiedName() + "]");
            }

            descriptor.setContents(getClass()
                     .getResourceAsStream("/org/jboss/forge/web/beans.xml"));
         }
      }

      return super.install();
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

   /*
    * Facet methods
    */

   public void updateConfigLocation(@Observes final PackagingChanged event)
   {
      Project project = event.getProject();
      if (project.hasFacet(CDIFacetImpl.class))
      {
         PackagingType oldType = event.getOldPackagingType();
         FileResource<?> configFile = getConfigFile(project, oldType);

         BeansDescriptor config = getConfig(project, configFile);
         saveConfig(project, config);

         configFile.delete();
      }
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      return getConfigFile(project);
   }

   private FileResource<?> getConfigFile(final Project project)
   {
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      return getConfigFile(project, packaging.getPackagingType());
   }

   private FileResource<?> getConfigFile(final Project project, final PackagingType type)
   {
      if (PackagingType.WAR.equals(type))
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
   public BeansDescriptor getConfig()
   {
      return getConfig(project, getConfigFile(project));
   }

   private BeansDescriptor getConfig(final Project project, final FileResource<?> file)
   {
      DescriptorImporter<BeansDescriptor> importer = Descriptors.importAs(BeansDescriptor.class);
      BeansDescriptor descriptor = importer.from(file.getResourceInputStream());
      return descriptor;
   }

   @Override
   public void saveConfig(final BeansDescriptor model)
   {
      saveConfig(project, model);
   }

   private void saveConfig(final Project project, final BeansDescriptor descriptor)
   {
      String output = descriptor.exportAsString();
      getConfigFile(project).setContents(output);
   }
}
