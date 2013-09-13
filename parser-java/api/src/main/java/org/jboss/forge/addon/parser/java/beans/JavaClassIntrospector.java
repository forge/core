/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.beans;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Parameter;

/**
 * Provides a Bean specific 'view' of a JPA entity. Used for obtaining access to JavaBean-like properties for further
 * inspection.
 */
public class JavaClassIntrospector
{

   private static final String GET_PREFIX = "get";
   private static final String SET_PREFIX = "set";
   private static final String IS_PREFIX = "is";

   private JavaClass klass;

   private List<Method<JavaClass>> methods;

   private Map<String, Property> propertyCache = new HashMap<String, Property>();

   public JavaClassIntrospector(final JavaClass klass)
   {
      this.klass = klass;
      this.methods = this.klass.getMethods();
      populatePropertyCache();
   }

   /**
    * Retrieves a list of property instances that correspond to JavaBean-like properties found in the Java class
    * 
    * @return a {@link List} of {@link Property} instances found in the Java class.
    */
   public List<Property> getProperties()
   {
      return new ArrayList<Property>(propertyCache.values());
   }

   private void populatePropertyCache()
   {
      locateProperties();
      for (Member<JavaClass, ?> member : klass.getMembers())
      {
         if (member.isStatic())
         {
            continue;
         }

         String memberName = member.getName();
         if (member instanceof Field)
         {
            @SuppressWarnings("unchecked")
            Field<JavaClass> field = (Field<JavaClass>) member;
            if (hasAccessor(field))
            {
               createOrUpdateProperty(memberName, field, null, null);
            }
         }

      }
   }

   private void locateProperties()
   {
      for (Method<JavaClass> method : methods)
      {
         if (method.isStatic())
         {
            continue;
         }

         if (isAccessor(method))
         {
            String propertyName = propertyNameFromMethod(method);
            createOrUpdateProperty(propertyName, null, method, null);
         }
         else if (isMutator(method))
         {
            String propertyName = propertyNameFromMethod(method);
            createOrUpdateProperty(propertyName, null, null, method);
         }
      }
   }

   private String propertyNameFromMethod(Method<JavaClass> method)
   {
      String methodName = method.getName();
      String qualifiedReturnType = method.getQualifiedReturnType();
      if (methodName.startsWith(GET_PREFIX))
      {
         return Introspector.decapitalize(methodName.substring(3));
      }
      else if (methodName.startsWith(IS_PREFIX)
               && (qualifiedReturnType.equals("boolean") || qualifiedReturnType.equals("java.lang.Boolean")))
      {
         return Introspector.decapitalize(methodName.substring(2));
      }
      else if (methodName.startsWith(SET_PREFIX))
      {
         return Introspector.decapitalize(methodName.substring(3));
      }
      else
      {
         return null;
      }
   }

   private Property createOrUpdateProperty(String name, Field<JavaClass> field, Method<JavaClass> accessor,
            Method<JavaClass> mutator)
   {
      Property property = propertyCache.get(name);
      if (property == null)
      {
         property = new Property(name);
         propertyCache.put(name, property);
      }
      if (field != null)
      {
         property.setActualField(field);
      }
      if (accessor != null)
      {
         property.setAccessor(accessor);
      }
      if (mutator != null)
      {
         property.setMutator(mutator);
      }
      return property;
   }

   private boolean hasAccessor(Field<?> field)
   {
      String fieldName = field.getName();
      Property property = propertyCache.get(fieldName);
      if (property == null)
      {
         return false;
      }
      else
      {
         return true;
      }
   }

   private boolean isAccessor(Method<JavaClass> method)
   {
      String methodName = method.getName();
      String qualifiedReturnType = method.getQualifiedReturnType();
      List<Parameter<JavaClass>> parameters = method.getParameters();
      if (!method.isStatic() && methodName.startsWith(GET_PREFIX) && qualifiedReturnType != null
               && parameters.size() == 0)
      {
         return true;
      }
      if (methodName.startsWith(IS_PREFIX)
               && (qualifiedReturnType.equals("boolean") || qualifiedReturnType.equals("java.lang.Boolean"))
               && parameters.size() == 0)
      {
         return true;
      }
      return false;
   }

   private boolean isMutator(Method<JavaClass> method)
   {
      String methodName = method.getName();
      String qualifiedReturnType = method.getQualifiedReturnType();
      List<Parameter<JavaClass>> parameters = method.getParameters();
      if (!method.isStatic() && methodName.startsWith(SET_PREFIX) && qualifiedReturnType == null
               && parameters.size() == 1)
      {
         return true;
      }
      return false;
   }
}
