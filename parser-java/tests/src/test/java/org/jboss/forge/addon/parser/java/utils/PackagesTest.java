/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.utils;

import static org.jboss.forge.addon.parser.java.utils.Packages.toValidPackageName;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link Packages} utility class
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class PackagesTest
{
   @Test
   public void testToValidPackageName()
   {
      Assert.assertEquals("org.agoncal.training.javaee6adv", toValidPackageName("org.agoncal.training.javaee6adv"));
   }

   @Test
   public void testToValidPackageNameWithReservedWords()
   {
      Assert.assertEquals("for_", toValidPackageName("for"));
      Assert.assertEquals("something.for_.burr", toValidPackageName("something.for.burr"));
      Assert.assertEquals("something.true_", toValidPackageName("something.true"));
      Assert.assertEquals("native_", toValidPackageName("native"));
   }

   @Test
   public void testToValidPackageNameWithDotBeginning()
   {
      Assert.assertEquals("org.example", toValidPackageName(".org.example"));
   }

   @Test
   public void testToValidPackageNameWithDotEnd()
   {
      Assert.assertEquals("org.example", toValidPackageName("org.example."));
   }

   @Test
   public void testEmptyPackage()
   {
      Assert.assertEquals("", toValidPackageName(""));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullPackage()
   {
      toValidPackageName(null);
   }

   @Test
   public void testToValidPackageNameWithNumbers()
   {
      Assert.assertEquals("org.forge", toValidPackageName("org.forge.2"));
      Assert.assertEquals("org.forge", toValidPackageName("org.forge.2.34.56"));
      Assert.assertEquals("", toValidPackageName("1.2.3.4.5.6"));
   }

}
