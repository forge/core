/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.jboss.forge.addon.javaee.rest.generation.RestGenerationContext;
import org.jboss.forge.addon.javaee.rest.generation.RestResourceGenerator;
import org.jboss.forge.addon.javaee.rest.generator.ResourceGeneratorUtil;
import org.jboss.forge.addon.javaee.rest.generator.dto.DTOClassBuilder;
import org.jboss.forge.addon.javaee.rest.generator.dto.DTOCollection;
import org.jboss.forge.addon.parser.java.beans.JavaClassIntrospector;
import org.jboss.forge.addon.parser.java.beans.Property;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.templates.TemplateProcessor;
import org.jboss.forge.addon.templates.TemplateProcessorFactory;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Type;

/**
 * A JAX-RS resource generator that creates root and nested DTOs for JPA entities, and references these DTOs in the
 * created REST resources.
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RootAndNestedDTOResourceGenerator implements RestResourceGenerator
{
   @Inject
   TemplateProcessorFactory processorFactory;

   @Inject
   ResourceFactory resourceFactory;

   @Override
   public List<JavaClass> generateFrom(RestGenerationContext context) throws Exception
   {
      List<JavaClass> result = new ArrayList<JavaClass>();
      JavaClass entity = context.getEntity();

      Project project = context.getProject();
      String contentType = context.getContentType();
      String idType = ResourceGeneratorUtil.resolveIdType(entity);
      String persistenceUnitName = context.getPersistenceUnitName();
      String idGetterName = ResourceGeneratorUtil.resolveIdGetterName(entity);
      String entityTable = ResourceGeneratorUtil.getEntityTable(entity);
      String selectExpression = ResourceGeneratorUtil.getSelectExpression(entity, entityTable);
      String idClause = ResourceGeneratorUtil.getIdClause(entity, entityTable);
      String orderClause = ResourceGeneratorUtil.getOrderClause(entity,
               ResourceGeneratorUtil.getJpqlEntityVariable(entityTable));
      String resourcePath = ResourceGeneratorUtil.getResourcePath(context);

      DTOCollection createdDtos = from(project, entity, context.getTargetPackageName() + ".dto");
      JavaClass rootDto = createdDtos.getDTOFor(entity, true);

      Map<Object, Object> map = new HashMap<Object, Object>();
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

      Resource<URL> templateResource = resourceFactory.create(getClass().getResource("EndpointWithDTO.jv"));
      TemplateProcessor processor = processorFactory.createProcessorFor(templateResource);
      String output = processor.process(map);
      JavaClass resource = JavaParser.parse(JavaClass.class, output);
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
   public DTOCollection from(Project project, JavaClass entity, String dtoPackage)
   {
      DTOCollection dtoCollection = new DTOCollection();
      if (entity == null)
      {
         throw new IllegalArgumentException("The argument entity was null.");
      }
      generatedDTOGraphForEntity(project, entity, dtoPackage, true, false, dtoCollection);
      return dtoCollection;
   }

   private JavaClass generatedDTOGraphForEntity(Project project, JavaClass entity, String dtoPackage, boolean topLevel,
            boolean isEmbeddedType, DTOCollection dtoCollection)
   {
      if (dtoCollection.containsDTOFor(entity, topLevel))
      {
         return dtoCollection.getDTOFor(entity, topLevel);
      }

      Property idProperty = null;
      JavaClassIntrospector bean = new JavaClassIntrospector(entity);
      idProperty = parseIdPropertyForJPAEntity(bean);

      DTOClassBuilder dtoClassBuilder = new DTOClassBuilder(entity, idProperty, topLevel, processorFactory,
               resourceFactory)
               .setPackage(dtoPackage)
               .setEmbeddedType(isEmbeddedType);

      for (Property property : bean.getProperties())
      {
         if (property.isTransient() || property.hasAnnotation(Transient.class))
         {
            // No known reason for transient fields to be present in DTOs.
            // Revisit this if necessary for @Transient
            continue;
         }

         String qualifiedPropertyType = property.getQualifiedType();
         // Get the JavaClass for the field's type so that we can inspect it later for annotations and such
         // and recursively generate a DTO for it as well.
         JavaClass propertyClass = tryGetJavaClass(project, qualifiedPropertyType);

         boolean isReadable = property.isReadable();
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
            JavaClass parameterizedClass = tryGetJavaClass(project, qualifiedParameterizedType);
            if (parameterizedClass == null)
            {
               // ShellMessages.warn(writer, "Omitting creation of fields and DTO for type " +
               // qualifiedParameterizedType
               // + " due to missing source.");
               continue;
            }

            JavaClass nestedDTOClass = generatedDTOGraphForEntity(project, parameterizedClass, dtoPackage, false,
                     false, dtoCollection);
            // Then update the DTO for the collection field
            JavaClassIntrospector parameterizedClassBean = new JavaClassIntrospector(parameterizedClass);
            Property nestedDtoId = parseIdPropertyForJPAEntity(parameterizedClassBean);
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
            JavaClass associatedClass = tryGetJavaClass(project, qualifiedPropertyType);
            if (associatedClass == null)
            {
               // ShellMessages.warn(writer, "Omitting creation of fields and DTO for type " + qualifiedPropertyType
               // + " due to missing source.");
               continue;
            }

            JavaClass nestedDTOClass = generatedDTOGraphForEntity(project, associatedClass, dtoPackage, false, false,
                     dtoCollection);
            dtoClassBuilder.updateForReferencedProperty(property, nestedDTOClass);
         }
         else if (isEmbedded)
         {
            // Create another DTO for the @Embedded type, if it does not exist
            JavaClass dtoForEmbeddedType = generatedDTOGraphForEntity(project, propertyClass, dtoPackage, true, true,
                     dtoCollection);
            dtoClassBuilder.updateForReferencedProperty(property, dtoForEmbeddedType);
         }
         else
         {
            dtoClassBuilder.updateForSimpleProperty(property, property.getType());
         }
      }

      JavaClass dtoClass = dtoClassBuilder.createDTO();
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

   private Property parseIdPropertyForJPAEntity(JavaClassIntrospector bean)
   {
      for (Property property : bean.getProperties())
      {
         if (property.hasAnnotation(Id.class))
         {
            return property;
         }
      }
      return null;
   }

   private JavaClass tryGetJavaClass(Project project, String qualifiedFieldType)
   {
      try
      {
         JavaResource javaResource = project.getFacet(JavaSourceFacet.class).getJavaResource(qualifiedFieldType);
         JavaSource<?> javaSource = javaResource.getJavaSource();
         if (javaSource instanceof JavaClass)
         {
            return (JavaClass) javaSource;
         }
      }
      catch (FileNotFoundException fileEx)
      {
         // Ignore, since the source file may not be available
      }
      return null;
   }

   @Override
   public String getName()
   {
      return "ROOT_AND_NESTED_DTO";
   }

   @Override
   public String getDescription()
   {
      return "Expose DTOs for JPA entities in the REST resources";
   }

}
