/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;

/**
 * @author Kevin Pollet
 */
public final class JavaHelper
{
   private static final String GET_PREFIX = "get";
   private static final String IS_PREFIX = "is";

   // disallow instantiation of this helper class
   private JavaHelper()
   {}

   public static Method<JavaClass> getFieldAccessor(final Field<JavaClass> field)
   {
      if (field == null)
      {
         throw new IllegalArgumentException("The parameter 'field' cannot be null");
      }

      final JavaClass javaClass = field.getOrigin();
      final String accessorMethodSuffix = getAccessorMethodSuffix(field);

      Method<JavaClass> method = null;
      if (field.isType(Boolean.class) || field.isType(Boolean.TYPE) || field.isType(boolean.class))
      {
         method = javaClass.getMethod(IS_PREFIX + accessorMethodSuffix);
      }
      if (method == null)
      {
         method = javaClass.getMethod(GET_PREFIX + accessorMethodSuffix);
      }
      return method;
   }

   private static String getAccessorMethodSuffix(final Field<JavaClass> field)
   {
      final String fieldName = field.getName();
      final StringBuilder methodSuffix = new StringBuilder();
      if (fieldName.length() > 0)
      {
         methodSuffix.append(Character.toUpperCase(fieldName.charAt(0)));
         if (fieldName.length() > 1)
         {
            methodSuffix.append(fieldName.substring(1, fieldName.length()));
         }
      }
      return methodSuffix.toString();
   }
}
