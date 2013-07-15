/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.isA;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;

import org.jboss.forge.addon.manager.impl.AddonManagerImpl;
import org.jboss.forge.addon.manager.request.DeployRequest;
import org.jboss.forge.addon.manager.request.InstallRequest;
import org.jboss.forge.addon.manager.spi.AddonDependencyResolver;
import org.jboss.forge.addon.maven.addon.MavenAddonDependencyResolver;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.util.Files;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that depend on real addons
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class AddonManagerRealTest
{

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
      System.out.println(repository);
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
      install.perform();
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

}
