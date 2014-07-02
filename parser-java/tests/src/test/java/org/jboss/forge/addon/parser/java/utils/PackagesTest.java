/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class PackagesTest
{

   @Test
   public void testToValidPackageNameWithReservedWords()
   {
      Assert.assertEquals("something.for_.burr", Packages.toValidPackageName("something.for.burr"));
      Assert.assertEquals("something.true_", Packages.toValidPackageName("something.true"));
      Assert.assertEquals("native_", Packages.toValidPackageName("native"));
   }

   @Test
   public void testToValidPackageNameWithDotBeginning()
   {
      String pkg = ".org.example";
      String validPkg = Packages.toValidPackageName(pkg);
      Assert.assertEquals("org.example", validPkg);
   }

   @Test
   public void testToValidPackageNameWithDotEnd()
   {
      String pkg = "org.example.";
      String validPkg = Packages.toValidPackageName(pkg);
      Assert.assertEquals("org.example", validPkg);
   }

}
