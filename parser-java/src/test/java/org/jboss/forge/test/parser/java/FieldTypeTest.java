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
package org.jboss.forge.test.parser.java;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Type;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FieldTypeTest
{
   @Test
   public void testGetReturnTypeReturnsFullTypeForJavaLang() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class).addField("public Long l;");
      Assert.assertEquals("java.lang.Long", field.getQualifiedType());
      Assert.assertEquals("Long", field.getType());
   }

   @Test
   public void testGetReturnTypeReturnsFullTypeForJavaLangGeneric() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class)
               .addField("public List<Long> list;");
      field.getOrigin().addImport(List.class);
      Assert.assertEquals("java.util.List", field.getQualifiedType());
      Assert.assertEquals("List", field.getType());
   }

   @Test
   public void testGetReturnTypeObjectArray() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class)
               .addField("public List[] field;");
      field.getOrigin().addImport(List.class);
      Type<JavaClass> type = field.getTypeInspector();
      Assert.assertEquals("java.util.List", type.getQualifiedName());
      Assert.assertFalse(type.isParameterized());
      Assert.assertFalse(type.isWildcard());
      Assert.assertFalse(type.isPrimitive());
      Assert.assertFalse(type.isQualified());
      Assert.assertTrue(type.isArray());

      List<Type<JavaClass>> arguments = type.getTypeArguments();

      Assert.assertEquals(0, arguments.size());
   }

   @Test
   public void testGetReturnTypeObjectArrayParameterized() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class)
               .addField("public List<Long>[] list;");
      field.getOrigin().addImport(List.class);
      Type<JavaClass> type = field.getTypeInspector();
      Assert.assertEquals("java.util.List", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());
      Assert.assertFalse(type.isWildcard());
      Assert.assertFalse(type.isPrimitive());
      Assert.assertFalse(type.isQualified());
      Assert.assertTrue(type.isArray());

      List<Type<JavaClass>> arguments = type.getTypeArguments();

      Assert.assertEquals(1, arguments.size());
   }

   @Test
   public void testGetReturnTypeObjectUnparameterized() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class)
               .addField("public List list;");
      field.getOrigin().addImport(List.class);
      Assert.assertEquals("java.util.List", field.getTypeInspector().getQualifiedName());
      Assert.assertFalse(field.getTypeInspector().isParameterized());
   }

   @Test
   public void testGetReturnTypeObjectParameterized() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class)
               .addField("public List<Long> list;");
      field.getOrigin().addImport(List.class);
      Type<JavaClass> type = field.getTypeInspector();
      Assert.assertEquals("java.util.List", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClass>> arguments = type.getTypeArguments();

      Assert.assertEquals(1, arguments.size());
      Assert.assertEquals("Long", arguments.get(0).getName());
      Assert.assertEquals("java.lang.Long", arguments.get(0).getQualifiedName());
   }

   @Test
   public void testGetReturnTypeObjectWildcard() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class)
               .addField("public List<?> list;");
      field.getOrigin().addImport(List.class);
      Type<JavaClass> type = field.getTypeInspector();
      Assert.assertEquals("java.util.List", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClass>> arguments = type.getTypeArguments();

      Assert.assertEquals(1, arguments.size());
      Assert.assertEquals("?", arguments.get(0).getName());
      Assert.assertEquals("?", arguments.get(0).getQualifiedName());
   }

   @Test
   public void testGetReturnTypeObjectParameterizedMultiple() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class)
               .addField("public Map<String, Long> map;");
      field.getOrigin().addImport(Map.class);
      Type<JavaClass> type = field.getTypeInspector();
      Assert.assertEquals("java.util.Map", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClass>> arguments = type.getTypeArguments();

      Assert.assertEquals(2, arguments.size());
      Assert.assertEquals("String", arguments.get(0).getName());
      Assert.assertEquals("java.lang.String", arguments.get(0).getQualifiedName());

      Assert.assertEquals("Long", arguments.get(1).getName());
      Assert.assertEquals("java.lang.Long", arguments.get(1).getQualifiedName());
   }

   @Test
   public void testGetReturnTypeObjectParameterizedNested() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class)
               .addField("public List<List<Long>> map;");
      field.getOrigin().addImport(List.class);
      Type<JavaClass> type = field.getTypeInspector();
      Assert.assertEquals("java.util.List", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClass>> arguments = type.getTypeArguments();

      Assert.assertEquals(1, arguments.size());
      Assert.assertEquals("List", arguments.get(0).getName());
      Assert.assertEquals("java.util.List", arguments.get(0).getQualifiedName());

      Assert.assertEquals(1, arguments.size());
      Assert.assertEquals("Long", arguments.get(0).getTypeArguments().get(0).getName());
      Assert.assertEquals("java.lang.Long", arguments.get(0).getTypeArguments().get(0).getQualifiedName());
   }

   @Test
   public void testGetReturnTypeObjectParameterizedMultipleNested() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class)
               .addField("public Map<String, List<Long>> map;");
      field.getOrigin().addImport(List.class);
      field.getOrigin().addImport(Map.class);
      Type<JavaClass> type = field.getTypeInspector();
      Assert.assertEquals("java.util.Map", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClass>> arguments = type.getTypeArguments();

      Assert.assertEquals(2, arguments.size());
      Assert.assertEquals("String", arguments.get(0).getName());
      Assert.assertEquals("java.lang.String", arguments.get(0).getQualifiedName());

      Assert.assertEquals("List", arguments.get(1).getName());
      Assert.assertEquals("java.util.List", arguments.get(1).getQualifiedName());
   }

   @Test
   public void testGetReturnTypeObjectParameterizedArrayMultipleNested() throws Exception
   {
      Field<JavaClass> field = JavaParser.create(JavaClass.class)
               .addField("public Map<String, List<Long>>[] maps;");
      field.getOrigin().addImport(List.class);
      field.getOrigin().addImport(Map.class);
      Type<JavaClass> type = field.getTypeInspector();
      Assert.assertEquals("java.util.Map", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClass>> arguments = type.getTypeArguments();

      Assert.assertEquals(2, arguments.size());
      Assert.assertEquals("String", arguments.get(0).getName());
      Assert.assertEquals("java.lang.String", arguments.get(0).getQualifiedName());

      Assert.assertEquals("List", arguments.get(1).getName());
      Assert.assertEquals("java.util.List", arguments.get(1).getQualifiedName());
   }

}
