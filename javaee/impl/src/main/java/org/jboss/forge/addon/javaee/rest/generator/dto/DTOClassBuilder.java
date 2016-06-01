/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.generator.dto;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.templates.Template;
import org.jboss.forge.addon.templates.TemplateFactory;
import org.jboss.forge.addon.templates.freemarker.FreemarkerTemplate;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Property;
import org.jboss.forge.roaster.model.Type;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.util.Refactory;
import org.jboss.forge.roaster.model.util.Strings;
import org.jboss.forge.roaster.model.util.Types;

/**
 * A helper class to aid in creation of DTOs.
 */
public class DTOClassBuilder
{
   private static final Map<String, String> primitiveToWrapperTypeMap = new HashMap<>(8);

   private JavaClassSource dto;
   private final boolean topLevel;
   private String dtoClassName;
   private boolean isEmbeddedType;
   private final JavaClass<?> entity;
   private final StringBuilder copyCtorBuilder;
   private final StringBuilder assembleJPABuilder;
   private MethodSource<JavaClassSource> assembleJPA;
   private MethodSource<JavaClassSource> copyCtor;
   private final Property<?> idProperty;
   private final Template initializeJPAEntityFromId;
   private final Template initializeJPAEntityFromPrimitiveId;
   private final Template assembleCollection;
   private final Template assembleCollectionWithPrimitiveId;
   private final Template initializeNestedDTOCollection;

   static
   {
      primitiveToWrapperTypeMap.put("boolean", "Boolean");
      primitiveToWrapperTypeMap.put("byte", "Byte");
      primitiveToWrapperTypeMap.put("short", "Short");
      primitiveToWrapperTypeMap.put("int", "Integer");
      primitiveToWrapperTypeMap.put("long", "Long");
      primitiveToWrapperTypeMap.put("float", "Float");
      primitiveToWrapperTypeMap.put("double", "Double");
      primitiveToWrapperTypeMap.put("char", "Character");
   }

   public DTOClassBuilder(JavaClass<?> entity, Property<?> idProperty, boolean topLevel,
            TemplateFactory templateFactory, ResourceFactory resourceFactory)
   {
      this.entity = entity;
      this.idProperty = idProperty;
      this.topLevel = topLevel;
      this.copyCtorBuilder = new StringBuilder();
      this.assembleJPABuilder = new StringBuilder();
      this.initializeJPAEntityFromId = templateFactory.create(
               resourceFactory.create(getClass().getResource("InitializeJPAEntityFromId.jv")),
               FreemarkerTemplate.class);
      this.initializeJPAEntityFromPrimitiveId = templateFactory.create(
               resourceFactory.create(getClass().getResource("InitializeJPAEntityFromPrimitiveId.jv")),
               FreemarkerTemplate.class);
      this.assembleCollection = templateFactory.create(
               resourceFactory.create(getClass().getResource("AssembleCollection.jv")), FreemarkerTemplate.class);
      this.assembleCollectionWithPrimitiveId = templateFactory.create(
               resourceFactory.create(getClass().getResource("AssembleCollectionWithPrimitiveId.jv")),
               FreemarkerTemplate.class);

      this.initializeNestedDTOCollection = templateFactory.create(
               resourceFactory.create(getClass().getResource("InitializeNestedDTOCollection.jv")),
               FreemarkerTemplate.class);

      initName();
      initClassStructure();
      initializeJPAEntityInAssembler();
   }

   public DTOClassBuilder setPackage(String dtoPackage)
   {
      dto.setPackage(dtoPackage);
      return this;
   }

   public DTOClassBuilder setEmbeddedType(boolean isEmbeddedType)
   {
      this.isEmbeddedType = isEmbeddedType;
      return this;
   }

   public DTOClassBuilder updateForCollectionProperty(Property<?> property, JavaClassSource nestedDTOClass,
            Type<?> parameterizedType, Property<?> nestedDTOId)
   {
      // Create a collection field referencing the DTO
      addCollectionProperty(property, nestedDTOClass);

      // Add an expression in the ctor to extract the collection
      addInitializerFromCollection(property, nestedDTOClass, parameterizedType);
      addCollectionAssembler(property, parameterizedType, nestedDTOClass, nestedDTOId);
      return this;
   }

   public DTOClassBuilder updateForReferencedProperty(Property<?> property, JavaClassSource nestedDTOClass)
   {
      // Create a field referencing the DTO
      addProperty(property, nestedDTOClass);

      // Add an expression in the ctor to extract the field
      addInitializerFromDTO(property, nestedDTOClass);
      if (property.isMutable())
      {
         addAssemblerForReference(property);
      }
      return this;
   }

   public DTOClassBuilder updateForSimpleProperty(Property<?> property, Type<?> type)
   {
      // Create a field referencing the type
      addProperty(property, property.getType());

      // Add an expression in the ctor to extract the field
      addInitializerFromProperty(property);
      if (!property.equals(idProperty) && property.isMutable())
      {
         addPropertyAssembler(property);
      }
      return this;
   }

