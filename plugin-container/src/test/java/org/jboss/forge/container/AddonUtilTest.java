/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container;

import org.junit.Assert;
import org.junit.Test;

public class AddonUtilTest
{

   @Test
   public void testMinorVersionCompatible() throws Exception
   {
      AddonEntry entry = AddonEntry.fromCoordinates("com.example.plugin:1.0.0-SNAPSHOT:main");
      Assert.assertTrue(AddonUtil.isApiCompatible("1.0.1.Final", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.0.2.Final", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.0.2000.Final", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.0.2-SNAPSHOT", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.0.1000-SNAPSHOT", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.0.1000-adsfasfsd", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.1.0.Final", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.1.1.Final", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.2.0.Final", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.2.1.Final", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("2.0.0.Final", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("s1.0.0.Final", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("", entry));
   }

   @Test
   public void testMinorVersionCompatibleBackwards() throws Exception
   {
      AddonEntry entry = AddonEntry.fromCoordinates("com.example.plugin:1.1.0-SNAPSHOT:main");
      Assert.assertFalse(AddonUtil.isApiCompatible("1.0.1.Final", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("1.0.2.Final", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("1.0.2000.Final", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("1.0.2-SNAPSHOT", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("1.0.1000-SNAPSHOT", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("1.0.1000-adsfasfsd", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.1.0.Final", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.1.1.Final", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.2.0.Final", entry));
      Assert.assertTrue(AddonUtil.isApiCompatible("1.2.1.Final", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("2.0.0.Final", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("s1.0.0.Final", entry));
      Assert.assertFalse(AddonUtil.isApiCompatible("", entry));
   }

}
