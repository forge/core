/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.facets.PersistenceFacet;
import org.jboss.forge.addon.javaee.facets.PersistenceMetaModelFacet;
import org.jboss.forge.addon.parser.java.JavaSourceFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.forge.parser.java.util.Types;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnit;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnitTransactionType;

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
   private ProjectFactory projectFactory;

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
         PersistenceFacet facet = facetFactory.install(PersistenceFacet.class, project);
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
      }
      if (configureMetadata)
      {
         facetFactory.install(PersistenceMetaModelFacet.class, project);
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
      final JavaSourceFacet java = facetFactory.install(JavaSourceFacet.class, project);
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
      Refactory.createHashCodeAndEquals(javaClass);
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

   /**
    * 
    * @param project
    * @param targetEntity
    * @param fieldType
    * @param fieldName
    * @param annotation
    * @return
    * @throws FileNotFoundException
    */
   public Field<JavaClass> addFieldTo(final JavaClass targetEntity, final String fieldType,
            final String fieldName, String... annotations)
            throws FileNotFoundException
   {
      if (targetEntity.hasField(fieldName))
      {
         throw new IllegalStateException("Entity already has a field named [" + fieldName + "]");
      }
      Field<JavaClass> field = targetEntity.addField();
      field.setName(fieldName).setPrivate().setType(fieldType);
      for (String annotation : annotations)
      {
         field.addAnnotation(annotation);
      }

      String fieldTypeForImport = Types.stripArray(fieldType);
      if (!fieldTypeForImport.startsWith("java.lang.") && fieldTypeForImport.contains(".")
               && !fieldTypeForImport.equals(targetEntity.getCanonicalName()))
      {
         targetEntity.addImport(fieldTypeForImport);
      }
      Refactory.createGetterAndSetter(targetEntity, field);
      updateToString(targetEntity);
      return field;
   }

   private void updateToString(final JavaClass targetEntity)
   {
      if (targetEntity.hasMethodSignature("toString"))
      {
         targetEntity.removeMethod(targetEntity.getMethod("toString"));
      }
      List<Field<JavaClass>> fields = new ArrayList<Field<JavaClass>>();
      for (Field<JavaClass> f : targetEntity.getFields())
      {
         if (!"id".equals(f.getName()) && !"version".equals(f.getName())
                  && (f.getTypeInspector().isPrimitive() || Types.isJavaLang(f.getType())))
         {
            fields.add(f);
         }
      }
      if (!fields.isEmpty())
      {
         Refactory.createToStringFromFields(targetEntity, fields);
      }
   }
}
