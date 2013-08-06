package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

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
import org.jboss.forge.spec.javaee.util.JPABean;

public class JpaDtoGenerator
{

   @Inject
   @ProjectScoped
   private Project project;

   @Inject
   private ShellPrintWriter writer;

   private Map<JavaClass, JavaResource> allDTOs = new HashMap<JavaClass, JavaResource>();

   public Map<JavaClass, JavaResource> from(JavaClass entity, String dtoPackage) throws FileNotFoundException
   {
      if (entity == null)
      {
         throw new IllegalArgumentException("The argument entity was null.");
      }
      allDTOs.clear();
      generatedDTOGraphForEntity(entity, dtoPackage, true);
      return allDTOs;
   }

   public JavaClass generatedDTOGraphForEntity(JavaClass entity, String dtoPackage, boolean topLevel)
            throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String dtoClassName = entity.getName() + (topLevel ? "" : "Nested") + "DTO";
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

      // Copy constructor for JPA Entity
      dtoClass.addImport(entity.getQualifiedName());
      Method<JavaClass> copyCtor = dtoClass.addMethod();
      copyCtor.setConstructor(true);
      copyCtor.setPublic();
      copyCtor.setParameters("final " + entity.getName() + " entity");
      StringBuilder copyCtorBuilder = new StringBuilder();

      for (Field<?> field : entity.getFields())
      {
         if (field.isTransient() || field.hasAnnotation(Transient.class) || field.isStatic())
         {
            // No known reason for transient and static fields to be present in DTOs.
            // Revisit this if necessary for @Transient
            continue;
         }

         String fieldName = field.getName();
         String fieldType = field.getType();
         String qualifiedFieldType = field.getQualifiedType();
         // Get the JavaClass for the field's type so that we can inspect it later for annotations and such
         // and recursively generate a DTO for it as well.
         JavaClass fieldClass = tryGetJavaClass(java, qualifiedFieldType);

         JPABean bean = new JPABean(entity);
         boolean isReadOnly = bean.isReadOnly(field);
         boolean isReadable = bean.isReadable(field);
         boolean isCollection = field.hasAnnotation(OneToMany.class) || field.hasAnnotation(ManyToMany.class);
         Type<?> fieldTypeInspector = field.getTypeInspector();
         boolean parameterized = fieldTypeInspector.isParameterized();
         boolean hasAssociation = field.hasAnnotation(OneToOne.class) || field.hasAnnotation(ManyToOne.class);
         boolean isEmbedded = field.hasAnnotation(Embedded.class)
                  || (fieldClass != null && fieldClass.hasAnnotation(Embeddable.class));

         if (!isReadable)
         {
            // Skip the field if it lacks a getter. It is obviously not permitted to be read by other classes
            continue;
         }

         if (isCollection && parameterized)
         {
            if(!topLevel)
            {
               // Do no expand collections beyond the root
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

            JavaClass nestedDTOClass = generatedDTOGraphForEntity(parameterizedClass, dtoPackage, false);
            allDTOs.put(parameterizedClass, java.saveJavaSource(nestedDTOClass));

            // Then create a collection field for the DTO
            String nestedDTOType = nestedDTOClass.getName();
            String qualifiedDTOType = nestedDTOClass.getQualifiedName();
            addCollectionProperty(dtoClass, field, nestedDTOType, qualifiedDTOType);

            // Add expressions in the ctor to extract the PK
            dtoClass.addImport(qualifiedParameterizedType);
            dtoClass.addImport(Iterator.class);
            String iterator = "iter" + Strings.capitalize(fieldName);
            copyCtorBuilder.append("Iterator" + " " + iterator + " = " + "entity.get"
                     + Strings.capitalize(fieldName) + "().iterator();");
            copyCtorBuilder.append("for (; " + iterator + ".hasNext() ;) ");
            copyCtorBuilder.append("{");
            copyCtorBuilder.append("this." + fieldName + ".add(" + "new " + nestedDTOType + "((" + simpleParameterizedType
                     + ")" + iterator + ".next()));");
            copyCtorBuilder.append("}");
         }
         else if (hasAssociation)
         {
            if(!topLevel)
            {
               // Do no expand associations beyond the root
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

            JavaClass nestedDTOClass = generatedDTOGraphForEntity(associatedClass, dtoPackage, false);
            allDTOs.put(associatedClass, java.saveJavaSource(nestedDTOClass));

            // Then create a field referencing the DTO
            String nestedType = nestedDTOClass.getName();
            String qualifiedDTOFieldType = nestedDTOClass.getQualifiedName();
            addProperty(dtoClass, field, nestedType, qualifiedDTOFieldType);

            // Add an expression in the ctor to extract the fields
            copyCtorBuilder.append("this." + fieldName + " = " + "new " + nestedType + "(entity.get"
                     + Strings.capitalize(fieldName) + "());");
         }
         else if (isEmbedded)
         {
            // Create another DTO for the @Embedded type, if it does not exist
            JavaClass dtoForEmbeddedType = generatedDTOGraphForEntity(fieldClass, dtoPackage, true);
            String embeddedType = dtoForEmbeddedType.getName();
            String qualifiedNameForEmbeddedType = dtoForEmbeddedType.getQualifiedName();
            addProperty(dtoClass, field, embeddedType, qualifiedNameForEmbeddedType);

            // Add an expression in the ctor to assign the embedded type
            copyCtorBuilder.append("this." + fieldName + " = " + "new " + embeddedType + "(entity.get"
                     + Strings.capitalize(fieldName) + "());");
         }
         else
         {
            addProperty(dtoClass, field, fieldType, qualifiedFieldType);
            copyCtorBuilder.append("this." + fieldName + " = " + "entity.get" + Strings.capitalize(fieldName) + "();");
         }
      }

      copyCtor.setBody(copyCtorBuilder.toString());

      allDTOs.put(entity, java.saveJavaSource(dtoClass));
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

   private Field<JavaClass> addCollectionProperty(JavaClass dtoClass, Field<?> field, String pkDTOType,
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

   private void addProperty(JavaClass dtoClass, Field<?> field, String simpleName, String qualifiedName)
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
