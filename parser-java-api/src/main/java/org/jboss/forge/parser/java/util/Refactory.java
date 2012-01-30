/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.parser.java.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Refactory
{
   public static void createGetterAndSetter(final JavaClass clazz, final Field<JavaClass> field)
   {
      if (!clazz.hasField(field))
      {
         throw new IllegalArgumentException("Entity did not contain the given field [" + field + "]");
      }

      clazz.getMethods();

      String fieldName = field.getName();
      String methodNameSuffix = Strings.capitalize(fieldName);
      clazz.addMethod().setReturnType(field.getTypeInspector().toString()).setName("get" + methodNameSuffix)
               .setPublic()
               .setBody("return this." + fieldName + ";");

      if (!field.isFinal()) {
         clazz.addMethod().setReturnTypeVoid().setName("set" + methodNameSuffix).setPublic()
                  .setParameters("final " + field.getTypeInspector().toString() + " " + fieldName)
                  .setBody("this." + fieldName + " = " + fieldName + ";");
      }
   }

   public static void createHashCodeAndEquals(final JavaClass clazz)
   {
      clazz.addMethod(
               "public boolean equals(Object that) { " +
                        "if (this == that) { return true; } " +
                        "if (that == null) { return false; } " +
                        "if (getClass() != that.getClass()) { return false; } " +
                        "if (id != null) { return id.equals((("
                        + clazz.getName() + ") that).id); } " +
                        "return super.equals(that); " +
                        "}")
               .addAnnotation(Override.class);

      clazz.addMethod(
               "public int hashCode() { " +
                        "if (id != null) { return id.hashCode(); } " +
                        "return super.hashCode(); }")
               .addAnnotation(Override.class);
   }

   public static void createToStringFromFields(final JavaClass clazz)
   {
      List<Field<JavaClass>> fields = clazz.getFields();
      createToStringFromFields(clazz, fields);
   }

   public static void createToStringFromFields(final JavaClass clazz, final Field<JavaClass>... fields)
   {
      createToStringFromFields(clazz, Arrays.asList(fields));
   }

   public static void createToStringFromFields(final JavaClass clazz, final List<Field<JavaClass>> fields)
   {
      Method<JavaClass> method = clazz.addMethod().setName("toString").setReturnType(String.class).setPublic();

      List<String> list = new ArrayList<String>();

      String delimeter = "\n";
      for (Field<JavaClass> field : fields)
      {
         if (clazz.hasField(field))
         {
            String line = "";
            if (!field.isPrimitive())
               line += "if(" + field.getName() + " != null)\n";

            line += " result += \" \" + " + field.getName() + ";";
            list.add(line);
         }
      }

      String body = "String result = \"\";\n" +
               Strings.join(list, delimeter) + "\n" +
               "return result;";
      method.setBody(body);
   }
}
