/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
import org.jboss.forge.spec.javaee.util.JPABean;
import org.jboss.forge.spec.javaee.util.JPAProperty;

/**
 * A DTO generator for JPA entities. Creates Root and Nested DTOs.
 */
public class RootAndNestedDtoGenerator
{

   @Inject
   private JavaSourceFacet java;

   @Inject
   private ShellPrintWriter writer;

   @Inject
   private DTOCollection dtoCollection;

   /**
    * Creates a collection of DTOs for the provided JPA entity, and any JPA entities referenced in the JPA entity.
    * 
    * @param entity The JPA entity for which DTOs are to be generated
    * @param dtoPackage The Java package in which the DTOs are to be created
    * @return The {@link DTOCollection} containing the DTOs created for the JPA entity.
    */
   public DTOCollection from(JavaClass entity, String dtoPackage)
   {
      if (entity == null)
      {
         throw new IllegalArgumentException("The argument entity was null.");
      }
      generatedDTOGraphForEntity(entity, dtoPackage, true, false);
      return dtoCollection;
   }

   private JavaClass generatedDTOGraphForEntity(JavaClass entity, String dtoPackage, boolean topLevel,
            boolean isEmbeddedType)
   {
      if (dtoCollection.containsDTOFor(entity, topLevel))
      {
         return dtoCollection.getDTOFor(entity, topLevel);
      }

      JPAProperty idProperty = null;
      JPABean bean = new JPABean(entity);
      idProperty = parseIdPropertyForJPAEntity(bean);

      DTOClassBuilder dtoClassBuilder = new DTOClassBuilder(entity, idProperty, topLevel)
               .setPackage(dtoPackage)
               .setEmbeddedType(isEmbeddedType);

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
            JPABean parameterizedClassBean = new JPABean(parameterizedClass);
            JPAProperty nestedDtoId = parseIdPropertyForJPAEntity(parameterizedClassBean);
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

   private JPAProperty parseIdPropertyForJPAEntity(JPABean bean)
   {
      for (JPAProperty property : bean.getProperties())
      {
         if (property.hasAnnotation(Id.class))
         {
            return property;
         }
      }
      return null;
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
