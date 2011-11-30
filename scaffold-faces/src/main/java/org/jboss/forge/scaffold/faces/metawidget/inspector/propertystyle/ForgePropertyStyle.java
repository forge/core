/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle;

import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.util.List;
import java.util.Map;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.MethodHolder;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.metawidget.inspector.iface.InspectorException;
import org.metawidget.inspector.impl.propertystyle.BaseProperty;
import org.metawidget.inspector.impl.propertystyle.BasePropertyStyle;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.inspector.impl.propertystyle.ValueAndDeclaredType;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.simple.StringUtils;

/**
 * Inspects Forge-specific <tt>JavaSource</tt> objects for properties.
 *
 * @author Richard Kennard
 */

public class ForgePropertyStyle
         extends BasePropertyStyle
{
   //
   // Private members
   //

   private final Project project;

   //
   // Constructor
   //

   public ForgePropertyStyle(final ForgePropertyStyleConfig config)
   {
      super(config);

      this.project = config.getProject();
   }

   //
   // Public methods
   //

   /**
    * Traverses the given Class heirarchy using properties of the given names.
    *
    * @return the declared type (not actual type). May be null
    */

   @Override
   public ValueAndDeclaredType traverse(final Object toTraverse, final String type, final boolean onlyToParent,
            final String... names)
   {
      // Traverse through names (if any)

      if ((names == null) || (names.length == 0))
      {
         // If no names, no parent

         if (onlyToParent)
         {
            return new ValueAndDeclaredType(null, null);
         }

         return new ValueAndDeclaredType(null, type);
      }

      String traverseDeclaredType = type;

      for (int loop = 0, length = names.length; loop < length; loop++)
      {
         if (onlyToParent && (loop >= (length - 1)))
         {
            return new ValueAndDeclaredType(null, traverseDeclaredType);
         }

         String name = names[loop];
         Property property = getProperties(traverseDeclaredType).get(name);

         if ((property == null) || !property.isReadable())
         {
            return new ValueAndDeclaredType(null, null);
         }

         traverseDeclaredType = property.getType();
      }

      return new ValueAndDeclaredType(null, traverseDeclaredType);
   }

   //
   // Protected methods
   //

   @Override
   protected Map<String, Property> inspectProperties(final String type)
   {
      try
      {
         // LinkedHashMap so that returns ordered properties

         Map<String, Property> properties = CollectionUtils.newLinkedHashMap();

         // Lookup properties

         JavaSource<?> clazz = sourceForName(type);

         if (clazz == null)
         {
            return properties;
         }

         lookupGetters(properties, (MethodHolder<?>) clazz);
         lookupSetters(properties, (MethodHolder<?>) clazz);

         return properties;
      }
      catch (Exception e)
      {
         throw InspectorException.newException(e);
      }
   }

   /**
    * Lookup getter-based properties.
    * <p>
    * This method will be called after <code>lookupFields</code> but before <code>lookupSetters</code>.
    */

   protected void lookupGetters(final Map<String, Property> properties, final MethodHolder<?> clazz)
   {
      // Hack until https://issues.jboss.org/browse/FORGE-368

      for (Method<?> method : clazz.getMethods())
      {
         // Exclude static methods

         if (method.isStatic())
         {
            continue;
         }

         // Get type

         if (!method.getParameters().isEmpty())
         {
            continue;
         }

         if (method.getReturnType() == null)
         {
            continue;
         }

         // Get name

         String propertyName = isGetter(method);

         if (propertyName == null)
         {
            continue;
         }

         // Exclude based on other criteria
         //
         // (explicitly set to null in case we encounted/encounter an imbalanced field/setter)

         String type = getQualifiedType(method.getReturnType());

         // TODO:if (isExcluded(ClassUtils.getOriginalDeclaringClass(method), propertyName, type))
         {
            // properties.put(propertyName, null);
            // continue;
         }

         properties
                  .put(propertyName,
                           new ForgeProperty(propertyName, type, method, null, getPrivateField(
                                    (FieldHolder<?>) clazz,
                                    propertyName)));
      }
   }

   /**
    * Returns whether the given method is a 'getter' method.
    *
    * @param method a parameterless method that returns a non-void
    * @return the property name
    */

   protected String isGetter(final Method<?> method)
   {

      String methodName = method.getName();
      String propertyName;

      if (methodName.startsWith(ClassUtils.JAVABEAN_GET_PREFIX))
      {
         propertyName = methodName.substring(ClassUtils.JAVABEAN_GET_PREFIX.length());

      }
      else if (methodName.startsWith(ClassUtils.JAVABEAN_IS_PREFIX) && boolean.class.equals(method.getReturnType()))
      {

         // As per section 8.3.2 (Boolean properties) of The JavaBeans API specification, 'is'
         // only applies to boolean (little 'b')

         propertyName = methodName.substring(ClassUtils.JAVABEAN_IS_PREFIX.length());
      }
      else
      {
         return null;
      }

      if (!StringUtils.isCapitalized(propertyName))
      {
         return null;
      }

      return StringUtils.decapitalize(propertyName);
   }

   /**
    * Lookup setter-based properties.
    * <p>
    * This method will be called after <code>lookupFields</code> and <code>lookupGetters</code>.
    */

   protected void lookupSetters(final Map<String, Property> properties, final MethodHolder<?> clazz)
   {

      for (Method<?> method : clazz.getMethods())
      {

         // Exclude static methods

         if (method.isStatic())
         {
            continue;
         }

         // Get type

         List<Parameter> parameters = method.getParameters();

         if (parameters.size() != 1)
         {
            continue;
         }

         // Get name

         String propertyName = isSetter(method);

         if (propertyName == null)
         {
            continue;
         }

         // Exclude based on other criteria
         //
         // (explicitly set to null in case we encountered an imbalanced field/getter)

         String type = parameters.get(0).getType();

         // TODO:if (isExcluded(ClassUtils.getOriginalDeclaringClass(method), propertyName, type))
         {
            // properties.put(propertyName, null);
            // continue;
         }

         // Already found via its getter?

         Property existingProperty = properties.get(propertyName);

         if (existingProperty instanceof ForgeProperty)
         {
            ForgeProperty existingForgeProperty = (ForgeProperty) existingProperty;

            // Beware covariant return types: always prefer the getter's type

            properties.put(
                     propertyName,
                     new ForgeProperty(propertyName, existingForgeProperty.getType(),
                              existingForgeProperty.getReadMethod(), method, getPrivateField((FieldHolder<?>) clazz,
                                       propertyName)));
            continue;
         }

         // Explicitly excluded based on getter already?

         if ((existingProperty == null) && properties.containsKey(propertyName))
         {
            continue;
         }

         properties
                  .put(propertyName,
                           new ForgeProperty(propertyName, type, null, method, getPrivateField(
                                    (FieldHolder<?>) clazz,
                                    propertyName)));
      }
   }

   /**
    * Returns whether the given method is a 'setter' method.
    *
    * @param method a single-parametered method. May return non-void (ie. for Fluent interfaces)
    * @return the property name
    */

   protected String isSetter(final Method<?> method)
   {

      String methodName = method.getName();

      if (!methodName.startsWith(ClassUtils.JAVABEAN_SET_PREFIX))
      {
         return null;
      }

      String propertyName = methodName.substring(ClassUtils.JAVABEAN_SET_PREFIX.length());

      if (!StringUtils.isCapitalized(propertyName))
      {
         return null;
      }

      return StringUtils.decapitalize(propertyName);
   }

   /**
    * Gets the private field representing the given <code>propertyName</code> within the given class. Uses the
    * configured <code>privateFieldConvention</code> (if any). Traverses up the superclass heirarchy as necessary.
    * <p>
    * Note it is <em>not</em> considered an error if a property has no corresponding private field: not all properties
    * do. For example a property <code>getAge</code> may be calculated internally based off a <code>mDateOfBirth</code>
    * field, with no corresponding <code>mAge</code> field per se.
    * <p>
    * Clients may override this method to change how the public-method-to-private-field mapping operates.
    *
    * @return the private Field for this propertyName, or null if no such field (should not throw NoSuchFieldException)
    */

   protected Field<?> getPrivateField(final FieldHolder<?> fieldHolder, final String propertyName)
   {

      // Go looking for such a field, traversing the superclass heirarchy as necessary
      //
      // Note: the use of clazz.getDeclaredFields() may lead to Applet SecurityExceptions

      while (fieldHolder != null)
      { // TODO:&& !isExcludedBaseType( currentClass ) ) {

         return fieldHolder.getField(propertyName);
         // TODO: need to traverse? currentClass = currentClass.getSuperType();
      }

      return null;
   }

   //
   // Private methods
   //

   private JavaSource<?> sourceForName(final String type)
   {
      try
      {
         JavaSourceFacet javaSourceFact = this.project.getFacet(JavaSourceFacet.class);
         return javaSourceFact.getJavaResource(type).getJavaSource();
      }
      catch (FileNotFoundException e)
      {
         // Not a Forge-based type

         return null;
      }
   }

   /**
    * Hack until https://issues.jboss.org/browse/FORGE-371.
    */

   private String getQualifiedType(final String type)
   {
      if ("Long".equals(type))
      {
         return Long.class.getName();
      }

      if ("String".equals(type))
      {
         return String.class.getName();
      }

      return type;
   }

   //
   // Inner classes
   //

   public static class ForgeProperty
            extends BaseProperty
   {

      //
      // Private methods
      //

      private final Method<?> readMethod;

      private final Method<?> writeMethod;

      private final Field<?> privateField;

      //
      // Constructor
      //

      public ForgeProperty(final String name, final String type, final Method<?> readMethod,
               final Method<?> writeMethod,
               final Field<?> privateField)
      {

         super(name, type);

         this.readMethod = readMethod;
         this.writeMethod = writeMethod;

         // Must have a getter or a setter (or both)

         if ((this.readMethod == null) && (this.writeMethod == null))
         {
            throw InspectorException.newException("Property '" + name + "' has no getter and no setter");
         }

         this.privateField = privateField;
      }

      //
      // Public methods
      //

      @Override
      public boolean isReadable()
      {
         return (this.readMethod != null);
      }

      @Override
      public Object read(final Object obj)
      {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isWritable()
      {
         return (this.writeMethod != null);
      }

      @Override
      public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
      {
         org.jboss.forge.parser.java.Annotation<?> annotation = this.privateField.getAnnotation(annotationClass
                  .getName());

         if (annotation != null)
         {
            T annotationProxy = AnnotationProxy.newInstance(annotation);
            return annotationProxy;
         }

         return null;
      }

      @Override
      public String getGenericType()
      {
         // Note: this needs https://issues.jboss.org/browse/FORGE-387
         
         return null;
      }

      public Method<?> getReadMethod()
      {

         return this.readMethod;
      }

      public Method<?> getWriteMethod()
      {

         return this.writeMethod;
      }
   }

   /**
    * Java annotations are defined as interfaces. Therefore in order to instantiate one, we must create a Proxy.
    */

   public static class AnnotationProxy<T extends Annotation> implements InvocationHandler
   {

      //
      // Private statics
      //

      private final org.jboss.forge.parser.java.Annotation<?> annotationSource;

      private final Class<T> annotationClass;

      //
      // Public statics
      //

      @SuppressWarnings("unchecked")
      public static <T extends Annotation> T newInstance(
               final org.jboss.forge.parser.java.Annotation<?> annotationSource)
      {
         try
         {
            // Hack until https://issues.jboss.org/browse/FORGE-370

            Class<T> annotationClass = (Class<T>) Class.forName("javax.persistence." + annotationSource.getName());

            // TODO: test this not using annotationSource.getClass().getClassLoader() (will require integration test)

            return (T) java.lang.reflect.Proxy.newProxyInstance(
                     annotationClass.getClassLoader(),
                     new Class[] { annotationClass },
                     new AnnotationProxy<T>(annotationClass, annotationSource));
         }
         catch (Exception e)
         {
            throw InspectorException.newException(e);
         }
      }

      //
      // Constructor
      //

      private AnnotationProxy(final Class<T> annotationClass,
               final org.jboss.forge.parser.java.Annotation<?> annotationSource)
      {
         this.annotationSource = annotationSource;
         this.annotationClass = annotationClass;
      }

      //
      // Public methods
      //

      @Override
      public Object invoke(final Object proxy, final java.lang.reflect.Method method, final Object[] args)
               throws Throwable
      {
         try
         {
            String methodName = method.getName();

            // Get the value from the Forge Annotation class...

            String value = this.annotationSource.getStringValue(methodName);

            // ...if no value, return the default...

            java.lang.reflect.Method annotationMethod = this.annotationClass.getMethod(methodName);

            if (value == null)
            {
               return annotationMethod.getDefaultValue();
            }

            // ...otherwise cast it to the correct class

            Class<?> returnType = annotationMethod.getReturnType();

            if (boolean.class.equals(returnType))
            {
               return Boolean.valueOf(value);
            }
            else if (int.class.equals(returnType))
            {
               return Integer.valueOf(value);
            }

            return value;
         }
         catch (Exception e)
         {
            throw InspectorException.newException(e);
         }
      }
   }
}
