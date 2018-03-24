/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Version;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.beans.FieldOperations;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.PropertySource;
import org.jboss.forge.roaster.model.util.Refactory;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * This class contains JPA specific operations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:ch.schulz@joinout.de">Christoph "criztovyl" Schulz</a>
 * 
 */
public class PersistenceOperationsImpl implements PersistenceOperations
{
   @Inject
   private FacetFactory facetFactory;

   @Inject
   private FieldOperations beanOperations;

   @Override
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
         PersistenceUnitCommon unit = null;
         List<PersistenceUnitCommon> allPersistenceUnit = config.getAllPersistenceUnit();
         for (PersistenceUnitCommon persistenceUnit : allPersistenceUnit)
         {
            if (unitName.equals(persistenceUnit.getName()))
            {
               unit = persistenceUnit;
               break;
            }
         }
         if (unit == null)
         {
            unit = config.createPersistenceUnit();
         }
         else
         {
            // FORGE-2049: Call all Remove methods until there is a decent way to do this in ShrinkWrap Descriptors
            unit.removeAllClazz().removeAllJarFile().removeAllMappingFile().removeDescription()
                     .removeExcludeUnlistedClasses().removeJtaDataSource().removeName().removeNonJtaDataSource()
                     .removeProperties().removeProvider();
         }
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

   @Override
   public JavaResource newEntity(Project project, String entityName, String entityPackage, GenerationType idStrategy,
            String tableName)
            throws FileNotFoundException
   {
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource javaClass = createJavaEntityClass(entityName, entityPackage, idStrategy, tableName);
      return java.saveJavaSource(javaClass);
   }

   @Override
   public JavaResource newEntity(DirectoryResource target, String entityName, String entityPackage,
            GenerationType idStrategy, String tableName)
   {
      JavaClassSource javaClass = createJavaEntityClass(entityName, entityPackage, idStrategy, tableName);
      JavaResource javaResource = getJavaResource(target, javaClass.getName());
      javaResource.setContents(javaClass);
      return javaResource;
   }

   @Override
   public JavaResource newEmbeddableEntity(Project project, String entityName, String entityPackage)
            throws FileNotFoundException
   {
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource javaClass = createJavaEmbeddableClass(entityName, entityPackage);
      return java.saveJavaSource(javaClass);
   }

   @Override
   public JavaResource newEmbeddableEntity(DirectoryResource target, String entityName, String entityPackage)
   {
      JavaClassSource javaClass = createJavaEmbeddableClass(entityName, entityPackage);
      JavaResource javaResource = getJavaResource(target, javaClass.getName());
      javaResource.setContents(javaClass);
      return javaResource;
   }

   @Override
   public JavaClassSource newEmbeddableEntity(JavaClassSource source)
   {
      return createJavaEmbeddableClass(source.getName(), source.getPackage());
   }

   @Override
   public JavaResource newEntity(Project project, String entityName, String entityPackage, GenerationType idStrategy)
            throws FileNotFoundException
   {
      return newEntity(project, entityName, entityPackage, idStrategy, null);
   }

   @Override
   public JavaResource newEntity(DirectoryResource target, String entityName, String entityPackage,
            GenerationType idStrategy)
   {
      return newEntity(target, entityName, entityPackage, idStrategy, null);
   }

   @Override
   public JavaClassSource newEntity(JavaClassSource javaClass, GenerationType idStrategy, String tableName)
   {
      return newEntity(javaClass, idStrategy, tableName, "id", "version");
   }

   @SuppressWarnings("unchecked")
   @Override
   public JavaClassSource newEntity(JavaClassSource javaClass, GenerationType idStrategy, String tableName,
            String idPropertyName, String versionPropertyName)
   {
      FieldSource<JavaClassSource> id = beanOperations.addFieldTo(javaClass, "Long", idPropertyName);
      id.addAnnotation(Id.class);
      id.addAnnotation(GeneratedValue.class)
               .setEnumValue("strategy", idStrategy);
      id.addAnnotation(Column.class)
               .setStringValue("name", "id")
               .setLiteralValue("updatable", "false")
               .setLiteralValue("nullable", "false");

      return newEntity(javaClass, tableName, id, versionPropertyName);
   }

   @Override
   public JavaClassSource newEntityEmbeddedId(JavaClassSource javaClass, String tableName, String idPropertyName,
            String idPropertyType, String versionPropertyName)
   {
      FieldSource<JavaClassSource> id = beanOperations.addFieldTo(javaClass, idPropertyType, idPropertyName);
      id.addAnnotation(EmbeddedId.class);

      return newEntity(javaClass, tableName, id, versionPropertyName);
   }

