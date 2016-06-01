/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.javaee.JavaEEPackageConstants;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;

/**
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractJPAFacetImpl<DESCRIPTOR extends PersistenceCommonDescriptor> extends AbstractJavaEEFacet
         implements JPAFacet<DESCRIPTOR>
{

   public AbstractJPAFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "JPA";
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
      FileResource<?> configFile = getConfigFile();
      if (!configFile.exists())
      {
         return false;
      }
      return getSpecVersion().toString().equals(getConfig().getVersion());
   }

   /*
    * Facet methods
    */

   @Override
   public String getEntityPackage()
   {
      JavaSourceFacet sourceFacet = getFaceted().getFacet(JavaSourceFacet.class);
      return sourceFacet.getBasePackage() + "." + JavaEEPackageConstants.DEFAULT_ENTITY_PACKAGE;
   }

   @Override
   public DirectoryResource getEntityPackageDir()
   {
      JavaSourceFacet sourceFacet = getFaceted().getFacet(JavaSourceFacet.class);

      DirectoryResource entityRoot = sourceFacet.getBasePackageDirectory().getChildDirectory(
               JavaEEPackageConstants.DEFAULT_ENTITY_PACKAGE);
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
      return (FileResource<?>) resources.getResourceDirectory().getChild(
               "META-INF" + File.separator + "persistence.xml");
   }

   @Override
   public List<JavaClassSource> getAllEntities()
   {
      final List<JavaClassSource> result = new ArrayList<>();
      JavaSourceFacet javaSourceFacet = getFaceted().getFacet(JavaSourceFacet.class);
      javaSourceFacet.visitJavaSources(new JavaResourceVisitor()
      {
         @Override
         public void visit(VisitContext context, JavaResource resource)
         {
            try
            {
               JavaType<?> type = resource.getJavaType();
               if (type.hasAnnotation(Entity.class) && type.isClass())
               {
                  result.add((JavaClassSource) type);
               }
            }
            catch (FileNotFoundException e)
            {
               throw new IllegalStateException(e);
            }
         }
      });

      return result;
   }

   protected abstract void createDefaultConfig(FileResource<?> descriptor);

}
