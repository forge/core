/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Entity;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.jpa")
@RequiresFacet({ JavaSourceFacet.class, ResourceFacet.class, DependencyFacet.class })
@RequiresPackagingType({ PackagingType.JAR, PackagingType.WAR, PackagingType.BUNDLE })
public class PersistenceFacetImpl extends BaseJavaEEFacet implements PersistenceFacet
{
   public static final String DEFAULT_ENTITY_PACKAGE = "model";

   @Inject
   public PersistenceFacetImpl(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList((Dependency) DependencyBuilder
               .create("org.hibernate.javax.persistence:hibernate-jpa-2.0-api"));
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         FileResource<?> descriptor = getConfigFile();
         if (!descriptor.exists())
         {
            createDefaultConfig(descriptor);
         }
      }
      return super.install();
   }

   @Override
   public boolean isInstalled()
   {
      return super.isInstalled() && getConfigFile().exists();
   }

   /*
    * Facet methods
    */

   @Override
   public String getEntityPackage()
   {
      JavaSourceFacet sourceFacet = project.getFacet(JavaSourceFacet.class);
      return sourceFacet.getBasePackage() + "." + DEFAULT_ENTITY_PACKAGE;
   }

   @Override
   public DirectoryResource getEntityPackageDir()
   {
      JavaSourceFacet sourceFacet = project.getFacet(JavaSourceFacet.class);

      DirectoryResource entityRoot = sourceFacet.getBasePackageResource().getChildDirectory(DEFAULT_ENTITY_PACKAGE);
      if (!entityRoot.exists())
      {
         entityRoot.mkdirs();
      }

      return entityRoot;
   }

   @Override
   public PersistenceDescriptor getConfig()
   {
      DescriptorImporter<PersistenceDescriptor> importer = Descriptors.importAs(PersistenceDescriptor.class);
      final FileResource<?> configFile = getConfigFile();
      if (!configFile.exists())
      {
         createDefaultConfig(configFile);
      }
      PersistenceDescriptor descriptor = importer.from(configFile.getResourceInputStream());
      return descriptor;
   }
   
   private void createDefaultConfig(FileResource<?> descriptor)
   {
      PersistenceDescriptor descriptorContents = Descriptors.create(PersistenceDescriptor.class)
               .version("2.0");
      descriptor.setContents(descriptorContents.exportAsString());
   }

   @Override
   public void saveConfig(final PersistenceDescriptor descriptor)
   {
      String output = descriptor.exportAsString();
      getConfigFile().setContents(output);
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      ResourceFacet resources = project.getFacet(ResourceFacet.class);
      return (FileResource<?>) resources.getResourceFolder().getChild("META-INF" + File.separator + "persistence.xml");
   }

   @Override
   public List<JavaClass> getAllEntities()
   {
      DirectoryResource packageFile = getEntityPackageDir();
      return findEntitiesInFolder(packageFile);
   }

   private List<JavaClass> findEntitiesInFolder(final DirectoryResource packageFile)
   {
      List<JavaClass> result = new ArrayList<JavaClass>();
      if (packageFile.exists())
      {
         for (Resource<?> source : packageFile.listResources())
         {
            if (source instanceof JavaResource)
            {
               try
               {
                  JavaSource<?> javaClass = ((JavaResource) source).getJavaSource();
                  if (javaClass.hasAnnotation(Entity.class) && javaClass.isClass())
                  {
                     result.add((JavaClass) javaClass);
                  }
               }
               catch (FileNotFoundException e)
               {
                  throw new IllegalStateException(e);
               }
            }
         }

         for (Resource<?> source : packageFile.listResources())
         {
            if (source instanceof DirectoryResource)
            {
               List<JavaClass> subResults = findEntitiesInFolder((DirectoryResource) source);
               result.addAll(subResults);
            }
         }
      }
      return result;
   }
}
