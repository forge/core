/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
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

      Assert.assertTrue(repository.isEnabled(addon));
      Assert.assertEquals(2, repository.getAddonResources(addon).size());
      Assert.assertTrue(repository.getAddonResources(addon).contains(
               new File(repository.getAddonBaseDir(addon), "commons-lang-2.6.jar")));
      Assert.assertTrue(repository.getAddonResources(addon).contains(
               new File(repository.getAddonBaseDir(addon), "example-2.0.0-SNAPSHOT-forge-addon.jar")));

      Thread.sleep(500);
      Assert.assertEquals(addonCount + 1, registry.getRegisteredAddons().size());
   }
}
