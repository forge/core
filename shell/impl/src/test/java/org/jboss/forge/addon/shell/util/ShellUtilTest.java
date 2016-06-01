/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUtilTest
{
   /**
    * Test method for {@link org.jboss.forge.addon.shell.util.ShellUtil#shellifyOptionNameDashed(java.lang.String)}.
    */
   @Test
   public void testShellifyOptionName()
   {
      assertEquals("target-package", ShellUtil.shellifyOptionNameDashed("targetPackage"));
   }

   @Test
   public void testShellifyOptionNameWithMultipleCapitalWords()
   {
      assertEquals("java-ee-version", ShellUtil.shellifyOptionNameDashed("javaEEVersion"));
   }

   @Test
   public void testShellifyOptionNameMultipleWords()
   {
      assertEquals("ship-a-release", ShellUtil.shellifyOptionNameDashed("shipARelease"));
   }

   @Test
   public void testShellifyOptionNameWithSingleSpace()
   {
      assertEquals("another-parameter", ShellUtil.shellifyOptionNameDashed("Another Parameter"));
   }

   @Test
   public void testShellifyOptionNameWithMultipleSpaces()
   {
      assertEquals("java-ee-version", ShellUtil.shellifyOptionNameDashed("Java EE Version"));
   }

   @Test
   public void testShellifyOptionNameEndingWithCapitals()
   {
      assertEquals("java-ee", ShellUtil.shellifyOptionNameDashed("javaEE"));
   }

}
