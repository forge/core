/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.Entity;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.Descriptors;
import org.jboss.forge.addon.javaee.facets.PersistenceFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PersistenceFacetImpl extends AbstractJavaEEFacet implements PersistenceFacet
{
   public static final String DEFAULT_ENTITY_PACKAGE = "model";
   private final Dependency JAVAX_PERSISTENCE = DependencyBuilder
            .create("org.hibernate.javax.persistence:hibernate-jpa-2.0-api");

   @Inject
   public PersistenceFacetImpl(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public Version getSpecVersion()
   {
      return new SingleVersion("2.0");
   }

   @Override
   protected Map<Dependency, List<Dependency>> getRequiredDependencyOptions()
   {
      return Collections.singletonMap(JAVAX_PERSISTENCE, Arrays.asList(JAVAX_PERSISTENCE));
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
      JavaSourceFacet sourceFacet = getFaceted().getFacet(JavaSourceFacet.class);
      return sourceFacet.getBasePackage() + "." + DEFAULT_ENTITY_PACKAGE;
   }

   @Override
   public DirectoryResource getEntityPackageDir()
   {
      JavaSourceFacet sourceFacet = getFaceted().getFacet(JavaSourceFacet.class);

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
      PersistenceDescriptor descriptor = importer.fromStream(configFile.getResourceInputStream());
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
      ResourcesFacet resources = getFaceted().getFacet(ResourcesFacet.class);
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
