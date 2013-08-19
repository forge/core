package org.jboss.forge.spec.javaee.rest;

import java.io.Serializable;
import java.util.Iterator;

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

   public DTOClassBuilder(JavaClass entity, boolean topLevel)
   {
      this.entity = entity;
      this.topLevel = topLevel;
      this.copyCtorBuilder = new StringBuilder();
      this.assembleJPABuilder = new StringBuilder();
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

   public DTOClassBuilder addCollectionProperty(JPAProperty field, JavaClass nestedDTOClass)
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
      return this;
   }

   public DTOClassBuilder addProperty(JPAProperty field, Type<?> dtoFieldType)
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
      return this;
   }

   public DTOClassBuilder addProperty(JPAProperty property, JavaClass nestedDTOClass)
   {
      String simpleName = nestedDTOClass.getName();
      String qualifiedName = nestedDTOClass.getQualifiedName();
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
      return this;
   }

   public DTOClassBuilder addCollectionAssembler(JPAProperty property, Type<?> parameterizedType,
            JavaClass nestedDTOClass)
   {
      String id = idProperty.getName();
      String fieldName = property.getName();
      String nestedDTOType = nestedDTOClass.getName();
      String jpaIterator = "iter" + Strings.capitalize(fieldName);
      String simpleParameterizedType = parameterizedType.getName();
      String jpaVar = Strings.uncapitalize(simpleParameterizedType);
      String dtoIterator = "iterDto" + Strings.capitalize(fieldName);
      String dtoVar = "dto" + Strings.capitalize(simpleParameterizedType);
      String jpqlVar = simpleParameterizedType.toLowerCase().substring(0, 1);
   
      assembleJPABuilder.append("Iterator" + " " + jpaIterator + " = " + "entity.get"
               + Strings.capitalize(fieldName) + "().iterator();");
      assembleJPABuilder.append("for (; " + jpaIterator + ".hasNext() ;) {");
      assembleJPABuilder.append(" boolean found = false;");
      assembleJPABuilder.append(" " + simpleParameterizedType + " " + jpaVar + " = (" + simpleParameterizedType
               + ") " + jpaIterator + ".next();");
      assembleJPABuilder.append("Iterator" + " " + dtoIterator + " = " + "this.get"
               + Strings.capitalize(fieldName) + "().iterator();");
      assembleJPABuilder.append("for (; " + dtoIterator + ".hasNext() ;) {");
      assembleJPABuilder.append(" " + nestedDTOType + " " + dtoVar + " = (" + nestedDTOType + ") " + dtoIterator
               + ".next();");
      assembleJPABuilder.append("");
      assembleJPABuilder.append("if(" + dtoVar + ".get" + Strings.capitalize(id) + "().equals(" + jpaVar + ".get"
               + Strings.capitalize(id) + "())) { found = true; break; }");
      assembleJPABuilder.append("}");
      assembleJPABuilder.append("if(found == false) { ");
      assembleJPABuilder.append(jpaIterator + ".remove();");
      assembleJPABuilder.append("} }");
   
      assembleJPABuilder.append("Iterator" + " " + dtoIterator + " = " + "this.get"
               + Strings.capitalize(fieldName) + "().iterator();");
      assembleJPABuilder.append("for (; " + dtoIterator + ".hasNext() ;) {");
      assembleJPABuilder.append(" boolean found = false;");
      assembleJPABuilder.append(" " + nestedDTOType + " " + dtoVar + " = (" + nestedDTOType + ") " + dtoIterator
               + ".next();");
      assembleJPABuilder.append(jpaIterator + " = " + "entity.get"
               + Strings.capitalize(fieldName) + "().iterator();");
      assembleJPABuilder.append("for (; " + jpaIterator + ".hasNext() ;) {");
      assembleJPABuilder.append(" " + simpleParameterizedType + " " + jpaVar + " = (" + simpleParameterizedType
               + ") " + jpaIterator + ".next();");
      assembleJPABuilder.append("if(" + dtoVar + ".get" + Strings.capitalize(id) + "().equals(" + jpaVar + ".get"
               + Strings.capitalize(id) + "())) { found = true; break; }");
      assembleJPABuilder.append("}");
      assembleJPABuilder.append("if(found == false) { ");
      assembleJPABuilder.append("Iterator resultIter = em.createQuery(\"SELECT DISTINCT " + jpqlVar + " FROM "
               + simpleParameterizedType + " " + jpqlVar + "\", " + simpleParameterizedType
               + ".class).getResultList().iterator();");
      assembleJPABuilder.append("for(; resultIter.hasNext();) { ");
      assembleJPABuilder.append(simpleParameterizedType + " result = (" + simpleParameterizedType
               + ") resultIter.next();");
      assembleJPABuilder.append("if( result.get" + Strings.capitalize(id) + "().equals(" + dtoVar + ".get"
               + Strings.capitalize(id) + "())) {");
      assembleJPABuilder.append("entity.get" + Strings.capitalize(fieldName) + "().add(result);");
      assembleJPABuilder.append("break;");
      assembleJPABuilder.append("} }");
      assembleJPABuilder.append("} }");
      return this;
   }

   public DTOClassBuilder addAssociationAssembler(JPAProperty property)
   {
      String fieldName = property.getName();
      assembleJPABuilder.append("if(this." + fieldName + " != null) {");
      assembleJPABuilder.append("entity.set" + Strings.capitalize(fieldName) + "(this." + fieldName
               + ".fromDTO(entity.get" + Strings.capitalize(fieldName) + "(), em));");
      assembleJPABuilder.append("}");
      return this;
   }

   public DTOClassBuilder addPropertyAssembler(JPAProperty property)
   {
      String fieldName = property.getName();
      assembleJPABuilder.append("entity.set" + Strings.capitalize(fieldName) + "(this." + fieldName
               + ");");
      return this;
   }

   public DTOClassBuilder addEmbeddableAssembler(JPAProperty property)
   {
      String fieldName = property.getName();
      assembleJPABuilder.append("if(this." + fieldName + " != null) {");
      assembleJPABuilder.append("entity.set" + Strings.capitalize(fieldName) + "(this." + fieldName
               + ".fromDTO(entity.get" + Strings.capitalize(fieldName) + "(), em));");
      assembleJPABuilder.append("}");
      return this;
   }

   public DTOClassBuilder addInitializerFromCollection(JPAProperty property, JavaClass nestedDTOClass,
            String simpleParameterizedType, String qualifiedParameterizedType)
   {
      String fieldName = property.getName();
      String nestedDTOType = nestedDTOClass.getName();
      dto.addImport(qualifiedParameterizedType);
      dto.addImport(Iterator.class);
      String iterator = "iter" + Strings.capitalize(fieldName);
      copyCtorBuilder.append("Iterator" + " " + iterator + " = " + "entity.get" + Strings.capitalize(fieldName)
               + "().iterator();");
      copyCtorBuilder.append("for (; " + iterator + ".hasNext() ;) ");
      copyCtorBuilder.append("{");
      copyCtorBuilder.append("this." + fieldName + ".add(" + "new " + nestedDTOType + "((" + simpleParameterizedType
               + ")" + iterator + ".next()));");
      copyCtorBuilder.append("}");
      return this;
   }

   public DTOClassBuilder addInitializerFromDTO(JPAProperty property, JavaClass dtoClass)
   {
      String fieldName = property.getName();
      String dtoType = dtoClass.getName();
      copyCtorBuilder.append("this." + fieldName + " = " + "new " + dtoType + "(entity.get"
               + Strings.capitalize(fieldName) + "());");
      return this;
   }

   public DTOClassBuilder addInitializerFromProperty(JPAProperty property)
   {
      String fieldName = property.getName();
      copyCtorBuilder.append("this." + fieldName + " = " + "entity.get" + Strings.capitalize(fieldName) + "();");
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
      String id = idProperty.getName();
      String entityName = entity.getName();
   
      if (!topLevel)
      {
         String jpqlVar = entityName.toLowerCase().substring(0, 1);
         dto.addImport(TypedQuery.class);
         assembleJPABuilder.append("if(this." + id + " != null) {");
         assembleJPABuilder.append("TypedQuery findByIdQuery = em.createQuery(\"SELECT DISTINCT " + jpqlVar
                  + " FROM " + entityName + " " + jpqlVar + " WHERE " + jpqlVar + "." + id + " = :entityId"
                  + "\", " + entityName + ".class);");
         assembleJPABuilder.append("findByIdQuery.setParameter(\"entityId\", this." + id + ");");
         assembleJPABuilder.append("entity = (" + entityName + ") findByIdQuery.getSingleResult();");
         assembleJPABuilder.append("return entity; }");
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

}
