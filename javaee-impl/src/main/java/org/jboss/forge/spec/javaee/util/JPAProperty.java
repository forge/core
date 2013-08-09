package org.jboss.forge.spec.javaee.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Type;

public class JPAProperty
{

   private final String name;
   private Field<?> actualField;
   private Method<JavaClass> accessor;
   private Method<JavaClass> mutator;

   public JPAProperty(final String name)
   {
      super();
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public Field<?> getActualField()
   {
      return actualField;
   }

   void setActualField(Field<?> actualField)
   {
      this.actualField = actualField;
   }

   public Method<JavaClass> getAccessor()
   {
      return accessor;
   }

   void setAccessor(Method<JavaClass> accessor)
   {
      this.accessor = accessor;
   }

   public Method<JavaClass> getMutator()
   {
      return mutator;
   }

   void setMutator(Method<JavaClass> mutator)
   {
      this.mutator = mutator;
   }

   public boolean isReadable()
   {
      return accessor != null;
   }

   public boolean isWritable()
   {
      return mutator != null;
   }

   public boolean isReadOnly()
   {
      return isReadable() && !isWritable();
   }

   public boolean hasAnnotation(Class<? extends Annotation> klass)
   {
      return (actualField != null && actualField.hasAnnotation(klass))
               || (accessor != null && accessor.hasAnnotation(klass));
   }

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

   public boolean isTransient()
   {
      return actualField != null && actualField.isTransient();
   }

   public String getType()
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

   public Type<?> getTypeInspector()
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
      if (!(obj instanceof JPAProperty))
         return false;
      JPAProperty other = (JPAProperty) obj;
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
