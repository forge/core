/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.beans;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.forge.parser.java.util.Refactory;
import org.jboss.forge.parser.java.util.Types;

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
   public void removeField(final JavaClass targetClass, final Field<JavaClass> field)
   {
      targetClass.removeField(field);
      removeAccessor(targetClass, field);
      removeMutator(targetClass, field);
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
   public Field<JavaClass> addFieldTo(final JavaClass targetClass, final String fieldType,
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
    * @param annotations An optional list of annotations that will be added to the field 
    * @return The newly created field
    */
   public Field<JavaClass> addFieldTo(final JavaClass targetClass, final String fieldType,
            final String fieldName, Visibility visibility, boolean withGetter, boolean withSetter, 
            String... annotations)            
   {
      if (targetClass.hasField(fieldName))
      {
         throw new IllegalStateException("Entity already has a field named [" + fieldName + "]");
      }
      Field<JavaClass> field = targetClass.addField();
      field.setName(fieldName).setVisibility(visibility).setType(fieldType);
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
      
      Refactory.createGetterAndSetter(targetClass, field);
      
      if (!withGetter) 
      {
         removeAccessor(targetClass, field);
      }
      
      if (!withSetter)
      {
         removeMutator(targetClass, field);
      }
      
      updateToString(targetClass);
      return field;
   }
   
   private JavaClass removeAccessor(final JavaClass targetClass, final Field<JavaClass> field)
   {
      String methodNameSuffix = Strings.capitalize(field.getName());
      if (targetClass.hasMethodSignature("get" + methodNameSuffix))
      {
         Method<JavaClass> method = targetClass.getMethod("get" + methodNameSuffix);
         targetClass.removeMethod(method);
      }
      
      return targetClass;
   }
   
   private JavaClass removeMutator(final JavaClass targetClass, final Field<JavaClass> field)
   {
      String methodNameSuffix = Strings.capitalize(field.getName());
      if (targetClass.hasMethodSignature("set" + methodNameSuffix, field.getQualifiedType()))
      {
         Method<JavaClass> method = targetClass.getMethod("set" + methodNameSuffix, field.getQualifiedType());
         targetClass.removeMethod(method);
      }
      
      return targetClass;
   }
   
   private void updateToString(final JavaClass targetEntity)
   {
      if (targetEntity.hasMethodSignature("toString"))
      {
         targetEntity.removeMethod(targetEntity.getMethod("toString"));
      }
      List<Field<JavaClass>> fields = new ArrayList<Field<JavaClass>>();
      for (Field<JavaClass> f : targetEntity.getFields())
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

   protected boolean canAddFieldToToString(Field<JavaClass> field)
   {
      return true;
   }
}
