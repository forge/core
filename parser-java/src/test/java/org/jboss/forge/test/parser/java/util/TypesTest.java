/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.test.parser.java.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.forge.parser.java.util.Types;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class TypesTest
{
   @Test
   public void testIsBasicType()
   {
      assertTrue(Types.isBasicType("String"));
      assertTrue(Types.isBasicType("Short"));
      assertTrue(Types.isBasicType("int"));
      assertTrue(Types.isBasicType("boolean"));
      assertTrue(Types.isBasicType("Boolean"));
      assertTrue(Types.isBasicType("long"));
      assertTrue(Types.isBasicType("Float"));
      assertTrue(Types.isBasicType("Double"));
   }

   @Test
   public void testAreEquivalent() throws Exception
   {
      assertTrue(Types.areEquivalent("com.example.Domain", "com.example.Domain"));
      assertTrue(Types.areEquivalent("Domain", "com.example.Domain"));
      assertTrue(Types.areEquivalent("com.example.Domain", "Domain"));
      assertTrue(Types.areEquivalent("Domain", "Domain"));
      assertFalse(Types.areEquivalent("com.example.Domain", "com.other.Domain"));
      assertTrue(Types.areEquivalent("com.example.Domain<T>", "Domain"));
      assertTrue(Types.areEquivalent("Domain<T>", "com.example.Domain"));
   }

   @Test
   public void testIsQualified() throws Exception
   {
      assertTrue(Types.isQualified("org.jboss.forge.parser.JavaParser"));
      assertFalse(Types.isQualified("JavaParser"));
   }

   @Test
   public void testGetPackage() throws Exception
   {
      assertEquals("com.example", Types.getPackage("com.example.Domain"));
      assertEquals("", Types.getPackage("Domain"));
   }

   @Test
   public void testIsSimpleName() throws Exception
   {
      assertTrue(Types.isSimpleName("Domain$"));
      assertFalse(Types.isSimpleName("9Domain$"));
      assertFalse(Types.isSimpleName("com.Domain$"));
      assertFalse(Types.isSimpleName("99"));
      assertFalse(Types.isSimpleName(""));
      assertFalse(Types.isSimpleName("Foo-bar"));
   }
}
