/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.beans;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Type;

/**
 * Represents a single instance of a property corresponding to a field and/or a JavaBean style accessor and mutator.
 */
public class Property
{

   private final String name;
   private Field<JavaClass> actualField;
   private Method<JavaClass> accessor;
   private Method<JavaClass> mutator;

   public Property(final String name)
   {
      super();
      this.name = name;
   }

   /**
    * The name of the property as governed by the JavaBeans property naming convention. This automatically happens to be
    * field name. If a property has a getter named <code>getX()</code> then the property name is 'X' (without quotes).
    * Boolean property getters starting with 'is' are also considered - <code>isX()</code> corresponds to a property
    * named 'X'.
    * 
    * @return The name of the property.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Returns a reference to the {@link Field} instance represented by the property.
    * 
    * @return The backing field for this property
    */
   public Field<JavaClass> getActualField()
   {
      return actualField;
   }

   void setActualField(Field<JavaClass> actualField)
   {
      this.actualField = actualField;
   }

   /**
    * Returns a reference to the accessor {@link Method} instance for this property.
    * 
    * @return The accessor method for this property
    */
   public Method<JavaClass> getAccessor()
   {
      return accessor;
   }

   void setAccessor(Method<JavaClass> accessor)
   {
      this.accessor = accessor;
   }

   /**
    * Returns a reference to the mutator {@link Method} instance for this property.
    * 
    * @return The mutator method for this property
    */
   public Method<JavaClass> getMutator()
   {
      return mutator;
   }

   void setMutator(Method<JavaClass> mutator)
   {
      this.mutator = mutator;
   }

   /**
    * Indicates whether a property can be read from via a getter, or not.
    * 
    * @return true if the property has a getter, false otherwise
    */
   public boolean isReadable()
   {
      return accessor != null;
   }

   /**
    * Indicates whether a property can be writter to via a setter, or not.
    * 
    * @return true if the property has a setter, false otherwise
    */
   public boolean isWritable()
   {
      return mutator != null;
   }

   /**
    * Indicates whether a property is read only or not.
    * 
    * @return true if the property has an accessor only, false otherwise. If no accessor is found, it will return false.
    */
   public boolean isReadOnly()
   {
      if (isReadable() && !isWritable())
      {
         return true;
      }
      return false;
   }
   
   /**
    * Indicates whether a property is write only or not.
    * 
    * @return true if the property has a mutator only, false otherwise. If no mutator is found, it will return false.
    */
   public boolean isWriteOnly()
   {
      if (isWritable() && !isReadable())
      {
         return true;
      }
      return false;
   }

   /**
    * Verifies whether the provided annotation is present on the property. Only fields and accessors are considered.
    * Mutators/setter methods are ignored.
    * 
    * @param klass The annotation class whose presence is to be verified
    * @return true if the annotation is present on the field or the accessor of the property
    */
   public boolean hasAnnotation(Class<? extends Annotation> klass)
   {
      return (actualField != null && actualField.hasAnnotation(klass))
               || (accessor != null && accessor.hasAnnotation(klass));
   }

   /**
    * Verifies whether the provided annotation is present on the property at the level of the field or the accessor.
    * Only fields and accessors are considered. Mutators/setters are not considered valid arguments.
    * 
    * @param klass The annotation class whose presence is to be verified
    * @param type The {@link ElementType} at which the annotation should be specified. Should be either
    *           {@link ElementType#FIELD} or {@link ElementType#METHOD}.
    * @return true if the annotation was found on the specified element of the property
    */
   public boolean hasAnnotation(Class<? extends Annotation> klass, ElementType type)
   {
      if (ElementType.FIELD.equals(type))
      {
         return actualField != null && actualField.hasAnnotation(klass);
      }
      else if (ElementType.METHOD.equals(type))
      {
         return accessor != null && accessor.hasAnnotation(klass);
      }
      throw new IllegalArgumentException("Invalid ElementType enum value was provided.");
   }

   /**
    * Indicates whether the property is transient or not.
    * 
    * @return true if the underlying field of the property is transient
    */
   public boolean isTransient()
   {
      return actualField != null && actualField.isTransient();
   }

   /**
    * Retrieves the {@link Type} of the property.
    * 
    * @return the {@link Type} of the property
    */
   public Type<?> getType()
   {
      if (actualField != null)
      {
         return actualField.getTypeInspector();
      }
      else if (accessor != null)
      {
         return accessor.getReturnTypeInspector();
      }
      throw new IllegalStateException("The property " + name + " is not associated with a field or an accessor");
   }

   /**
    * Retrieves the simple name of the {@link Type} corresponding to the property.
    * 
    * @return the simple name of the property's {@link Type}.
    */
   public String getSimpleType()
   {
      if (actualField != null)
      {
         return actualField.getType();
      }
      else if (accessor != null)
      {
         return accessor.getReturnType();
      }
      throw new IllegalStateException("The property " + name + " is not associated with a field or an accessor");
   }

   /**
    * Retrieves the qualified name of the {@link Type} corresponding to the property.
    * 
    * @return the qualified name of the property's {@link Type}.
    */
   public String getQualifiedType()
   {
      if (actualField != null)
      {
         return actualField.getQualifiedType();
      }
      else if (accessor != null)
      {
         return accessor.getQualifiedReturnType();
      }
      throw new IllegalStateException("The property " + name + " is not associated with a field or an accessor");
   }

   /**
    * Indicates whether the underlying field or the accessor returns a Java language primitive type.
    * 
    * @return true if the type of the property is a Java language primitive.
    */
   public boolean isPrimitive()
   {
      return (actualField != null && actualField.isPrimitive())
               || (accessor != null && accessor.getReturnTypeInspector().isPrimitive());
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof Property))
         return false;
      Property other = (Property) obj;
      if (name == null)
      {
         if (other.name != null)
            return false;
      }
      else if (!name.equals(other.name))
         return false;
      return true;
   }

}
