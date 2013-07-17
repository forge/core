/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.isA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ServiceLoader;

import org.jboss.forge.addon.manager.impl.AddonManagerImpl;
import org.jboss.forge.addon.manager.request.AddonActionRequest;
import org.jboss.forge.addon.manager.request.DeployRequest;
import org.jboss.forge.addon.manager.request.InstallRequest;
import org.jboss.forge.addon.manager.request.UpdateRequest;
import org.jboss.forge.addon.manager.spi.AddonDependencyResolver;
import org.jboss.forge.addon.maven.MavenContainer;
import org.jboss.forge.addon.maven.addon.MavenAddonDependencyResolver;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.util.Files;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AddonManagerInstallTest
{

   @BeforeClass
   public static void setRemoteRepository() throws IOException
   {
      System.setProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION, getAbsolutePath("profiles/settings.xml"));
      System.setProperty(MavenContainer.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
   }

   private static String getAbsolutePath(String path) throws FileNotFoundException
   {
      URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
      if (resource == null)
         throw new FileNotFoundException(path);
      return resource.getFile();
   }

   @AfterClass
   public static void clearRemoteRepository()
   {
      System.clearProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION);
      System.clearProperty(MavenContainer.ALT_LOCAL_REPOSITORY_LOCATION);
   }

   private Furnace furnace;
   private AddonManager addonManager;
   private AddonDependencyResolver resolver;
   private File repository;

   @Before
   public void setUp() throws IOException
   {
      furnace = ServiceLoader.load(Furnace.class).iterator().next();
      resolver = new MavenAddonDependencyResolver();
      repository = File.createTempFile("furnace-repo", ".tmp");
      repository.delete();
      repository.mkdir();
      furnace.addRepository(AddonRepositoryMode.MUTABLE, repository);
      addonManager = new AddonManagerImpl(furnace, resolver);
   }

   @After
   public void tearDown()
   {
      if (repository != null && !Files.delete(repository, true))
      {
         System.err.println("Could not delete " + repository);
      }
   }

   @Test
   public void testAddonInstallNoDependencyWithEmptyRepository() throws IOException
   {
      AddonId addon = AddonId.from("test:no_dep", "1.0.0.Final");
      InstallRequest install = addonManager.install(addon);
      List<? extends AddonActionRequest> actions = install.getActions();
      Assert.assertEquals(1, actions.size());
      Assert.assertThat(actions.get(0), instanceOf(DeployRequest.class));
   }

   @Test
   public void testAddonInstallNoDependencyWithAddonAlreadyInstalled() throws IOException
   {
      AddonId addon = AddonId.from("test:no_dep", "1.0.0.Final");
      InstallRequest install = addonManager.install(addon);
      install.perform();
      install = addonManager.install(addon);
      Assert.assertTrue(install.getActions().isEmpty());
   }

   @Test
   public void testAddonInstallSnapshot() throws IOException
   {
      AddonId addon = AddonId.from("test:no_dep", "1.1.2-SNAPSHOT");
      InstallRequest install = addonManager.install(addon);
      Assert.assertEquals(1, install.getActions().size());
      install.perform();
      install = addonManager.install(addon);
      List<? extends AddonActionRequest> actions = install.getActions();
      Assert.assertEquals(1, actions.size());
      Assert.assertThat(actions.get(0), instanceOf(UpdateRequest.class));
   }

   @Test
   public void testAddonUpdate() throws IOException
   {
      AddonId addon = AddonId.from("test:one_dep", "1.0.0.Final");
      InstallRequest install = addonManager.install(addon);
      Assert.assertEquals(2, install.getActions().size());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testInstallTwoDeps() throws IOException
   {
      AddonId addon = AddonId.from("test:one_dep", "1.0.0.Final");
      InstallRequest install = addonManager.install(addon);
      List<?> actions = install.getActions();
      Assert.assertEquals(2, actions.size());
      Assert.assertThat((List<DeployRequest>) actions, everyItem(isA(DeployRequest.class)));
   }
}
