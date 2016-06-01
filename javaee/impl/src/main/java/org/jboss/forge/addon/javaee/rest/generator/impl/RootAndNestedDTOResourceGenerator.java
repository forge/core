/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generator.impl;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.jboss.forge.addon.javaee.jpa.JPAEntityUtil;
import org.jboss.forge.addon.javaee.rest.generation.RestGenerationConstants;
import org.jboss.forge.addon.javaee.rest.generation.RestGenerationContext;
import org.jboss.forge.addon.javaee.rest.generation.RestResourceGenerator;
import org.jboss.forge.addon.javaee.rest.generator.ResourceGeneratorUtil;
import org.jboss.forge.addon.javaee.rest.generator.dto.DTOClassBuilder;
import org.jboss.forge.addon.javaee.rest.generator.dto.DTOCollection;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.templates.Template;
import org.jboss.forge.addon.templates.TemplateFactory;
import org.jboss.forge.addon.templates.freemarker.FreemarkerTemplate;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Method;
import org.jboss.forge.roaster.model.Property;
import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.util.Types;

/**
 * A JAX-RS resource generator that creates root and nested DTOs for JPA entities, and references these DTOs in the
 * created REST resources.
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RootAndNestedDTOResourceGenerator implements RestResourceGenerator
{
   @Inject
   TemplateFactory templateFactory;

   @Inject
   ResourceFactory resourceFactory;

   @Override
   public List<JavaClassSource> generateFrom(RestGenerationContext context) throws Exception
   {
      List<JavaClassSource> result = new ArrayList<>();
      JavaClassSource entity = context.getEntity();

      Project project = context.getProject();
      String contentType = ResourceGeneratorUtil.getContentType(context.getContentType());
      String idType = JPAEntityUtil.resolveIdType(entity);
      String persistenceUnitName = context.getPersistenceUnitName();
      String idGetterName = JPAEntityUtil.resolveIdGetterName(entity);
      String entityTable = JPAEntityUtil.getEntityTable(entity);
      String selectExpression = JPAEntityUtil.getSelectExpression(entity, entityTable);
      String idClause = JPAEntityUtil.getIdClause(entity, entityTable);
      String orderClause = JPAEntityUtil.getOrderClause(entity,
               JPAEntityUtil.getJpqlEntityVariable(entityTable));
      String resourcePath = ResourceGeneratorUtil.getResourcePath(context);

      DTOCollection createdDtos = from(project, entity, context.getTargetPackageName() + ".dto");
      JavaClassSource rootDto = createdDtos.getDTOFor(entity, true);

      Map<Object, Object> map = new HashMap<>();
      map.put("entity", entity);
      map.put("dto", rootDto);
      map.put("idType", idType);
      map.put("getIdStatement", idGetterName);
      map.put("contentType", contentType);
      map.put("persistenceUnitName", persistenceUnitName);
      map.put("entityTable", entityTable);
      map.put("selectExpression", selectExpression);
      map.put("idClause", idClause);
      map.put("orderClause", orderClause);
      map.put("resourcePath", resourcePath);
      map.put("idIsPrimitive", Types.isPrimitive(idType));

      Resource<URL> templateResource = resourceFactory.create(getClass().getResource("EndpointWithDTO.jv"));
      Template processor = templateFactory.create(templateResource, FreemarkerTemplate.class);
      String output = processor.process(map);
      JavaClassSource resource = Roaster.parse(JavaClassSource.class, output);
      resource.addImport(rootDto.getQualifiedName());
      resource.addImport(entity.getQualifiedName());
      resource.setPackage(context.getTargetPackageName());
      result.add(resource);
      result.addAll(createdDtos.allResources());

      return result;
   }

   /**
    * Creates a collection of DTOs for the provided JPA entity, and any JPA entities referenced in the JPA entity.
    * 
    * @param entity The JPA entity for which DTOs are to be generated
    * @param dtoPackage The Java package in which the DTOs are to be created
    * @return The {@link DTOCollection} containing the DTOs created for the JPA entity.
    */
   public DTOCollection from(Project project, JavaClass<?> entity, String dtoPackage)
   {
      DTOCollection dtoCollection = new DTOCollection();
      if (entity == null)
      {
         throw new IllegalArgumentException("The argument entity was null.");
      }
      generatedDTOGraphForEntity(project, entity, dtoPackage, true, false, dtoCollection);
      return dtoCollection;
   }

   private JavaClassSource generatedDTOGraphForEntity(Project project, JavaClass<?> entity, String dtoPackage,
            boolean topLevel,
            boolean isEmbeddedType, DTOCollection dtoCollection)
   {
      if (dtoCollection.containsDTOFor(entity, topLevel))
      {
         return dtoCollection.getDTOFor(entity, topLevel);
      }

      Property<?> idProperty = parseIdPropertyForJPAEntity(entity);

      DTOClassBuilder dtoClassBuilder = new DTOClassBuilder(entity, idProperty, topLevel, templateFactory,
               resourceFactory)
               .setPackage(dtoPackage)
               .setEmbeddedType(isEmbeddedType);

      for (Property<?> property : entity.getProperties())
      {
         Field<?> field = property.getField();
         Method<?, ?> accessor = property.getAccessor();
         if (field != null)
         {
            if (field.isTransient() || field.hasAnnotation(Transient.class))
            {
               // No known reason for transient fields to be present in DTOs.
               // Revisit this if necessary for @Transient
               continue;
            }
         }
         else
         {
            if (accessor.hasAnnotation(Transient.class))
            {
               // No known reason for transient fields to be present in DTOs.
               // Revisit this if necessary for @Transient
               continue;
            }
         }

         String qualifiedPropertyType = property.getType().getQualifiedName();
         // Get the JavaClass for the field's type so that we can inspect it later for annotations and such
         // and recursively generate a DTO for it as well.
         JavaClass<?> propertyClass = tryGetJavaClass(project, qualifiedPropertyType);

         boolean isReadable = property.isAccessible();
         boolean isCollection = property.hasAnnotation(OneToMany.class) || property.hasAnnotation(ManyToMany.class);
         Type<?> propertyTypeInspector = property.getType();
         boolean parameterized = propertyTypeInspector.isParameterized();
         boolean hasAssociation = property.hasAnnotation(OneToOne.class) || property.hasAnnotation(ManyToOne.class);
         boolean isEmbedded = property.hasAnnotation(Embedded.class)
                  || (propertyClass != null && propertyClass.hasAnnotation(Embeddable.class));

         if (!isReadable)
         {
            // Skip the field if it lacks a getter. It is obviously not permitted to be read by other classes
            continue;
         }

         if (isCollection && parameterized)
         {
            if (!topLevel)
            {
               // Do not expand collections beyond the root
               continue;
            }

            // Create a DTO having the PK-field of the parameterized type of multi-valued collections,
            // if it does not exist
            Type<?> type = propertyTypeInspector.getTypeArguments().get(0);
            String qualifiedParameterizedType = type.getQualifiedName();
            JavaClass<?> parameterizedClass = tryGetJavaClass(project, qualifiedParameterizedType);
            if (parameterizedClass == null)
            {
               // ShellMessages.warn(writer, "Omitting creation of fields and DTO for type " +
               // qualifiedParameterizedType
               // + " due to missing source.");
               continue;
            }

            JavaClassSource nestedDTOClass = generatedDTOGraphForEntity(project, parameterizedClass, dtoPackage, false,
                     false, dtoCollection);
            // Then update the DTO for the collection field
            Property<?> nestedDtoId = parseIdPropertyForJPAEntity(parameterizedClass);
            dtoClassBuilder.updateForCollectionProperty(property, nestedDTOClass, type, nestedDtoId);
         }
         else if (hasAssociation)
         {
            if (!topLevel)
            {
               // Do not expand associations beyond the root
               continue;
            }

            // Create another DTO having the PK-field of the type of single-valued associations,
            // if it does not exist
            JavaClass<?> associatedClass = tryGetJavaClass(project, qualifiedPropertyType);
            if (associatedClass == null)
            {
               // ShellMessages.warn(writer, "Omitting creation of fields and DTO for type " + qualifiedPropertyType
               // + " due to missing source.");
               continue;
            }

            JavaClassSource nestedDTOClass = generatedDTOGraphForEntity(project, associatedClass, dtoPackage, false,
                     false,
                     dtoCollection);
            dtoClassBuilder.updateForReferencedProperty(property, nestedDTOClass);
         }
         else if (isEmbedded)
         {
            // Create another DTO for the @Embedded type, if it does not exist
            JavaClassSource dtoForEmbeddedType = generatedDTOGraphForEntity(project, propertyClass, dtoPackage, true,
                     true,
                     dtoCollection);
            dtoClassBuilder.updateForReferencedProperty(property, dtoForEmbeddedType);
         }
         else
         {
            dtoClassBuilder.updateForSimpleProperty(property, property.getType());
         }
      }

      JavaClassSource dtoClass = dtoClassBuilder.createDTO();
      if (topLevel)
      {
         dtoCollection.addRootDTO(entity, dtoClass);
      }
      else
      {
         dtoCollection.addNestedDTO(entity, dtoClass);
      }
      return dtoClass;
   }

   private Property<?> parseIdPropertyForJPAEntity(JavaClass<?> bean)
   {
      for (Property<?> property : bean.getProperties())
      {
         Field<?> field = property.getField();
         if (field != null && (field.hasAnnotation(Id.class) || field.hasAnnotation(EmbeddedId.class)))
         {
            return property;
         }
         Method<?, ?> accessor = property.getAccessor();
         if (accessor != null && (accessor.hasAnnotation(Id.class) || accessor.hasAnnotation(EmbeddedId.class)))
         {
            return property;
         }
      }
      return null;
   }

   private JavaClass<?> tryGetJavaClass(Project project, String qualifiedFieldType)
   {
      try
      {
         JavaResource javaResource = project.getFacet(JavaSourceFacet.class).getJavaResource(qualifiedFieldType);
         JavaClass<?> javaClass = javaResource.getJavaType();
         return javaClass;
      }
      catch (ClassCastException fileEx)
      {
         // Ignore, since the source file may not be a JavaClass
      }
      catch (FileNotFoundException fileEx)
      {
         // Ignore, since the source file may not be available
      }
      catch (ResourceException resourceEx)
      {
         // Ignore, since the source file may not be available
      }
      return null;
   }

   @Override
   public String getName()
   {
      return RestGenerationConstants.ROOT_AND_NESTED_DTO;
   }

   @Override
   public String getDescription()
   {
      return "Expose DTOs for JPA entities in the REST resources";
   }

}
