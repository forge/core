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

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.impl.AddonManagerImpl;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.repositories.AddonRepositoryMode;
import org.jboss.forge.container.util.Addons;
import org.jboss.forge.container.util.Files;
import org.jboss.forge.maven.dependencies.FileResourceFactory;
import org.jboss.forge.maven.dependencies.MavenContainer;
import org.jboss.forge.maven.dependencies.MavenDependencyResolver;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MultipleRepositoryTest
{
   static File repodir1;
   static File repodir2;

   @BeforeClass
   public static void init() throws IOException
   {
      repodir1 = File.createTempFile("forge", "repo1");
      repodir2 = File.createTempFile("forge", "repo2");
   }

   @AfterClass
   public static void teardown()
   {
      Files.delete(repodir1, true);
      Files.delete(repodir2, true);
   }

   @Test
   public void testInstallIntoMultipleRepositoriesDefaultsToFirst() throws IOException
   {
      Forge forge = new ForgeImpl();
      AddonRepository left = forge.addRepository(AddonRepositoryMode.MUTABLE, repodir1);
      AddonRepository right = forge.addRepository(AddonRepositoryMode.MUTABLE, repodir2);
      forge.startAsync();

      AddonManager manager = new AddonManagerImpl(forge, new MavenDependencyResolver(new FileResourceFactory(),
               new MavenContainer()));

      AddonId facets = AddonId.from("org.jboss.forge:facets", "2.0.0-SNAPSHOT");
      AddonId convert = AddonId.from("org.jboss.forge:convert", "2.0.0-SNAPSHOT");
      AddonId resources = AddonId.from("org.jboss.forge:resources", "2.0.0-SNAPSHOT");

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
   }
}
