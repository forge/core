/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa;

import java.io.FileNotFoundException;
import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.JavaSourceFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.shrinkwrap.descriptor.api.persistence21.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence21.PersistenceUnit;
import org.jboss.shrinkwrap.descriptor.api.persistence21.PersistenceUnitTransactionType;

/**
 * This class contains JPA specific operations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class PersistenceOperations
{
   public static final String DEFAULT_UNIT_NAME = "forge-default";
   public static final String DEFAULT_UNIT_DESC = "Forge Persistence Unit";

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private JavaSourceFactory javaSourceFactory;

   /**
    * Setups JPA in the project
    * 
    * @param project
    * @param dataSource
    * @param configureMetadata
    */
   public FileResource<?> setup(String unitName, Project project, JPADataSource dataSource, boolean configureMetadata)
   {
      FileResource<?> result = null;
      if (project != null)
      {
         PersistenceFacet facet = facetFactory.install(project, PersistenceFacet.class);
         PersistenceContainer container = dataSource.getContainer();
         PersistenceProvider provider = dataSource.getProvider();
         PersistenceDescriptor config = facet.getConfig();
         PersistenceUnit<PersistenceDescriptor> unit = config.createPersistenceUnit();
         unit.name(unitName).description(DEFAULT_UNIT_DESC);
         unit.transactionType(container.isJTASupported() ? PersistenceUnitTransactionType._JTA
                  : PersistenceUnitTransactionType._RESOURCE_LOCAL);
         unit.provider(provider.getProvider());

         container.setupConnection(unit, dataSource);
         provider.configure(unit, dataSource);
         facet.saveConfig(config);
         result = facet.getConfigFile();
         if (configureMetadata)
         {
            Iterable<PersistenceMetaModelFacet> facets = facetFactory.createFacets(project,
                     PersistenceMetaModelFacet.class);
            for (PersistenceMetaModelFacet metaModelFacet : facets)
            {
               metaModelFacet.setMetaModelProvider(provider.getMetaModelProvider());
               if (facetFactory.install(project, metaModelFacet))
               {
                  break;
               }
            }
         }
      }
      return result;
   }

   /**
    * Creates a new {@link JavaResource} in the specified project. If no project is available, use
    * {@link PersistenceOperations#newEntity(DirectoryResource, String, String, GenerationType)}
    * 
    * @param project the current project to create the entity. Must not be null
    * @param entityName the name of the entity
    * @param entityPackage the package of the entity to be created
    * @param idStrategy the ID strategy chosen for this entity
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEntity(Project project, String entityName, String entityPackage, GenerationType idStrategy)
            throws FileNotFoundException
   {
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass javaClass = createJavaClass(entityName, entityPackage, idStrategy);
      return java.saveJavaSource(javaClass);
   }

   /**
    * Creates a new {@link JavaResource} in the specified target. If a project is available, use
    * {@link PersistenceOperations#newEntity(Project, String, String, GenerationType)}
    * 
    * @param target the target directory resource to create this class
    * @param entityName the name of the entity
    * @param entityPackage the package of the entity to be created
    * @param idStrategy the ID strategy chosen for this entity
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEntity(DirectoryResource target, String entityName, String entityPackage,
            GenerationType idStrategy)
   {
      JavaClass javaClass = createJavaClass(entityName, entityPackage, idStrategy);
      JavaResource javaResource = getJavaResource(target, javaClass.getName());
      javaResource.setContents(javaClass);
      return javaResource;
   }

   @SuppressWarnings("unchecked")
   private JavaClass createJavaClass(String entityName, String entityPackage, GenerationType idStrategy)
   {
      JavaClass javaClass = javaSourceFactory.create(JavaClass.class)
               .setName(entityName)
               .setPublic()
               .addAnnotation(Entity.class).getOrigin()
               .addInterface(Serializable.class);
      if (entityPackage != null && !entityPackage.isEmpty())
      {
         javaClass.setPackage(entityPackage);
      }
      Field<JavaClass> id = javaClass.addField("private Long id = null;");
      id.addAnnotation(Id.class);
      id.addAnnotation(GeneratedValue.class)
               .setEnumValue("strategy", idStrategy);
      id.addAnnotation(Column.class)
               .setStringValue("name", "id")
               .setLiteralValue("updatable", "false")
               .setLiteralValue("nullable", "false");

      Field<JavaClass> version = javaClass.addField("private int version = 0;");
      version.addAnnotation(Version.class);
      version.addAnnotation(Column.class).setStringValue("name", "version");

      Refactory.createGetterAndSetter(javaClass, id);
      Refactory.createGetterAndSetter(javaClass, version);
      Refactory.createToStringFromFields(javaClass, id);
      Refactory.createHashCodeAndEquals(javaClass, id);
      return javaClass;
   }

   private JavaResource getJavaResource(final DirectoryResource sourceDir, final String relativePath)
   {
      String path = relativePath.trim().endsWith(".java")
               ? relativePath.substring(0, relativePath.lastIndexOf(".java")) : relativePath;
      path = path.replace(".", "/") + ".java";
      JavaResource target = sourceDir.getChildOfType(JavaResource.class, path);
      return target;
   }
}