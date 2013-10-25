/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractJPAFacetImpl<DESCRIPTOR extends PersistenceCommonDescriptor> extends AbstractJavaEEFacet
         implements
         JPAFacet<DESCRIPTOR>
{

   public static final String DEFAULT_ENTITY_PACKAGE = "model";

   public AbstractJPAFacetImpl(DependencyInstaller installer)
   {
      super(installer);
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

   protected abstract void createDefaultConfig(FileResource<?> descriptor);

}
