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
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AddonManagerTest
{
   @Inject
   private AddonRegistry registry;

   @Inject
   private AddonManager addonManager;

   @Inject
   private AddonRepository repository;

   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class, "test-archive.jar")
               .addPackages(true, AddonManager.class.getPackage())
               .addAsLibraries(
                        Maven.resolver().offline().loadPomFromFile("pom.xml").importRuntimeDependencies().asFile())
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

      return archive;
   }

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

      Set<AddonDependency> dependencies = repository.getAddonDependencies(addon);
      Assert.assertEquals(1, dependencies.size());
      AddonDependency dependency = dependencies.toArray(new AddonDependency[dependencies.size()])[0];
      Assert.assertEquals("org.jboss.forge:example2", dependency
               .getId().getName());
      Assert.assertEquals("2.0.0-SNAPSHOT", dependency
               .getId().getVersion());
      Assert.assertTrue(dependency.isOptional());
      Assert.assertFalse(dependency.isExport());

      Thread.sleep(500);
      Assert.assertEquals(addonCount + 1, registry.getRegisteredAddons().size());
   }

   @Test
   public void testInstallingAddonWithTwoRequiredAddonDependency() throws InterruptedException
   {
      final int addonInitialCount = registry.getRegisteredAddons().size();
      AddonId resources = AddonId.fromCoordinates("org.jboss.forge:resources,2.0.0-SNAPSHOT");
      InstallRequest request = addonManager.install(resources);

      Assert.assertEquals(2, request.getRequiredAddons().size());
      Assert.assertEquals(0, request.getOptionalAddons().size());

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

      Set<AddonDependency> dependencies = repository.getAddonDependencies(resources);
      Assert.assertEquals(2, dependencies.size());
      List<String> addonDependenciesIds = new ArrayList<String>();
      addonDependenciesIds.add("org.jboss.forge:convert");
      addonDependenciesIds.add("org.jboss.forge:facets");

      final int addonDepsSize = addonDependenciesIds.size();

      for (AddonDependency dependency : dependencies)
      {
         Assert.assertTrue("Not a valid addon dependency: " + dependency.getId().getName(),
                  addonDependenciesIds.remove(dependency.getId().getName()));
         Assert.assertEquals("2.0.0-SNAPSHOT", dependency.getId().getVersion());
         Assert.assertFalse(dependency.isOptional());
      }
      Assert.assertTrue("Addons not detected as dependency: " + addonDependenciesIds, addonDependenciesIds.isEmpty());
      // FIXME Should this be true?
      // Assert.assertTrue(dependency.isExport());

      Thread.sleep(500);

      // The total registered addons is represented as the sum of the initial count, the addon dependencies and the
      // tested addon
      Assert.assertEquals(addonInitialCount + addonDepsSize + 1, registry.getRegisteredAddons().size());
   }
}
