/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.furnace;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.jboss.forge.furnace.FurnaceImpl;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.impl.AddonRepositoryImpl;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.junit.Assert;
import org.junit.Test;

public class AddonRepositoryImplTest
{

   @Test
   public void testMinorVersionCompatible() throws Exception
   {
      AddonId entry = AddonId.fromCoordinates("com.example.plugin,40,1.0.0-SNAPSHOT");
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.1.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.2.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.2000.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.2-SNAPSHOT"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.1000-SNAPSHOT"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.1000-adsfasfsd"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.1.0.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.1.1.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.2.0.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.2.1.Final"), entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible(new SingleVersion("2.0.0.Final"), entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible(new SingleVersion("s1.0.0.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(null, entry));
   }

   @Test
   public void testMinorVersionCompatibleBackwards() throws Exception
   {
      AddonId entry = AddonId.fromCoordinates("com.example.plugin,20.0i,1.1.0-SNAPSHOT");
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.1.Final"), entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.2.Final"), entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.2000.Final"), entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.2-SNAPSHOT"), entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.1000-SNAPSHOT"), entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.0.1000-adsfasfsd"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.1.0.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.1.1.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.2.0.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion("1.2.1.Final"), entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible(new SingleVersion("2.0.0.Final"), entry));
      Assert.assertFalse(AddonRepositoryImpl.isApiCompatible(new SingleVersion("s1.0.0.Final"), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(new SingleVersion(""), entry));
      Assert.assertTrue(AddonRepositoryImpl.isApiCompatible(null, entry));
   }

   @Test
   public void testAddonDirNaming() throws Exception
   {
      AddonRepository repository = AddonRepositoryImpl.forDirectory(new FurnaceImpl(),
               File.createTempFile("addonDir", "test"));
      File dir = repository.getAddonBaseDir(AddonId.from("123#$%456", "!@#789*-0"));
      Assert.assertEquals("123-456-789-0", dir.getName());
   }

   @Test
   public void testDeployAddonEntryNoDependencies() throws Exception
   {
      MutableAddonRepository repository = AddonRepositoryImpl.forDirectory(new FurnaceImpl(),
               File.createTempFile("addonDir", "test"));

      AddonId addon = AddonId.from("1", "2");
      ArrayList<File> resourceJars = new ArrayList<File>();
      File tempJar = File.createTempFile("addon" + addon.getName(), ".jar");
      tempJar.createNewFile();
      resourceJars.add(tempJar);

      Assert.assertFalse(repository.isDeployed(addon));
      repository.deploy(addon, new ArrayList<AddonDependencyEntry>(), resourceJars);
      Assert.assertTrue(repository.isDeployed(addon));

      Assert.assertFalse(repository.isEnabled(addon));
      repository.enable(addon);
      Assert.assertTrue(repository.isEnabled(addon));

      Assert.assertEquals(0, repository.getAddonDependencies(addon).size());
   }

   @Test
   public void testDeployAddonEntryNoDependenciesOrResources() throws Exception
   {
      MutableAddonRepository repository = AddonRepositoryImpl.forDirectory(new FurnaceImpl(),
               File.createTempFile("addonDir", "test"));

      AddonId addon = AddonId.from("1", "2");

      Assert.assertFalse(repository.isDeployed(addon));
      repository.deploy(addon, new ArrayList<AddonDependencyEntry>(), new ArrayList<File>());
      Assert.assertFalse(repository.isDeployed(addon));

      Assert.assertFalse(repository.isEnabled(addon));
      repository.enable(addon);
      Assert.assertTrue(repository.isEnabled(addon));

      Assert.assertEquals(0, repository.getAddonDependencies(addon).size());
   }

   @Test
   public void testDeployAddonEntrySingleDependency() throws Exception
   {
      MutableAddonRepository repository = AddonRepositoryImpl.forDirectory(new FurnaceImpl(),
               File.createTempFile("addonDir", "test"));

      AddonId addon = AddonId.from("1", "2");
      AddonDependencyEntry dependency = AddonDependencyEntry.create(AddonId.from("nm", "ver"), false, true);
      repository.deploy(addon, Arrays.asList(dependency), new ArrayList<File>());

      Assert.assertEquals(1, repository.getAddonDependencies(addon).size());
      Assert.assertTrue(repository.getAddonDependencies(addon).contains(dependency));
   }

   @Test
   public void testDeployAddonEntryMultipleDependencies() throws Exception
   {
      MutableAddonRepository repository = AddonRepositoryImpl.forDirectory(new FurnaceImpl(),
               File.createTempFile("addonDir", "test"));

      AddonId addon = AddonId.from("1", "2");
      AddonDependencyEntry dependency0 = AddonDependencyEntry.create(AddonId.from("nm1", "ver"), true, false);
      AddonDependencyEntry dependency1 = AddonDependencyEntry.create(AddonId.from("nm2", "ver"));

      repository.deploy(addon, Arrays.asList(dependency0, dependency1), new ArrayList<File>());

      Assert.assertEquals(2, repository.getAddonDependencies(addon).size());
      Assert.assertTrue(repository.getAddonDependencies(addon).contains(dependency0));
      Assert.assertTrue(repository.getAddonDependencies(addon).contains(dependency1));
   }
}