   @Override
   public JavaClassSource newEntityIdClass(JavaClassSource javaClass, String tableName, JavaClassSource idPropertyClass,
           String versionPropertyName)
   {

       ArrayList<FieldSource<JavaClassSource>> props = new ArrayList<>();

       for(PropertySource<JavaClassSource> idProp : idPropertyClass.getProperties()){
           FieldSource<JavaClassSource> prop = beanOperations.addFieldTo(javaClass,
                   idProp.getField().getType().getQualifiedName(), idProp.getField().getName());
           prop.addAnnotation(Id.class);
           props.add(prop);
       }

       javaClass.addAnnotation(IdClass.class)
           // TODO use simple name if it is unambiguous
           .setLiteralValue(javaClass.addImport(idPropertyClass.getEnclosingType()).getQualifiedName() + ".class");

       Refactory.createHashCodeAndEquals(javaClass, props.toArray(new FieldSource<?>[0]));

       return makeClassEntity(javaClass, tableName, versionPropertyName);
   }

   @SuppressWarnings("unchecked")
   private JavaClassSource newEntity(JavaClassSource javaClass, String tableName, FieldSource<JavaClassSource> id,
            String versionPropertyName)
   {
      Refactory.createHashCodeAndEquals(javaClass, id);
      return makeClassEntity(javaClass, tableName, versionPropertyName);
   }

   @SuppressWarnings("unchecked")
   private JavaClassSource makeClassEntity(JavaClassSource javaClass, String tableName, String versionPropertyName)
   {
      javaClass.setPublic()
               .addAnnotation(Entity.class).getOrigin()
               .addInterface(Serializable.class);
      // Add serialVersionUID = 1L initially. It can be re-generated with the Java: Generate SerialVersionUID command
      javaClass.addField("private static final long serialVersionUID = 1L");

      if (tableName != null && !tableName.isEmpty())
      {
         javaClass.addAnnotation(Table.class).setStringValue("name", tableName);
      }

      FieldSource<JavaClassSource> version = beanOperations.addFieldTo(javaClass, "int", versionPropertyName);
      version.addAnnotation(Version.class);
      version.addAnnotation(Column.class).setStringValue("name", "version");

      return javaClass;
   }

   private JavaClassSource createJavaEmbeddableClass(String entityName, String entityPackage)
   {
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class)
               .setName(entityName)
               .setPublic()
               .addAnnotation(Embeddable.class).getOrigin()
               .addInterface(Serializable.class);
      if (entityPackage != null && !entityPackage.isEmpty())
      {
         javaClass.setPackage(entityPackage);
      }

      return javaClass;
   }

   private JavaClassSource createJavaEntityClass(String entityName, String entityPackage, GenerationType idStrategy,
            String tableName)
   {
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class).setName(entityName);
      if (entityPackage != null && !entityPackage.isEmpty())
      {
         javaClass.setPackage(entityPackage);
      }
      return newEntity(javaClass, idStrategy, tableName);
   }

   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public PersistenceUnitCommon getExistingPersistenceUnit(Project project, String unitName)
   {
      if (project != null && project.hasFacet(JPAFacet.class))
      {
         JPAFacet<?> facet = project.getFacet(JPAFacet.class);
         PersistenceCommonDescriptor config = facet.getConfig();
         List<PersistenceUnitCommon> allPersistenceUnit = config.getAllPersistenceUnit();
         for (PersistenceUnitCommon persistenceUnit : allPersistenceUnit)
         {
            if (unitName.equals(persistenceUnit.getName()))
            {
               return persistenceUnit;
            }
         }
      }
      return null;
   }

   private JavaResource getJavaResource(final DirectoryResource sourceDir, final String relativePath)
   {
      String path = relativePath.trim().endsWith(".java")
               ? relativePath.substring(0, relativePath.lastIndexOf(".java")) : relativePath;
      path = path.replace(".", File.separator) + ".java";
      JavaResource target = sourceDir.getChildOfType(JavaResource.class, path);
      return target;
   }

   @Override
   public List<JavaResource> getProjectEntities(Project project)
   {
      final List<JavaResource> entities = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, JavaResource resource)
            {
               try
               {
                  JavaSource<?> javaSource = resource.getJavaType();
                  if (javaSource.hasAnnotation(Entity.class) || javaSource.hasAnnotation(Embeddable.class)
                           || javaSource.hasAnnotation(MappedSuperclass.class))
                  {
                     entities.add(resource);
                  }
               }
               catch (ResourceException | FileNotFoundException e)
               {
                  // ignore
               }
            }
         });
      }
      return entities;
   }
}
