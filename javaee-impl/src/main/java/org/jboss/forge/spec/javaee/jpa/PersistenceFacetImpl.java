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

   private static final String DEFAULT_ENTITY_PACKAGE = "model";

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

            PersistenceDescriptor descriptorContents = Descriptors.create(PersistenceDescriptor.class)
                     .version("2.0");
            descriptor.setContents(descriptorContents.exportAsString());
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
      PersistenceDescriptor descriptor = importer.from(getConfigFile().getResourceInputStream());
      return descriptor;
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
