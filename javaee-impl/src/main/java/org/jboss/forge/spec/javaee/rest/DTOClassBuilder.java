package org.jboss.forge.spec.javaee.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.java.util.Types;
import org.jboss.forge.spec.javaee.util.FreemarkerTemplateProcessor;
import org.jboss.forge.spec.javaee.util.JPAProperty;

public class DTOClassBuilder
{
   private JavaClass dto;
   private boolean topLevel = false;
   private String dtoClassName;
   private boolean isEmbeddedType;
   private JavaClass entity;
   private StringBuilder copyCtorBuilder;
   private StringBuilder assembleJPABuilder;
   private Method<JavaClass> assembleJPA;
   private Method<JavaClass> copyCtor;
   private JPAProperty idProperty;
   private FreemarkerTemplateProcessor processor;

   public DTOClassBuilder(JavaClass entity, boolean topLevel)
   {
      this.entity = entity;
      this.topLevel = topLevel;
      this.copyCtorBuilder = new StringBuilder();
      this.assembleJPABuilder = new StringBuilder();
      this.processor = new FreemarkerTemplateProcessor();
      initName();
      initClassStructure();
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

   public DTOClassBuilder setIdProperty(JPAProperty idProperty)
   {
      this.idProperty = idProperty;
      initializeJPAEntityInAssembler();
      return this;
   }

   public DTOClassBuilder updateForCollectionProperty(JPAProperty property, JavaClass nestedDTOClass,
            Type<?> parameterizedType)
   {
      addCollectionProperty(property, nestedDTOClass);
      addInitializerFromCollection(property, nestedDTOClass, parameterizedType);
      addCollectionAssembler(property, parameterizedType, nestedDTOClass);
      return this;
   }

   public DTOClassBuilder updateForReferencedProperty(JPAProperty property, JavaClass nestedDTOClass)
   {
      // Then create a field referencing the DTO
      addProperty(property, nestedDTOClass);

      // Add an expression in the ctor to extract the fields
      addInitializerFromDTO(property, nestedDTOClass);
      if (property.isWritable())
      {
         addAssemblerForReference(property);
      }
      return this;
   }

   public DTOClassBuilder updateForSimpleProperty(JPAProperty property, Type<?> type)
   {
      addProperty(property, property.getType());
      addInitializerFromProperty(property);
      if (!property.equals(idProperty) && property.isWritable())
      {
         addPropertyAssembler(property);
      }
      return this;
   }

   public JavaClass createDTO()
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
      dto = JavaParser.create(JavaClass.class)
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

         Map<Object, Object> map = new HashMap<Object, Object>();
         map.put("id", idProperty.getName());
         map.put("entityName", entity.getName());
         map.put("jpqlVar", entity.getName().toLowerCase().substring(0, 1));
         String output = processor.processTemplate(map, "org/jboss/forge/rest/InitializeJPAEntityFromId.jv");
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
      Method<JavaClass> ctor = dto.addMethod();
      ctor.setConstructor(true);
      ctor.setPublic();
      ctor.setBody("");
   }

   private void addCollectionProperty(JPAProperty field, JavaClass nestedDTOClass)
   {
      String concreteCollectionType = null;
      String qualifiedConcreteCollectionType = null;
      String jpaCollectionType = field.getSimpleType();
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

      Field<JavaClass> dtoField = dto.addField("private " + jpaCollectionType + "<" + nestedDTOType + "> "
               + field.getName() + "= new " + concreteCollectionType + "<" + nestedDTOType + ">();");
      dto.addImport(field.getQualifiedType());
      dto.addImport(qualifiedConcreteCollectionType);
      if (!Types.isJavaLang(qualifiedDTOType))
      {
         dto.addImport(qualifiedDTOType);
      }
      Refactory.createGetterAndSetter(dto, dtoField);
   }

   private void addProperty(JPAProperty field, Type<?> dtoFieldType)
   {
      String simpleName = dtoFieldType.getName();
      String qualifiedName = dtoFieldType.getQualifiedName();
      Field<JavaClass> dtoField = dto.addField("private " + simpleName + " " + field.getName() + ";");
      if (!(field.isPrimitive() || Types.isJavaLang(qualifiedName) || Types.isArray(qualifiedName)))
      {
         dto.addImport(qualifiedName);
      }
      if (Types.isArray(qualifiedName))
      {
         String arrayType = field.getTypeInspector().getQualifiedName();
         if (!(Types.isJavaLang(arrayType) || Types.isPrimitive(arrayType)))
         {
            dto.addImport(arrayType);
         }
      }
      Refactory.createGetterAndSetter(dto, dtoField);
   }

