/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.versions.SingleVersion;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AddonManagerTest
{

   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:addon-manager", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:maven", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:addon-manager", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Inject
   private AddonManager addonManager;

   @Inject
   private AddonRepository repository;

   @Test
   public void testInstallingAddonWithSingleOptionalAddonDependency() throws InterruptedException
   {
      int addonCount = registry.getRegisteredAddons().size();
      AddonId addon = AddonId.fromCoordinates("org.jboss.forge:example,2.0.0-SNAPSHOT");
      InstallRequest request = addonManager.install(addon);

      Assert.assertEquals(0, request.getRequiredAddons().size());
      Assert.assertEquals(1, request.getOptionalAddons().size());

      request.perform();
      Thread.sleep(500);

      Assert.assertTrue(repository.isEnabled(addon));
      Assert.assertEquals(2, repository.getAddonResources(addon).size());
      Assert.assertTrue(repository.getAddonResources(addon).contains(
               new File(repository.getAddonBaseDir(addon), "commons-lang-2.6.jar")));
      Assert.assertTrue(repository.getAddonResources(addon).contains(
               new File(repository.getAddonBaseDir(addon), "example-2.0.0-SNAPSHOT-forge-addon.jar")));

      Set<AddonDependencyEntry> dependencies = repository.getAddonDependencies(addon);
      Assert.assertEquals(1, dependencies.size());
      AddonDependencyEntry dependency = dependencies.toArray(new AddonDependencyEntry[dependencies.size()])[0];
      Assert.assertEquals("org.jboss.forge:example2", dependency
               .getId().getName());
      Assert.assertEquals(new SingleVersion("2.0.0-SNAPSHOT"), dependency
               .getId().getVersion());
      Assert.assertTrue(dependency.isOptional());
      Assert.assertFalse(dependency.isExported());

      Assert.assertFalse(registry.isRegistered(AddonId.from("org.jboss.forge:example2", "2.0.0-SNAPSHOT")));

      Thread.sleep(500);
      Assert.assertEquals(addonCount + 1, registry.getRegisteredAddons().size());
   }

   @Test
   public void testInstallingAlreadyInstalledAddonWithTwoRequiredAddonDependency() throws InterruptedException
   {
      final int addonInitialCount = registry.getRegisteredAddons().size();
      AddonId resources = AddonId.fromCoordinates("org.jboss.forge:resources,2.0.0-SNAPSHOT");
      InstallRequest request = addonManager.install(resources);

      Assert.assertEquals(1, request.getRequiredAddons().size());
      Assert.assertEquals(2, request.getOptionalAddons().size());

      request.perform();

      Assert.assertTrue(repository.isEnabled(resources));
      Assert.assertEquals(3, repository.getAddonResources(resources).size());
      Assert.assertTrue(repository.getAddonResources(resources).contains(
               new File(repository.getAddonBaseDir(resources), "resources-2.0.0-SNAPSHOT-forge-addon.jar")));
      Assert.assertTrue(repository.getAddonResources(resources).contains(
               new File(repository.getAddonBaseDir(resources), "resources-api-2.0.0-SNAPSHOT.jar")));
      Assert.assertTrue(repository.getAddonResources(resources).contains(
               new File(repository.getAddonBaseDir(resources), "resources-impl-2.0.0-SNAPSHOT.jar")));

      AddonId facets = AddonId.from("org.jboss.forge:facets", "2.0.0-SNAPSHOT");
      Assert.assertTrue(repository.getAddonResources(facets)
               .contains(new File(repository.getAddonBaseDir(facets), "facets-2.0.0-SNAPSHOT-forge-addon.jar")));
      Assert.assertTrue(repository.getAddonResources(facets)
               .contains(new File(repository.getAddonBaseDir(facets), "facets-api-2.0.0-SNAPSHOT.jar")));

      Set<AddonDependencyEntry> dependencies = repository.getAddonDependencies(resources);
      Assert.assertEquals(3, dependencies.size());
      List<String> addonDependenciesIds = new ArrayList<String>();
      addonDependenciesIds.add("org.jboss.forge:convert");
      addonDependenciesIds.add("org.jboss.forge:facets");
      addonDependenciesIds.add("org.jboss.forge:ui-hints");

      for (AddonDependencyEntry dependency : dependencies)
      {
         Assert.assertTrue("Not a valid addon dependency: " + dependency.getId().getName(),
                  addonDependenciesIds.remove(dependency.getId().getName()));
         Assert.assertEquals(new SingleVersion("2.0.0-SNAPSHOT"), dependency.getId().getVersion());
      }
      Assert.assertTrue("Addons not detected as dependency: " + addonDependenciesIds, addonDependenciesIds.isEmpty());

      Thread.sleep(500);

      Assert.assertEquals(addonInitialCount, registry.getRegisteredAddons().size());
   }
}
