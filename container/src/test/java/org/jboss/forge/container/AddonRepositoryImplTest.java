/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.jboss.forge.container.AddonDependency.ExportType;
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

   @Test
   public void testAddonDirNaming() throws Exception
   {
      AddonRepository repository = AddonRepositoryImpl.forDefaultDirectory();
      File dir = repository.getAddonBaseDir(AddonEntry.from("123#$%456", "!@#789*-0"));
      Assert.assertEquals("123-456-789-0", dir.getName());
   }

   @Test
   public void testDeployAddonEntryNoDependencies() throws Exception
   {
      AddonRepository repository = AddonRepositoryImpl.forDirectory(File.createTempFile("addonDir", "test"));

      AddonEntry addon = AddonEntry.from("1", "2");
      repository.deploy(addon, new ArrayList<AddonDependency>(), new ArrayList<File>());

      Assert.assertEquals(0, repository.getAddonDependencies(addon).size());
   }

   @Test
   public void testDeployAddonEntrySingleDependency() throws Exception
   {
      AddonRepository repository = AddonRepositoryImpl.forDirectory(File.createTempFile("addonDir", "test"));

      AddonEntry addon = AddonEntry.from("1", "2");
      AddonDependency dependency = AddonDependency.create(AddonEntry.from("nm", "ver"), ExportType.ONDEMAND, false);
      repository.deploy(addon, Arrays.asList(dependency), new ArrayList<File>());

      Assert.assertEquals(1, repository.getAddonDependencies(addon).size());
      Assert.assertEquals(dependency, repository.getAddonDependencies(addon).get(0));
   }

   @Test
   public void testDeployAddonEntryMultipleDependencies() throws Exception
   {
      AddonRepository repository = AddonRepositoryImpl.forDirectory(File.createTempFile("addonDir", "test"));

      AddonEntry addon = AddonEntry.from("1", "2");
      AddonDependency dependency0 = AddonDependency.create(AddonEntry.from("nm1", "ver"), ExportType.ONDEMAND, false);
      AddonDependency dependency1 = AddonDependency.create(AddonEntry.from("nm2", "ver"), ExportType.ONDEMAND, false);

      repository.deploy(addon, Arrays.asList(dependency0, dependency1), new ArrayList<File>());

      Assert.assertEquals(2, repository.getAddonDependencies(addon).size());
      Assert.assertEquals(dependency0, repository.getAddonDependencies(addon).get(0));
      Assert.assertEquals(dependency1, repository.getAddonDependencies(addon).get(1));
   }
}
