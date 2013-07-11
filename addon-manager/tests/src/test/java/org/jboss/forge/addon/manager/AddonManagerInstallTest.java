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
import org.jboss.forge.addon.maven.dependencies.MavenDependencyResolver;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.util.Files;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
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
      System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, getAbsolutePath("profiles/settings.xml"));
      System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
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
      System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
      System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
   }

   private Furnace furnace;
   private AddonManager addonManager;
   private MavenDependencyResolver resolver;
   private File repository;
   private AddonRepository addonRepository;

   @Before
   public void setUp() throws IOException
   {
      furnace = ServiceLoader.load(Furnace.class).iterator().next();
      resolver = new MavenDependencyResolver();
      repository = File.createTempFile("furnace-repo", ".tmp");
      repository.delete();
      repository.mkdir();
      addonRepository = furnace.addRepository(AddonRepositoryMode.MUTABLE, repository);
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

   // UI Depends on convert, facets, ui-spi, environment
   @SuppressWarnings("unchecked")
   @Test
   public void testInstallUIAddon() throws IOException
   {
      InstallRequest install;
      AddonId addonUI = AddonId.from("org.jboss.forge.addon:ui", "2.0.0.Alpha6");
      install = addonManager.install(addonUI);
      List<?> actions = install.getActions();
      Assert.assertEquals(5, install.getActions().size());
      Assert.assertThat((List<DeployRequest>) actions, everyItem(isA(DeployRequest.class)));
      install.perform();
      install = addonManager.install(addonUI);
      // No actions should be needed, since we have all the needed addons
      Assert.assertEquals(0, install.getActions().size());
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

   @SuppressWarnings("unchecked")
   @Test
   public void testInstallAddonAddon()
   {
      // Addons depends directly on Projects, UI, Maven, parser-java, javaee
      // Addons depends indirectly on Environment, Dependencies, resources, facets, ui-spi, convert
      AddonId addon = AddonId.from("org.jboss.forge.addon:addons", "2.0.0.Alpha6");
      InstallRequest install = addonManager.install(addon);
      List<?> actions = install.getActions();
      Assert.assertEquals(12, actions.size());
      Assert.assertThat((List<DeployRequest>) actions, everyItem(isA(DeployRequest.class)));
   }

}
