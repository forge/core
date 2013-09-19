/*
 * Copyright 2012-2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.inspector.propertystyle;

import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.MethodHolder;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
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

   private final MessageFormat privateFieldConvention;

   //
   // Constructor
   //

   public ForgePropertyStyle(final ForgePropertyStyleConfig config)
   {
      super(config);

      this.project = config.getProject();
      this.privateFieldConvention = config.getPrivateFieldConvention();
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

         inspectClassProperties(type, properties);

         return properties;
      }
      catch (Exception e)
      {
         throw InspectorException.newException(e);
      }
   }

   /**
    * Recursive lookup for properties from superclass in order to support inheritance
    */
   private void inspectClassProperties(final String type,
            Map<String, Property> properties)
   {

      JavaSource<?> clazz = sourceForName(this.project, type);

      if (clazz instanceof MethodHolder<?>)
      {
         lookupGetters(properties, (MethodHolder<?>) clazz);
         lookupSetters(properties, (MethodHolder<?>) clazz);

         if (clazz instanceof JavaClass)
         {
            JavaClass source = JavaParser.parse(JavaClass.class,
                     clazz.toString());
            if (!source.getSuperType().equals("java.lang.Object"))
            {
               inspectClassProperties(source.getSuperType(), properties);
            }
         }
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
         
         // Exclude non-public methods. Section 8.3.1 of the JavaBean specification requires that getters be public.
         if (!method.isPublic())
         {
            continue;
         }

         // Get type

         if (!method.getParameters().isEmpty())
         {
            continue;
         }

         String returnType = method.getQualifiedReturnType();

         if (returnType == null)
         {
            continue;
         }

         // Get name

         String propertyName = isGetter(method);

         if (propertyName == null)
         {
            continue;
         }

         Field<?> privateField = getPrivateField((FieldHolder<?>) clazz, propertyName);

         if (privateField != null && this.privateFieldConvention == null)
         {
            propertyName = privateField.getName();
         }

         properties
                  .put(propertyName,
                           new ForgeProperty(propertyName, returnType, method, null, privateField, this.project));
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
      else if (methodName.startsWith(ClassUtils.JAVABEAN_IS_PREFIX)
               && boolean.class.equals(method.getQualifiedReturnType()))
      {

         // As per section 8.3.2 (Boolean properties) of The JavaBeans API specification, 'is'
         // only applies to boolean (little 'b')

         propertyName = methodName.substring(ClassUtils.JAVABEAN_IS_PREFIX.length());
      }
      else
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

   protected <O extends JavaSource<O>> void lookupSetters(final Map<String, Property> properties,
            final MethodHolder<O> clazz)
   {
      for (Method<O> method : clazz.getMethods())
      {
         // Exclude static methods

         if (method.isStatic())
         {
            continue;
         }
         
         // Exclude non-public methods. Section 8.3.1 of the JavaBean specification requires that setters be public.
         if (!method.isPublic())
         {
            continue;
         }

         // Get type

         List<Parameter<O>> parameters = method.getParameters();

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

         Field<?> privateField = getPrivateField((FieldHolder<?>) clazz, propertyName);

         if (privateField != null && this.privateFieldConvention == null)
         {
            propertyName = privateField.getName();
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
                                       propertyName), this.project));
            continue;
         }

         // Explicitly excluded based on getter already?

         if ((existingProperty == null) && properties.containsKey(propertyName))
         {
            continue;
         }

         properties
                  .put(propertyName,
                           new ForgeProperty(propertyName, type, null, method, privateField, this.project));
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

      return StringUtils.decapitalize(propertyName);
   }

   /**
    * Gets the private field representing the given <code>propertyName</code> within the given class.
    *
    * @return the private Field for this propertyName, or null if no such field (should not throw NoSuchFieldException)
    */

   protected Field<?> getPrivateField(final FieldHolder<?> fieldHolder, final String propertyName)
   {
      if (this.privateFieldConvention != null)
      {
         // Determine field name based on convention. MessageFormat arguments are:
         //
         // {0} = dateOfBirth, surname
         // {1} = DateOfBirth, Surname

         String[] arguments = new String[] { propertyName, StringUtils.capitalize(propertyName) };
         String fieldName;

         synchronized (this.privateFieldConvention)
         {
            fieldName = this.privateFieldConvention.format(arguments, new StringBuffer(), null).toString();
         }

         return fieldHolder.getField(fieldName);
      }

      Field<?> field = fieldHolder.getField(propertyName);

      // FORGE-402: support fields starting with capital letter

      if (field == null && !Character.isUpperCase(propertyName.charAt( 0 )))
      {
         field = fieldHolder.getField(StringUtils.capitalize(propertyName));
      }

      return field;
   }

   //
   // Private methods
   //

   /*package private*/

   static JavaSource<?> sourceForName(final Project project, final String type)
   {
      try
      {
         JavaSourceFacet javaSourceFact = project.getFacet(JavaSourceFacet.class);
         return javaSourceFact.getJavaResource(type).getJavaSource();
      }
      catch (FileNotFoundException e)
      {
         // Not a Forge-based type

         return null;
      }
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

      private final Project project;

      //
      // Constructor
      //

      public ForgeProperty(final String name, final String type, final Method<?> readMethod,
               final Method<?> writeMethod,
               final Field<?> privateField,
               final Project project)
      {
         super(name, type);

         this.readMethod = readMethod;
         this.writeMethod = writeMethod;

         // Must have a getter or a setter (or both)

         if (this.readMethod == null && this.writeMethod == null)
         {
            throw InspectorException.newException("Property '" + name + "' has no getter and no setter");
         }

         this.privateField = privateField;
         this.project = project;
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
      public void write(Object obj, Object value)
      {
         throw new UnsupportedOperationException();
      }

      @Override
      public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
      {
         org.jboss.forge.parser.java.Annotation<?> annotation = null;

         // https://issues.jboss.org/browse/FORGE-439: support annotations on readMethod

         if (this.readMethod != null)
         {
            annotation = this.readMethod.getAnnotation(annotationClass.getName());
         }

         if (annotation == null && this.privateField != null)
         {
            annotation = this.privateField.getAnnotation(annotationClass.getName());
         }

         if (annotation != null)
         {
            T annotationProxy = AnnotationProxy.newInstance(annotation);
            return annotationProxy;
         }

         return null;
      }

      public List<EnumConstant<JavaEnum>> getEnumConstants()
      {
         JavaSource<?> source = sourceForName( this.project, getType() );

         if ( source instanceof JavaEnum ) {
            return ((JavaEnum) source).getEnumConstants();
         }

         return null;
      }

      @Override
      public String getGenericType()
      {
         if (this.readMethod != null)
         {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            List<Type<?>> typeArguments = (List) this.readMethod.getReturnTypeInspector().getTypeArguments();

            if (!typeArguments.isEmpty())
            {
               return typeArguments.get(0).getQualifiedName();
            }
         }

         if (this.privateField != null)
         {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            List<Type<?>> typeArguments = (List) this.privateField.getTypeInspector().getTypeArguments();

            if (!typeArguments.isEmpty())
            {
               return typeArguments.get(0).getQualifiedName();
            }
         }

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
            Class<T> annotationClass = (Class<T>) Class.forName(annotationSource.getQualifiedName());

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

            // Reserved name

            if ("annotationType".equals(methodName))
            {
               return this.annotationClass;
            }

            // If no value, return the default...

            java.lang.reflect.Method annotationMethod = this.annotationClass.getMethod(methodName);
            String literalValue = this.annotationSource.getLiteralValue(methodName);

            if (literalValue == null)
            {
               Object defaultValue = annotationMethod.getDefaultValue();

               if (defaultValue == null)
               {
                  throw new UnsupportedOperationException(methodName + " does not have a default value");
               }

               return defaultValue;
            }

            // ...otherwise parse it

            return parse(literalValue, annotationMethod.getReturnType());
         }
         catch (Exception e)
         {
            throw InspectorException.newException(e);
         }
      }

      //
      // Private methods
      //

      /**
       * Parses the given literal value into the given returnType. Supports all standard annotation types (JLS 9.7).
       */

      private Object parse(String literalValue, Class<?> returnType) throws ClassNotFoundException
      {
         // Primitives

         if (byte.class.equals(returnType))
         {
            return Byte.valueOf(literalValue);
         }
         if (short.class.equals(returnType))
         {
            return Short.valueOf(literalValue);
         }
         if (int.class.equals(returnType))
         {
            return Integer.valueOf(literalValue);
         }
         if (long.class.equals(returnType))
         {
            String valueToUse = literalValue;
            if (valueToUse.endsWith("l") || valueToUse.endsWith("L"))
            {
               valueToUse = valueToUse.substring(0, valueToUse.length() - 1);
            }
            return Long.valueOf(valueToUse);
         }
         if (float.class.equals(returnType))
         {
            String valueToUse = literalValue;
            if (valueToUse.endsWith("f") || valueToUse.endsWith("F"))
            {
               valueToUse = valueToUse.substring(0, valueToUse.length() - 1);
            }
            return Float.valueOf(valueToUse);
         }
         if (double.class.equals(returnType))
         {
            String valueToUse = literalValue;
            if (valueToUse.endsWith("d") || valueToUse.endsWith("D"))
            {
               valueToUse = literalValue.substring(0, valueToUse.length() - 1);
            }
            return Double.valueOf(valueToUse);
         }
         if (boolean.class.equals(returnType))
         {
            return Boolean.valueOf(literalValue);
         }
         if (char.class.equals(returnType))
         {
            return Character.valueOf(literalValue.charAt(1));
         }

         // Arrays

         if (returnType.isArray())
         {
            String[] values = literalValue.substring(1, literalValue.length() - 1).split(",");
            int length = values.length;
            Class<?> componentType = returnType.getComponentType();
            Object array = Array.newInstance(componentType, length);

            for (int loop = 0; loop < length; loop++)
            {
               Array.set(array, loop, parse(values[loop], componentType));
            }

            return array;
         }

         // Enums

         if (returnType.isEnum())
         {
            Enum<?>[] constants = (Enum<?>[]) returnType.getEnumConstants();

            String valueToUse = StringUtils.substringAfterLast(literalValue, '.');

            for (Enum<?> inst : constants)
            {
               if (inst.name().equals(valueToUse))
               {
                  return inst;
               }
            }

            return null;
         }

         // Strings

         if (String.class.equals(returnType))
         {
            return literalValue.substring(1, literalValue.length() - 1);
         }

         // Classes

         if (Class.class.equals(returnType))
         {
            String resolvedType = StringUtils.substringBefore(literalValue, ".class");
            resolvedType = ((JavaSource<?>) this.annotationSource.getOrigin()).resolveType(resolvedType);
            return Class.forName(resolvedType);
         }

         // Annotations

         if (Annotation.class.isAssignableFrom(returnType))
         {
            String resolvedType = StringUtils.substringAfter(literalValue, "@");
            resolvedType = ((JavaSource<?>) this.annotationSource.getOrigin()).resolveType(resolvedType);

            return AnnotationProxy.newInstance(this.annotationSource);
         }

         // Unknown

         throw new UnsupportedOperationException(returnType.getSimpleName());
      }
   }
}
