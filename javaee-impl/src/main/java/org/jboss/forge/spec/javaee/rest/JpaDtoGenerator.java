package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Iterator;

import javax.inject.Inject;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.java.util.Types;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.project.ProjectScoped;
import org.jboss.forge.shell.util.Packages;
import org.jboss.forge.spec.javaee.util.JPABeanIntrospector;
import org.jboss.forge.spec.javaee.util.JPAProperty;

public class JpaDtoGenerator
{

   @Inject
   @ProjectScoped
   private Project project;

   @Inject
   private ShellPrintWriter writer;

   @Inject
   private DTOCollection dtoCollection;

   public DTOCollection from(JavaClass entity, String dtoPackage) throws FileNotFoundException
   {
      if (entity == null)
      {
         throw new IllegalArgumentException("The argument entity was null.");
      }
      generatedDTOGraphForEntity(entity, dtoPackage, true, false);
      return dtoCollection;
   }

   public JavaClass generatedDTOGraphForEntity(JavaClass entity, String dtoPackage, boolean topLevel,
            boolean isEmbeddedType)
            throws FileNotFoundException
   {
      if (dtoCollection.containsDTOFor(entity, topLevel))
      {
         return dtoCollection.getDTOFor(entity, topLevel);
      }

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String dtoClassName = (topLevel ? "" : "Nested") + entity.getName() + "DTO";
      JavaClass dtoClass = JavaParser.create(JavaClass.class)
               .setPackage(dtoPackage)
               .setName(dtoClassName)
               .setPublic()
               .addInterface(Serializable.class);

      // Default constructor
      Method<JavaClass> ctor = dtoClass.addMethod();
      ctor.setConstructor(true);
      ctor.setPublic();
      ctor.setBody("");

      // Copy constructor to assemble DTO from JPA Entity
      dtoClass.addImport(entity.getQualifiedName());
      dtoClass.addImport(EntityManager.class);
      Method<JavaClass> copyCtor = dtoClass.addMethod();
      copyCtor.setConstructor(true);
      copyCtor.setPublic();
      copyCtor.setParameters("final " + entity.getName() + " entity");
      StringBuilder copyCtorBuilder = new StringBuilder();
      copyCtorBuilder.append("if(entity != null) {");

      // Copy method to assemble JPA Entity from DTO
      Method<JavaClass> assembleJPA = dtoClass.addMethod()
               .setName("fromDTO")
               .setReturnType(entity.getName())
               .setPublic()
               .setParameters(entity.getName() + " entity, EntityManager em");
      StringBuilder assembleJPABuilder = new StringBuilder();
      String id = null;
      JPAProperty idProperty = null;
      JPABeanIntrospector bean = new JPABeanIntrospector(entity);
      for (JPAProperty property : bean.getProperties())
      {
         if (property.hasAnnotation(Id.class))
         {
            idProperty = property;
            id = property.getName();

            String entityName = entity.getName();
            assembleJPABuilder.append("if(entity == null) { entity = new " + entityName + "(); }");
            if (!topLevel)
            {
               String jpqlVar = entityName.toLowerCase().substring(0, 1);
               dtoClass.addImport(TypedQuery.class);
               assembleJPABuilder.append("if(this." + id + " != null) {");
               assembleJPABuilder.append("TypedQuery findByIdQuery = em.createQuery(\"SELECT DISTINCT " + jpqlVar
                        + " FROM " + entityName + " " + jpqlVar + " WHERE " + jpqlVar + "." + id + " = :entityId"
                        + "\", " + entityName + ".class);");
               assembleJPABuilder.append("findByIdQuery.setParameter(\"entityId\", this." + id + ");");
               assembleJPABuilder.append("entity = (" + entityName + ") findByIdQuery.getSingleResult();");
               assembleJPABuilder.append("return entity; }");
            }
         }
      }

      for (JPAProperty property : bean.getProperties())
      {
         if (property.isTransient() || property.hasAnnotation(Transient.class))
         {
            // No known reason for transient and static fields to be present in DTOs.
            // Revisit this if necessary for @Transient
            continue;
         }

         String fieldName = property.getName();
         String fieldType = property.getType();
         String qualifiedFieldType = property.getQualifiedType();
         // Get the JavaClass for the field's type so that we can inspect it later for annotations and such
         // and recursively generate a DTO for it as well.
         JavaClass fieldClass = tryGetJavaClass(java, qualifiedFieldType);

         // JPABeanTest bean = new JPABean(entity);
         boolean isWritable = property.isWritable();
         boolean isReadable = property.isReadable();
         boolean isCollection = property.hasAnnotation(OneToMany.class) || property.hasAnnotation(ManyToMany.class);
         Type<?> fieldTypeInspector = property.getTypeInspector();
         boolean parameterized = fieldTypeInspector.isParameterized();
         boolean hasAssociation = property.hasAnnotation(OneToOne.class) || property.hasAnnotation(ManyToOne.class);
         boolean isEmbedded = property.hasAnnotation(Embedded.class)
                  || (fieldClass != null && fieldClass.hasAnnotation(Embeddable.class));

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
            Type<?> type = fieldTypeInspector.getTypeArguments().get(0);
            String simpleParameterizedType = type.getName();
            String qualifiedParameterizedType = type.getQualifiedName();
            JavaClass parameterizedClass = tryGetJavaClass(java, qualifiedParameterizedType);
            if (parameterizedClass == null)
            {
               ShellMessages.warn(writer, "Omitting creation of fields and DTO for type " + qualifiedParameterizedType
                        + " due to missing source.");
               continue;
            }

            JavaClass nestedDTOClass = generatedDTOGraphForEntity(parameterizedClass, dtoPackage, false, false);

            // Then create a collection field for the DTO
            String nestedDTOType = nestedDTOClass.getName();
            String qualifiedDTOType = nestedDTOClass.getQualifiedName();
            addCollectionProperty(dtoClass, property, nestedDTOType, qualifiedDTOType);

            // Add expressions in the ctor to extract the PK
            dtoClass.addImport(qualifiedParameterizedType);
            dtoClass.addImport(Iterator.class);
            String iterator = "iter" + Strings.capitalize(fieldName);
            copyCtorBuilder.append("Iterator" + " " + iterator + " = " + "entity.get"
                     + Strings.capitalize(fieldName) + "().iterator();");
            copyCtorBuilder.append("for (; " + iterator + ".hasNext() ;) ");
            copyCtorBuilder.append("{");
            copyCtorBuilder.append("this." + fieldName + ".add(" + "new " + nestedDTOType + "(("
                     + simpleParameterizedType
                     + ")" + iterator + ".next()));");
            copyCtorBuilder.append("}");

            String jpaIterator = "iter" + Strings.capitalize(fieldName);
            assembleJPABuilder.append("Iterator" + " " + jpaIterator + " = " + "entity.get"
                     + Strings.capitalize(fieldName) + "().iterator();");
            assembleJPABuilder.append("for (; " + jpaIterator + ".hasNext() ;) {");
            assembleJPABuilder.append(" boolean found = false;");
            String jpaVar = Strings.uncapitalize(simpleParameterizedType);
            assembleJPABuilder.append(" " + simpleParameterizedType + " " + jpaVar + " = (" + simpleParameterizedType
                     + ") " + jpaIterator + ".next();");
            String dtoIterator = "iterDto" + Strings.capitalize(fieldName);
            assembleJPABuilder.append("Iterator" + " " + dtoIterator + " = " + "this.get"
                     + Strings.capitalize(fieldName) + "().iterator();");
            assembleJPABuilder.append("for (; " + dtoIterator + ".hasNext() ;) {");
            String dtoVar = "dto" + Strings.capitalize(simpleParameterizedType);
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
            String jpqlVar = simpleParameterizedType.toLowerCase().substring(0, 1);
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
            JavaClass associatedClass = tryGetJavaClass(java, qualifiedFieldType);
            if (associatedClass == null)
            {
               ShellMessages.warn(writer, "Omitting creation of fields and DTO for type " + qualifiedFieldType
                        + " due to missing source.");
               continue;
            }

            JavaClass nestedDTOClass = generatedDTOGraphForEntity(associatedClass, dtoPackage, false, false);

            // Then create a field referencing the DTO
            String nestedType = nestedDTOClass.getName();
            String qualifiedDTOFieldType = nestedDTOClass.getQualifiedName();
            addProperty(dtoClass, property, nestedType, qualifiedDTOFieldType);

            // Add an expression in the ctor to extract the fields
            copyCtorBuilder.append("this." + fieldName + " = " + "new " + nestedType + "(entity.get"
                     + Strings.capitalize(fieldName) + "());");

            if (isWritable)
            {
               assembleJPABuilder.append("if(this." + fieldName + " != null) {");
               assembleJPABuilder.append("entity.set" + Strings.capitalize(fieldName) + "(this." + fieldName
                        + ".fromDTO(entity.get" + Strings.capitalize(fieldName) + "(), em));");
               assembleJPABuilder.append("}");
            }
         }
         else if (isEmbedded)
         {
            // Create another DTO for the @Embedded type, if it does not exist
            JavaClass dtoForEmbeddedType = generatedDTOGraphForEntity(fieldClass, dtoPackage, true, true);
            String embeddedType = dtoForEmbeddedType.getName();
            String qualifiedNameForEmbeddedType = dtoForEmbeddedType.getQualifiedName();
            addProperty(dtoClass, property, embeddedType, qualifiedNameForEmbeddedType);

            // Add an expression in the ctor to assign the embedded type
            copyCtorBuilder.append("this." + fieldName + " = " + "new " + embeddedType + "(entity.get"
                     + Strings.capitalize(fieldName) + "());");

            if (isWritable)
            {
               assembleJPABuilder.append("if(this." + fieldName + " != null) {");
               assembleJPABuilder.append("entity.set" + Strings.capitalize(fieldName) + "(this." + fieldName
                        + ".fromDTO(entity.get" + Strings.capitalize(fieldName) + "(), em));");
               assembleJPABuilder.append("}");
            }
         }
         else
         {
            addProperty(dtoClass, property, fieldType, qualifiedFieldType);
            copyCtorBuilder.append("this." + fieldName + " = " + "entity.get" + Strings.capitalize(fieldName) + "();");

            if (!property.equals(idProperty) && isWritable)
            {
               assembleJPABuilder.append("entity.set" + Strings.capitalize(property.getName()) + "(this." + fieldName
                        + ");");
            }
         }
      }

