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

import junit.framework.Assert;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResolveTypeTest
{
   @Test
   public void testResolveTypePrimitiveByte() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public byte get()");
      Assert.assertEquals("byte", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypePrimitiveShort() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public short get()");
      Assert.assertEquals("short", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypePrimitiveInt() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public int get()");
      Assert.assertEquals("int", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypePrimitiveLong() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public long get()");
      Assert.assertEquals("long", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypePrimitiveFloat() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public float get()");
      Assert.assertEquals("float", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypePrimitiveDouble() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public double get()");
      Assert.assertEquals("double", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypePrimitiveBoolean() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public boolean get()");
      Assert.assertEquals("boolean", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypePrimitiveChar() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public char get()");
      Assert.assertEquals("char", method.getQualifiedReturnType());
   }

   /* Object Types */
   @Test
   public void testResolveTypeByte() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public Byte get()");
      Assert.assertEquals("java.lang.Byte", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypeShort() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public Short get()");
      Assert.assertEquals("java.lang.Short", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypeInt() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public Integer get()");
      Assert.assertEquals("java.lang.Integer", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypeLong() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public Long get()");
      Assert.assertEquals("java.lang.Long", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypeFloat() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public Float get()");
      Assert.assertEquals("java.lang.Float", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypeDouble() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public Double get()");
      Assert.assertEquals("java.lang.Double", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypeBoolean() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public Boolean get()");
      Assert.assertEquals("java.lang.Boolean", method.getQualifiedReturnType());
   }

   @Test
   public void testResolveTypeChar() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public CharSequence get()");
      Assert.assertEquals("java.lang.CharSequence", method.getQualifiedReturnType());
   }

}
