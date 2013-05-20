/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.impl.AddonManagerImpl;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.repositories.AddonRepositoryMode;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.util.Addons;
import org.jboss.forge.container.util.Files;
import org.jboss.forge.maven.addon.dependencies.FileResourceFactory;
import org.jboss.forge.maven.addon.dependencies.MavenContainer;
import org.jboss.forge.maven.addon.dependencies.MavenDependencyResolver;
import org.jboss.forge.se.init.ForgeFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MultipleRepositoryTest
{
   static File repodir1;
   static File repodir2;

   @Before
   public void init() throws IOException
   {
      repodir1 = File.createTempFile("forge", "repo1");
      repodir2 = File.createTempFile("forge", "repo2");
   }

   @After
   public void teardown()
   {
      Files.delete(repodir1, true);
      Files.delete(repodir2, true);
   }

   @Test
   public void testAddonsCanReferenceDependenciesInOtherRepositories() throws IOException
   {
      Forge forge = ForgeFactory.getInstance(Forge.class.getClassLoader());
      AddonRepository left = forge.addRepository(AddonRepositoryMode.MUTABLE, repodir1);
      AddonRepository right = forge.addRepository(AddonRepositoryMode.MUTABLE, repodir2);
      forge.startAsync();

      AddonManager manager = new AddonManagerImpl(forge, new MavenDependencyResolver(new FileResourceFactory(),
               new MavenContainer()));

      AddonId facets = AddonId.from("org.jboss.forge.addon:facets", "2.0.0-SNAPSHOT");
      AddonId convert = AddonId.from("org.jboss.forge.addon:convert", "2.0.0-SNAPSHOT");
      AddonId resources = AddonId.from("org.jboss.forge.addon:resources", "2.0.0-SNAPSHOT");

      Assert.assertFalse(left.isDeployed(resources));
      Assert.assertFalse(left.isDeployed(facets));
      Assert.assertFalse(left.isDeployed(convert));
      Assert.assertFalse(right.isDeployed(resources));
      Assert.assertFalse(right.isDeployed(facets));
      Assert.assertFalse(right.isDeployed(convert));

      manager.install(facets).perform(left);
      manager.install(convert).perform(left);
      manager.install(resources).perform(right);

      Assert.assertFalse(left.isDeployed(resources));
      Assert.assertFalse(right.isDeployed(convert));
      Assert.assertFalse(right.isDeployed(facets));
      Assert.assertTrue(left.isDeployed(convert));
      Assert.assertTrue(left.isDeployed(convert));
      Assert.assertTrue(right.isDeployed(resources));

      Addons.waitUntilStarted(forge.getAddonRegistry().getAddon(resources), 10, TimeUnit.SECONDS);

      forge.stop();
   }

   @Test
   public void testAddonsDontFailIfDuplicatedInOtherRepositories() throws IOException, Exception
   {
      Forge forge = ForgeFactory.getInstance(Forge.class.getClassLoader());
      AddonRepository left = forge.addRepository(AddonRepositoryMode.MUTABLE, repodir1);
      AddonRepository right = forge.addRepository(AddonRepositoryMode.MUTABLE, repodir2);

      AddonManager manager = new AddonManagerImpl(forge, new MavenDependencyResolver(new FileResourceFactory(),
               new MavenContainer()));

      AddonId facets = AddonId.from("org.jboss.forge.addon:facets", "2.0.0-SNAPSHOT");
      AddonId convert = AddonId.from("org.jboss.forge.addon:convert", "2.0.0-SNAPSHOT");
      AddonId resources = AddonId.from("org.jboss.forge.addon:resources", "2.0.0-SNAPSHOT");

      Assert.assertFalse(left.isDeployed(resources));
      Assert.assertFalse(left.isDeployed(facets));
      Assert.assertFalse(left.isDeployed(convert));
      Assert.assertFalse(right.isDeployed(resources));
      Assert.assertFalse(right.isDeployed(facets));
      Assert.assertFalse(right.isDeployed(convert));

      manager.install(facets).perform(left);
      manager.install(convert).perform(left);
      manager.install(resources).perform(left);
      manager.install(resources).perform(right);

      Assert.assertFalse(right.isDeployed(facets));
      Assert.assertFalse(right.isDeployed(convert));
      Assert.assertTrue(left.isDeployed(resources));
      Assert.assertTrue(left.isDeployed(convert));
      Assert.assertTrue(left.isDeployed(resources));
      Assert.assertTrue(right.isDeployed(resources));

      forge.startAsync();

      Addons.waitUntilStarted(forge.getAddonRegistry().getAddon(resources), 10, TimeUnit.SECONDS);
      Addons.waitUntilStarted(forge.getAddonRegistry().getAddon(facets), 10, TimeUnit.SECONDS);
      Addons.waitUntilStarted(forge.getAddonRegistry().getAddon(convert), 10, TimeUnit.SECONDS);

      System.out.println("Getting instances.");
      ExportedInstance<ConverterFactory> instance = forge.getAddonRegistry()
               .getExportedInstance(ConverterFactory.class);
      ConverterFactory factory = instance.get();

      factory.getConverter(File.class,
               forge.getAddonRegistry().getAddon(resources).getClassLoader()
                        .loadClass(DirectoryResource.class.getName()));

      forge.stop();
   }
}