      copyCtorBuilder.append("}");
      copyCtor.setBody(copyCtorBuilder.toString());
      if (!isEmbeddedType)
      {
         assembleJPABuilder.append("entity = em.merge(entity);");
      }
      assembleJPABuilder.append("return entity;");
      assembleJPA.setBody(assembleJPABuilder.toString());

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

   private JavaClass tryGetJavaClass(JavaSourceFacet java, String qualifiedFieldType)
   {
      try
      {
         JavaResource javaResource = java.getJavaResource(Packages.toFileSyntax(qualifiedFieldType));
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

   private Field<JavaClass> addCollectionProperty(JavaClass dtoClass, JPAProperty field, String pkDTOType,
            String qualifiedDTOType)
   {
      String concreteCollectionType = null;
      String qualifiedConcreteCollectionType = null;
      String fieldType = field.getType();
      if (fieldType.equals("Set"))
      {
         concreteCollectionType = "HashSet";
         qualifiedConcreteCollectionType = "java.util.HashSet";
      }
      else if (fieldType.equals("List"))
      {
         concreteCollectionType = "ArrayList";
         qualifiedConcreteCollectionType = "java.util.ArrayList";
      }
      else if (fieldType.equals("Map"))
      {
         concreteCollectionType = "HashMap";
         qualifiedConcreteCollectionType = "java.util.HashMap";
      }
      Field<JavaClass> dtoField = dtoClass.addField("private " + fieldType + "<" + pkDTOType + "> "
               + field.getName() + "= new " + concreteCollectionType + "<" + pkDTOType + ">();");
      dtoClass.addImport(field.getQualifiedType());
      dtoClass.addImport(qualifiedConcreteCollectionType);
      if (!Types.isJavaLang(qualifiedDTOType))
      {
         dtoClass.addImport(qualifiedDTOType);
      }
      Refactory.createGetterAndSetter(dtoClass, dtoField);
      return dtoField;
   }

   private void addProperty(JavaClass dtoClass, JPAProperty field, String simpleName, String qualifiedName)
   {
      Field<JavaClass> dtoField = dtoClass.addField("private " + simpleName + " " + field.getName() + ";");
      if (!(field.isPrimitive() || Types.isJavaLang(qualifiedName) || Types.isArray(qualifiedName)))
      {
         dtoClass.addImport(qualifiedName);
      }
      if (Types.isArray(qualifiedName))
      {
         String arrayType = field.getTypeInspector().getQualifiedName();
         if (!(Types.isJavaLang(arrayType) || Types.isPrimitive(arrayType)))
         {
            dtoClass.addImport(arrayType);
         }
      }
      Refactory.createGetterAndSetter(dtoClass, dtoField);
   }
}
