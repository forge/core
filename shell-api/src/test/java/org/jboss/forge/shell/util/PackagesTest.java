/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.util;

import org.junit.Assert;
import org.junit.Test;

public class PackagesTest
{

   @Test
   public void testToValidPackage()
   {
      String pkg = "com.example.app";
      Assert.assertEquals(pkg, Packages.toValidPackageName(pkg));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullPackage()
   {
      String pkg = null;
      Packages.toValidPackageName(pkg);
   }

   @Test
   public void testToInvalidPackage()
   {
      String pkg = "com.example.app-demo";
      Assert.assertEquals("com.example.appdemo", Packages.toValidPackageName(pkg));
   }

}
