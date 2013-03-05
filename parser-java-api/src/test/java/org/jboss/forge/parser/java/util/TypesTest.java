/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.parser.java.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TypesTest
{

   @Test
   public void testStringIsJavaLang()
   {
      assertTrue(Types.isJavaLang("String"));
   }

   @Test
   public void testAssertClassIsNotJavaLang()
   {
      assertFalse(Types.isJavaLang("AssertClass"));
   }
}
