/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonEntry;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
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
               .create(ForgeArchive.class)
               .addPackages(true, AddonManager.class.getPackage())
               .addAsLibraries(
                        Maven.resolver().offline().loadPomFromFile("pom.xml").importRuntimeDependencies().asFile())
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .setAsForgeXML(new StringAsset("<addon/>"));

      return archive;
   }

   @Test
   public void testResolvingAddon() throws InterruptedException
   {
      int addonCount = registry.getRegisteredAddons().size();
      AddonEntry addon = AddonEntry.fromCoordinates("org.jboss.forge:example,2.0.0-SNAPSHOT");
      Assert.assertTrue(addonManager.install(addon));
      Assert.assertTrue(repository.isEnabled(addon));
      Thread.sleep(500);
      Assert.assertEquals(addonCount + 1, registry.getRegisteredAddons().size());
   }
}
