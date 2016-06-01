/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.beans;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.PropertySource;
import org.jboss.forge.roaster.model.util.Refactory;
import org.jboss.forge.roaster.model.util.Types;

/**
 * Operations for manipulating JavaBeans methods of a class
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class FieldOperations
{
   /**
    * Removes the field, including its getters and setters and updating toString()
    *
    * @param targetClass The class, which field will be removed
    * @param field The field to be removed
    */
   public void removeField(final JavaClassSource targetClass, final Field<JavaClassSource> field)
   {
      PropertySource<JavaClassSource> property = targetClass.getProperty(field.getName());
      property.setMutable(false).setAccessible(false);
      targetClass.removeProperty(property);
      updateToString(targetClass);
   }

   /**
    * Adds the field with private visibility, including getter and setter, updating the toString()
    *
    * @param targetClass The class which the field will be added to
    * @param fieldType The type of the field
    * @param fieldName The name of the field
    * @param annotations An optional list of annotations that will be added to the field
    * @return The newly created field
    */
   public FieldSource<JavaClassSource> addFieldTo(final JavaClassSource targetClass, final String fieldType,
            final String fieldName, String... annotations)

   {
      return addFieldTo(targetClass, fieldType, fieldName, Visibility.PRIVATE, true, true, annotations);
   }

   /**
    * Adds the field, updating the toString(). If specified, adds a getter, a setter or both.
    *
    * @param targetClass The class which the field will be added to
    * @param fieldType The type of the field
    * @param fieldName The name of the field
    * @param visibility The visibility of the newly created field
    * @param withGetter Specifies whether accessor method should be created
    * @param withSetter Specifies whether mutator method should be created
    * @param updateToString Specifies whether the field should be added in the toString method
    * @param annotations An optional list of annotations that will be added to the field
    * @return The newly created field
    */
   public FieldSource<JavaClassSource> addFieldTo(final JavaClassSource targetClass, final String fieldType,
            final String fieldName, Visibility visibility, boolean withGetter, boolean withSetter,
            boolean updateToString,
            String... annotations)
   {
      if (targetClass.hasField(fieldName))
      {
         throw new IllegalStateException("Entity already has a field named [" + fieldName + "]");
      }
      PropertySource<JavaClassSource> property = targetClass.addProperty(fieldType, fieldName);
      FieldSource<JavaClassSource> field = property.getField();
      field.setVisibility(visibility);
      for (String annotation : annotations)
      {
         field.addAnnotation(annotation);
      }

      String fieldTypeForImport = Types.stripArray(fieldType);
      if (!fieldTypeForImport.startsWith("java.lang.") && fieldTypeForImport.contains(".")
               && !fieldTypeForImport.equals(targetClass.getCanonicalName()))
      {
         targetClass.addImport(fieldTypeForImport);
      }
      if (!withGetter)
      {
         targetClass.removeMethod(property.getAccessor());
      }
      if (!withSetter)
      {
         targetClass.removeMethod(property.getMutator());
      }
      if (updateToString)
      {
         updateToString(targetClass);
      }

      return field;
   }

   /**
    * Adds the field, updating the toString(). If specified, adds a getter, a setter or both.
    *
    * @param targetClass The class which the field will be added to
    * @param fieldType The type of the field
    * @param fieldName The name of the field
    * @param visibility The visibility of the newly created field
    * @param withGetter Specifies whether accessor method should be created
    * @param withSetter Specifies whether mutator method should be created
    * @param annotations An optional list of annotations that will be added to the field
    * @return The newly created field
    */
   public FieldSource<JavaClassSource> addFieldTo(final JavaClassSource targetClass, final String fieldType,
            final String fieldName, Visibility visibility, boolean withGetter, boolean withSetter,
            String... annotations)
   {
      return addFieldTo(targetClass, fieldType, fieldName, visibility, withGetter, withSetter, true, annotations);
   }

   private void updateToString(final JavaClassSource targetEntity)
   {
      if (targetEntity.hasMethodSignature("toString"))
      {
         targetEntity.removeMethod(targetEntity.getMethod("toString"));
      }
      List<FieldSource<JavaClassSource>> fields = new ArrayList<>();
      for (FieldSource<JavaClassSource> f : targetEntity.getFields())
      {
         if (canAddFieldToToString(f))
         {
            fields.add(f);
         }
      }
      if (!fields.isEmpty())
      {
         Refactory.createToStringFromFields(targetEntity, fields);
      }
   }

   protected boolean canAddFieldToToString(Field<JavaClassSource> field)
   {
      return !field.isStatic() && !field.isTransient() && !field.getType().isArray();
   }

   /**
    * @param project Project in which the fieldType will be searched
    * @param fieldType Full type of the field with package
    * @return true if fieldType was found and is enum false otherwise.
    * @throws IllegalArgumentException if fieldType or project is null
    */
   public boolean isFieldTypeEnum(Project project, String fieldType)
   {
      return isFieldTypeEnum(project, null, fieldType);
   }

   /**
    * @param project Project in which the fieldType will be searched
    * @param fieldType Type of the field
    * @param targetEntity Entity which package which will be used if fieldType doesn't have package specified
    * @return true if fieldType was found and is enum false otherwise.
    * @throws IllegalArgumentException if fieldType or project is null
    */
   public boolean isFieldTypeEnum(Project project, JavaClassSource targetEntity, String fieldType)
   {
      boolean isEnum = false;

      Assert.notNull(fieldType, "Field type should not be null");
      Assert.notNull(project, "Field project should not be null");

      try
      {
         isEnum = project.getFacet(JavaSourceFacet.class).getJavaResource(fieldType).getJavaType().isEnum();
      }
      catch (FileNotFoundException | ResourceException e1)
      {
         try
         {
            if (targetEntity != null)
            {

               isEnum = project.getFacet(JavaSourceFacet.class)
                        .getJavaResource(targetEntity.getPackage() + "." + fieldType).getJavaType().isEnum();
            }
         }
         catch (FileNotFoundException | ResourceException e2)
         {
            // ignore
         }
      }

      return isEnum;
   }
}