   public JavaClassSource createDTO()
   {
      if (topLevel && !isEmbeddedType)
      {
         dto.addAnnotation(XmlRootElement.class);
      }

      // Copy constructor to assemble DTO from JPA entity
      generateCopyConstructorBody();
      // Assembler method to assemble JPA entity from DTO
      generateJPAAssemblerBody();

      return dto;
   }

   private void initName()
   {
      dtoClassName = (topLevel ? "" : "Nested") + entity.getName() + "DTO";
   }

   private void initClassStructure()
   {
      dto = Roaster.create(JavaClassSource.class)
               .setName(dtoClassName)
               .setPublic()
               .addInterface(Serializable.class);

      // Default constructor
      createDefaultConstructor();
      // Copy constructor to assemble DTO from JPA entity
      createCopyConstructor();
      // Assembler method to assemble JPA entity from DTO
      createJPAAssembler();
   }

   private void initializeJPAEntityInAssembler()
   {
      if (!topLevel)
      {
         dto.addImport(TypedQuery.class);

         Map<Object, Object> map = new HashMap<>();
         map.put("id", idProperty.getName());
         map.put("entityName", entity.getName());
         map.put("jpqlVar", entity.getName().toLowerCase().substring(0, 1));
         String output;
         try
         {
            if (idProperty.getType().isPrimitive())
            {
               String wrapperType = primitiveToWrapperTypeMap.get(idProperty.getType().getName());
               map.put("wrapperType", wrapperType);
               output = initializeJPAEntityFromPrimitiveId.process(map);
            }
            else
            {
               output = initializeJPAEntityFromId.process(map);
            }
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         assembleJPABuilder.append(output);
      }
   }

   private void createJPAAssembler()
   {
      assembleJPA = dto.addMethod()
               .setName("fromDTO")
               .setReturnType(entity.getName())
               .setPublic()
               .setParameters(entity.getName() + " entity, EntityManager em");

      assembleJPABuilder.append("if(entity == null) { entity = new " + entity.getName() + "(); }");
   }

   private void generateJPAAssemblerBody()
   {
      if (!isEmbeddedType)
      {
         assembleJPABuilder.append("entity = em.merge(entity);");
      }
      assembleJPABuilder.append("return entity;");
      assembleJPA.setBody(assembleJPABuilder.toString());
   }

   private void createCopyConstructor()
   {
      dto.addImport(entity.getQualifiedName());
      dto.addImport(EntityManager.class);
      copyCtor = dto.addMethod()
               .setConstructor(true)
               .setPublic()
               .setParameters("final " + entity.getName() + " entity");
   }

   private void generateCopyConstructorBody()
   {
      copyCtor.setBody("if (entity != null) {\n" + copyCtorBuilder.toString() + "\n}");
   }

   private void createDefaultConstructor()
   {
      MethodSource<JavaClassSource> ctor = dto.addMethod();
      ctor.setConstructor(true);
      ctor.setPublic();
      ctor.setBody("");
   }

   private void addCollectionProperty(Property<?> field, JavaClassSource nestedDTOClass)
   {
      String concreteCollectionType = null;
      String qualifiedConcreteCollectionType = null;
      String jpaCollectionType = field.getType().getName();
      String nestedDTOType = nestedDTOClass.getName();
      String qualifiedDTOType = nestedDTOClass.getQualifiedName();
      if (jpaCollectionType.equals("Set"))
      {
         concreteCollectionType = "HashSet";
         qualifiedConcreteCollectionType = "java.util.HashSet";
      }
      else if (jpaCollectionType.equals("List"))
      {
         concreteCollectionType = "ArrayList";
         qualifiedConcreteCollectionType = "java.util.ArrayList";
      }
      else if (jpaCollectionType.equals("Map"))
      {
         concreteCollectionType = "HashMap";
         qualifiedConcreteCollectionType = "java.util.HashMap";
      }

      FieldSource<JavaClassSource> dtoField = dto.addField("private " + jpaCollectionType + "<" + nestedDTOType + "> "
               + field.getName() + "= new " + concreteCollectionType + "<" + nestedDTOType + ">();");
      dto.addImport(field.getType().getQualifiedName());
      dto.addImport(qualifiedConcreteCollectionType);
      if (!Types.isJavaLang(qualifiedDTOType))
      {
         dto.addImport(qualifiedDTOType);
      }
      Refactory.createGetterAndSetter(dto, dtoField);
   }

   private void addProperty(Property<?> field, Type<?> dtoFieldType)
   {
      String simpleName = dtoFieldType.getName();
      String qualifiedName = dtoFieldType.getQualifiedName();
      FieldSource<JavaClassSource> dtoField = dto.addField("private " + simpleName + " " + field.getName() + ";");
      if (!(field.getType().isPrimitive() || Types.isJavaLang(qualifiedName) || Types.isArray(qualifiedName)))
      {
         dto.addImport(qualifiedName);
      }
      if (Types.isArray(qualifiedName))
      {
         String arrayType = field.getType().getQualifiedName();
         if (!(Types.isJavaLang(arrayType) || Types.isPrimitive(arrayType)))
         {
            dto.addImport(arrayType);
         }
      }
      Refactory.createGetterAndSetter(dto, dtoField);
   }

   private void addProperty(Property<?> property, JavaClassSource dtoFieldType)
   {
      String simpleName = dtoFieldType.getName();
      String qualifiedName = dtoFieldType.getQualifiedName();
      FieldSource<JavaClassSource> dtoField = dto.addField("private " + simpleName + " " + property.getName() + ";");
      if (!(property.getType().isPrimitive() || Types.isJavaLang(qualifiedName) || Types.isArray(qualifiedName)))
      {
         dto.addImport(qualifiedName);
      }
      if (Types.isArray(qualifiedName))
      {
         String arrayType = property.getType().getQualifiedName();
         if (!(Types.isJavaLang(arrayType) || Types.isPrimitive(arrayType)))
         {
            dto.addImport(arrayType);
         }
      }
      Refactory.createGetterAndSetter(dto, dtoField);
   }

   private void addCollectionAssembler(Property<?> property, Type<?> parameterizedType,
            JavaClassSource nestedDTOClass, Property<?> nestedDtoId)
   {
      String fieldName = property.getName();
      String simpleParameterizedType = parameterizedType.getName();

      Map<Object, Object> map = new HashMap<>();
      map.put("reverseIdGetter", nestedDtoId.getAccessor().getName() + "()");
      map.put("fieldName", fieldName);
      map.put("fieldGetter", property.getAccessor().getName() + "()");
      map.put("nestedDTOType", nestedDTOClass.getName());
      map.put("jpaIterator", "iter" + Strings.capitalize(fieldName));
      map.put("simpleParameterizedType", simpleParameterizedType);
      map.put("jpaVar", Strings.uncapitalize(simpleParameterizedType));
      map.put("dtoIterator", "iterDto" + Strings.capitalize(fieldName));
      map.put("dtoVar", "dto" + Strings.capitalize(simpleParameterizedType));
      map.put("jpqlVar", simpleParameterizedType.toLowerCase().substring(0, 1));

      String output;
      try
      {
         if (nestedDtoId.getType().isPrimitive())
         {
            String wrapperType = primitiveToWrapperTypeMap.get(nestedDtoId.getType().getName());
            map.put("wrapperType", wrapperType);
            output = assembleCollectionWithPrimitiveId.process(map);
         }
         else
         {
            output = assembleCollection.process(map);
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      assembleJPABuilder.append(output);
   }

   private void addAssemblerForReference(Property<?> property)
   {
      String fieldName = property.getName();
      String fieldSetter = property.getMutator().getName();
      String fieldGetter = property.getAccessor().getName();
      assembleJPABuilder.append("if(this." + fieldName + " != null) {");
      assembleJPABuilder.append("entity." + fieldSetter + "(this." + fieldName + ".fromDTO(entity." + fieldGetter
               + "(), em));");
      assembleJPABuilder.append("}");
   }

   private void addPropertyAssembler(Property<?> property)
   {
      String fieldName = property.getName();
      String fieldSetter = property.getMutator().getName();
      assembleJPABuilder.append("entity." + fieldSetter + "(this." + fieldName + ");");
   }

   private void addInitializerFromCollection(Property<?> property, JavaClassSource nestedDTOClass,
            Type<?> parameterizedType)
   {
      dto.addImport(parameterizedType.getQualifiedName());
      dto.addImport(Iterator.class);
      Map<Object, Object> map = new HashMap<>();
      map.put("fieldName", property.getName());
      map.put("nestedDTOType", nestedDTOClass.getName());
      map.put("collectionIterator", "iter" + Strings.capitalize(property.getName()));
      map.put("elementType", parameterizedType.getName());
      map.put("fieldGetter", property.getAccessor().getName() + "()");
      String output;
      try
      {
         output = initializeNestedDTOCollection.process(map);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      copyCtorBuilder.append(output);
   }

   private void addInitializerFromDTO(Property<?> property, JavaClassSource dtoClass)
   {
      String fieldName = property.getName();
      String fieldGetter = property.getAccessor().getName();
      String dtoType = dtoClass.getName();
      copyCtorBuilder.append("this." + fieldName + " = " + "new " + dtoType + "(entity." + fieldGetter + "());");
   }

   private void addInitializerFromProperty(Property<?> property)
   {
      String fieldName = property.getName();
      String fieldGetter = property.getAccessor().getName();
      copyCtorBuilder.append("this." + fieldName + " = " + "entity." + fieldGetter + "();");
   }

}
