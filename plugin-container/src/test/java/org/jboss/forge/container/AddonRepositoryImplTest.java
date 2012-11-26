/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container;

import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.junit.Assert;
import org.junit.Test;

public class AddonRepositoryImplTest
{

   @Test
   public void testMinorVersionCompatible() throws Exception
   {
      AddonEntry entry = AddonEntry.fromCoordinates("com.example.plugin,40,1.0.0-SNAPSHOT");
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.0.1.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.0.2.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.0.2000.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.0.2-SNAPSHOT", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.0.1000-SNAPSHOT", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.0.1000-adsfasfsd", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.1.0.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.1.1.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.2.0.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.2.1.Final", entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible("2.0.0.Final", entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible("s1.0.0.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(null, entry));
   }

   @Test
   public void testMinorVersionCompatibleBackwards() throws Exception
   {
      AddonEntry entry = AddonEntry.fromCoordinates("com.example.plugin,20.0i,1.1.0-SNAPSHOT");
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible("1.0.1.Final", entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible("1.0.2.Final", entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible("1.0.2000.Final", entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible("1.0.2-SNAPSHOT", entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible("1.0.1000-SNAPSHOT", entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible("1.0.1000-adsfasfsd", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.1.0.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.1.1.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.2.0.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("1.2.1.Final", entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible("2.0.0.Final", entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible("s1.0.0.Final", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible("", entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(null, entry));
   }

}