   private void addProperty(JPAProperty property, JavaClass dtoFieldType)
   {
      String simpleName = dtoFieldType.getName();
      String qualifiedName = dtoFieldType.getQualifiedName();
      Field<JavaClass> dtoField = dto.addField("private " + simpleName + " " + property.getName() + ";");
      if (!(property.isPrimitive() || Types.isJavaLang(qualifiedName) || Types.isArray(qualifiedName)))
      {
         dto.addImport(qualifiedName);
      }
      if (Types.isArray(qualifiedName))
      {
         String arrayType = property.getTypeInspector().getQualifiedName();
         if (!(Types.isJavaLang(arrayType) || Types.isPrimitive(arrayType)))
         {
            dto.addImport(arrayType);
         }
      }
      Refactory.createGetterAndSetter(dto, dtoField);
   }

   private void addCollectionAssembler(JPAProperty property, Type<?> parameterizedType,
            JavaClass nestedDTOClass)
   {
      String fieldName = property.getName();
      String simpleParameterizedType = parameterizedType.getName();

      Map<Object, Object> map = new HashMap<Object, Object>();
      map.put("id", idProperty.getName());
      map.put("idGetter", idProperty.getAccessor().getName() + "()");
      map.put("fieldName", fieldName);
      map.put("fieldGetter", property.getAccessor().getName() + "()");
      map.put("nestedDTOType", nestedDTOClass.getName());
      map.put("jpaIterator", "iter" + Strings.capitalize(fieldName));
      map.put("simpleParameterizedType", simpleParameterizedType);
      map.put("jpaVar", Strings.uncapitalize(simpleParameterizedType));
      map.put("dtoIterator", "iterDto" + Strings.capitalize(fieldName));
      map.put("dtoVar", "dto" + Strings.capitalize(simpleParameterizedType));
      map.put("jpqlVar", simpleParameterizedType.toLowerCase().substring(0, 1));

      String output = processor.processTemplate(map, "org/jboss/forge/rest/AssembleCollection.jv");
      assembleJPABuilder.append(output);
   }

   private void addAssemblerForReference(JPAProperty property)
   {
      String fieldName = property.getName();
      String fieldSetter = property.getMutator().getName();
      String fieldGetter = property.getAccessor().getName();
      assembleJPABuilder.append("if(this." + fieldName + " != null) {");
      assembleJPABuilder.append("entity." + fieldSetter + "(this." + fieldName + ".fromDTO(entity." + fieldGetter
               + "(), em));");
      assembleJPABuilder.append("}");
   }

   private void addPropertyAssembler(JPAProperty property)
   {
      String fieldName = property.getName();
      String fieldSetter = property.getMutator().getName();
      assembleJPABuilder.append("entity." + fieldSetter + "(this." + fieldName + ");");
   }

   private void addInitializerFromCollection(JPAProperty property, JavaClass nestedDTOClass,
            Type<?> parameterizedType)
   {
      dto.addImport(parameterizedType.getQualifiedName());
      dto.addImport(Iterator.class);
      Map<Object, Object> map = new HashMap<Object, Object>();
      map.put("fieldName", property.getName());
      map.put("nestedDTOType", nestedDTOClass.getName());
      map.put("collectionIterator", "iter" + Strings.capitalize(property.getName()));
      map.put("elementType", parameterizedType.getName());
      map.put("fieldGetter", property.getAccessor().getName() + "()");
      String output = processor.processTemplate(map, "org/jboss/forge/rest/InitializeNestedDTOCollection.jv");
      copyCtorBuilder.append(output);
   }

   private void addInitializerFromDTO(JPAProperty property, JavaClass dtoClass)
   {
      String fieldName = property.getName();
      String fieldGetter = property.getAccessor().getName();
      String dtoType = dtoClass.getName();
      copyCtorBuilder.append("this." + fieldName + " = " + "new " + dtoType + "(entity." + fieldGetter + "());");
   }

   private void addInitializerFromProperty(JPAProperty property)
   {
      String fieldName = property.getName();
      String fieldGetter = property.getAccessor().getName();
      copyCtorBuilder.append("this." + fieldName + " = " + "entity." + fieldGetter + "();");
   }

}
