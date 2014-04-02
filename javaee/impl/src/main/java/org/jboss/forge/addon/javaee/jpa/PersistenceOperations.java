/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.util.Refactory;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * This class contains JPA specific operations
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class PersistenceOperations
{
   public static final String DEFAULT_UNIT_SUFFIX = "-persistence-unit";
   public static final String DEFAULT_UNIT_DESC = "Forge Persistence Unit";

   @Inject
   private FacetFactory facetFactory;

   /**
    * Setups JPA in the project
    *
    * @param project
    * @param dataSource
    * @param configureMetadata
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public FileResource<?> setup(String unitName, Project project,
            JPADataSource dataSource,
            boolean configureMetadata)
   {
      FileResource<?> result = null;
      if (project != null)
      {
         JPAFacet<PersistenceCommonDescriptor> facet = project.getFacet(JPAFacet.class);
         PersistenceContainer container = dataSource.getContainer();
         PersistenceProvider provider = dataSource.getProvider();
         PersistenceCommonDescriptor config = facet.getConfig();
         PersistenceUnitCommon unit = config.createPersistenceUnit();
         unit.name(unitName).description(DEFAULT_UNIT_DESC);

         if (provider.getProvider() != null)
         {
            unit.provider(provider.getProvider());
         }

         container.setupConnection(unit, dataSource);
         provider.configure(unit, dataSource, project);
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
   public JavaResource newEntity(Project project, String entityName, String entityPackage, GenerationType idStrategy,
            String tableName)
            throws FileNotFoundException
   {
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource javaClass = createJavaClass(entityName, entityPackage, idStrategy, tableName);
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
    * @param tableName the table name (optional)
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEntity(DirectoryResource target, String entityName, String entityPackage,
            GenerationType idStrategy, String tableName)
   {
      JavaClassSource javaClass = createJavaClass(entityName, entityPackage, idStrategy, tableName);
      JavaResource javaResource = getJavaResource(target, javaClass.getName());
      javaResource.setContents(javaClass);
      return javaResource;
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
      return newEntity(project, entityName, entityPackage, idStrategy, null);
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
      return newEntity(target, entityName, entityPackage, idStrategy, null);
   }

   @SuppressWarnings("unchecked")
   private JavaClassSource createJavaClass(String entityName, String entityPackage, GenerationType idStrategy,
            String tableName)
   {
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class)
               .setName(entityName)
               .setPublic()
               .addAnnotation(Entity.class).getOrigin()
               .addInterface(Serializable.class);
      if (tableName != null && !tableName.isEmpty())
      {
         javaClass.addAnnotation(Table.class).setStringValue("name", tableName);
      }
      if (entityPackage != null && !entityPackage.isEmpty())
      {
         javaClass.setPackage(entityPackage);
      }
      FieldSource<JavaClassSource> id = javaClass.addField("private Long id;");
      id.addAnnotation(Id.class);
      id.addAnnotation(GeneratedValue.class)
               .setEnumValue("strategy", idStrategy);
      id.addAnnotation(Column.class)
               .setStringValue("name", "id")
               .setLiteralValue("updatable", "false")
               .setLiteralValue("nullable", "false");

      FieldSource<JavaClassSource> version = javaClass.addField("private int version;");
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
      path = path.replace(".", File.separator) + ".java";
      JavaResource target = sourceDir.getChildOfType(JavaResource.class, path);
      return target;
   }
}