package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;

import javax.inject.Inject;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.util.Packages;
import org.jboss.forge.spec.javaee.util.JPABeanIntrospector;
import org.jboss.forge.spec.javaee.util.JPAProperty;

public class JpaDtoGenerator
{

   @Inject
   private JavaSourceFacet java;

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

      DTOClassBuilder dtoClassBuilder = new DTOClassBuilder(entity, topLevel)
               .setPackage(dtoPackage)
               .setEmbeddedType(isEmbeddedType);

      JPAProperty idProperty = null;
      JPABeanIntrospector bean = new JPABeanIntrospector(entity);
      for (JPAProperty property : bean.getProperties())
      {
         if (property.hasAnnotation(Id.class))
         {
            idProperty = property;
            dtoClassBuilder.setIdProperty(idProperty);
            break;
         }
      }

      for (JPAProperty property : bean.getProperties())
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
         JavaClass propertyClass = tryGetJavaClass(qualifiedPropertyType);

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
            JavaClass parameterizedClass = tryGetJavaClass(qualifiedParameterizedType);
            if (parameterizedClass == null)
            {
               ShellMessages.warn(writer, "Omitting creation of fields and DTO for type " + qualifiedParameterizedType
                        + " due to missing source.");
               continue;
            }

            JavaClass nestedDTOClass = generatedDTOGraphForEntity(parameterizedClass, dtoPackage, false, false);
            // Then update the DTO for the collection field
            dtoClassBuilder.updateForCollectionProperty(property, nestedDTOClass, type);
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
            JavaClass associatedClass = tryGetJavaClass(qualifiedPropertyType);
            if (associatedClass == null)
            {
               ShellMessages.warn(writer, "Omitting creation of fields and DTO for type " + qualifiedPropertyType
                        + " due to missing source.");
               continue;
            }

            JavaClass nestedDTOClass = generatedDTOGraphForEntity(associatedClass, dtoPackage, false, false);
            dtoClassBuilder.updateForReferencedProperty(property, nestedDTOClass);
         }
         else if (isEmbedded)
         {
            // Create another DTO for the @Embedded type, if it does not exist
            JavaClass dtoForEmbeddedType = generatedDTOGraphForEntity(propertyClass, dtoPackage, true, true);
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

   private JavaClass tryGetJavaClass(String qualifiedFieldType)
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
}
