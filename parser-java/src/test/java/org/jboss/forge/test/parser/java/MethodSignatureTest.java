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

import static org.junit.Assert.assertEquals;

import java.util.List;

import junit.framework.Assert;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.parser.java.Visibility;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MethodSignatureTest
{
   @Test
   public void testEmptyMethodSignature() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public void hello()");
      String signature = method.toSignature();
      assertEquals("public hello() : void", signature);
   }

   @Test
   public void testMethodSignatureParams() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public void hello(String foo, int bar)");
      String signature = method.toSignature();
      assertEquals("public hello(String, int) : void", signature);
   }

   @Test
   public void testMethodParams() throws Exception
   {
      Method<JavaClass> method = JavaParser.create(JavaClass.class).addMethod("public void hello(String foo, int bar)");
      List<Parameter> parameters = method.getParameters();

      Assert.assertEquals("String", parameters.get(0).getTypeInspector().toString());
      Assert.assertEquals("int", parameters.get(1).getTypeInspector().toString());
   }
   
   @Test
   public void testMethodVisibility() throws Exception {
       JavaClass javaClass = JavaParser.create(JavaClass.class);
       
       Method<JavaClass> method = javaClass.addMethod("public void hello()");
       assertVisibility(Visibility.PUBLIC, method);
       assertVisibility("public", method);
       
       method = javaClass.addMethod("protected void hello()");
       assertVisibility(Visibility.PROTECTED, method);
       assertVisibility("protected", method);
       
       method = javaClass.addMethod("private void hello()");
       assertVisibility(Visibility.PRIVATE, method);
       assertVisibility("private", method);
       
       method = javaClass.addMethod("void hello()");
       assertVisibility(Visibility.PACKAGE_PRIVATE, method);
       assertVisibility("", method);
   }
   
   @Test
   public void testMethodVisibilityWithSetter() throws Exception {
       JavaClass javaClass = JavaParser.create(JavaClass.class);       
       Method<JavaClass> method = javaClass.addMethod().setName("hello");
       assertVisibility("", method);
       
       method.setVisibility(Visibility.PUBLIC);
       assertVisibility("public", method);
       
       method.setVisibility(Visibility.PROTECTED);
       assertVisibility("protected", method);
       
       method.setVisibility(Visibility.PRIVATE);
       assertVisibility("private", method);
       
       method.setVisibility(Visibility.PACKAGE_PRIVATE);
       assertVisibility("", method);
   }
   
   private void assertVisibility(Visibility visibility, Method<JavaClass> method) {
       Assert.assertEquals(visibility, method.getVisibility());
   }

   private void assertVisibility(String visibility, Method<JavaClass> method) {
       Assert.assertEquals(visibility, method.getVisibility().toString());
   }
}
