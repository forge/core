/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.test.parser.java;

import static org.junit.Assert.assertTrue;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.junit.Test;

public class TestOnGenerics
{

   @Test
   public void addAndRemoveGenericType() throws ClassNotFoundException
   {
      JavaClass javaClass = JavaParser.create(JavaClass.class);
      javaClass.setPackage("it.coopservice.test");
      javaClass.setName("SimpleClass");
      javaClass.setGenericType("T");
      assertTrue(javaClass.toString().contains("<T>"));
      javaClass.setGenericType(null);
      assertTrue(!javaClass.toString().contains("<T>"));
   }

   @Test
   public void addGenericSuperTypeWithPackage() throws ClassNotFoundException
   {
      JavaClass javaClass = JavaParser.create(JavaClass.class);
      javaClass.setPackage("it.coopservice.test");
      javaClass.setName("SimpleClass");
      javaClass.setSuperType("it.coopservice.test.Bar<T>");
      assertTrue(javaClass.toString().contains("Bar<T>"));
      assert (javaClass.getImport("it.coopservice.test.Bar") != null);
   }

   @Test
   public void addGenericSuperTypeWithoutPackage() throws ClassNotFoundException
   {
      JavaClass javaClass = JavaParser.create(JavaClass.class);
      javaClass.setPackage("it.coopservice.test");
      javaClass.setName("SimpleClass");
      javaClass.setSuperType("Bar<T>");
      assertTrue(javaClass.toString().contains("Bar<T>"));
      assert (javaClass.getImport("it.coopservice.test.Bar") == null);
   }

   @Test
   public void removeGenericSuperType() throws ClassNotFoundException
   {
      JavaClass javaClass = JavaParser.create(JavaClass.class);
      javaClass.addImport("it.coopservice.test.Bar");
      javaClass.setPackage("it.coopservice.test");
      javaClass.setName("SimpleClass");
      javaClass.setSuperType("Bar<T>");
      assertTrue(javaClass.toString().contains("Bar<T>"));
      javaClass.setSuperType("");
      assertTrue(!javaClass.toString().contains("Bar<T>"));
   }
}
