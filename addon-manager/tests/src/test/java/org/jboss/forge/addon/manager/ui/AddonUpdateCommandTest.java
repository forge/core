/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.ui;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.manager.impl.ui.AddonCommandConstants;
import org.jboss.forge.addon.manager.impl.ui.AddonUpdateCommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.maven.MavenContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AddonUpdateCommandTest
{

   private static String previousUserSettings;
   private static String previousLocalRepository;

   @BeforeClass
   public static void setRemoteRepository() throws IOException
   {
      previousUserSettings = System.setProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION,
               getAbsolutePath("profiles/settings.xml"));
      previousLocalRepository = System.setProperty(MavenContainer.ALT_LOCAL_REPOSITORY_LOCATION,
               "target/the-other-repository");
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
      if (previousUserSettings == null)
      {
         System.clearProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION);
      }
      else
      {
         System.setProperty(MavenContainer.ALT_USER_SETTINGS_XML_LOCATION, previousUserSettings);
      }
      if (previousLocalRepository == null)
      {
         System.clearProperty(MavenContainer.ALT_LOCAL_REPOSITORY_LOCATION);
      }
      else
      {
         System.setProperty(MavenContainer.ALT_LOCAL_REPOSITORY_LOCATION, previousUserSettings);
      }
   }

   private Furnace furnace;
   private UITestHarness uiTestHarness;
   private AddonManager addonManager;

   @Before
   public void setUp()
   {
      furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
      uiTestHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
      addonManager = SimpleContainer.getServices(getClass().getClassLoader(), AddonManager.class).get();

   }

   private static final String ADDON_NAME_TO_UPDATE = "test:no_dep";

   @Test
   public void testUpdateAddon() throws Exception
   {
      AddonId exampleId = AddonId.fromCoordinates(ADDON_NAME_TO_UPDATE + ",1.0.0.Final");
      addonManager.deploy(exampleId).perform();

      try (CommandController controller = uiTestHarness.createCommandController(AddonUpdateCommand.class))
      {
         controller.initialize();
         UICommandMetadata metadata = controller.getMetadata();
         assertEquals(AddonCommandConstants.ADDON_UPDATE_COMMAND_NAME, metadata.getName());
         assertEquals(1, controller.getInputs().size());
         controller.setValueFor("named", exampleId.getName());
         Result result = controller.execute();
         Assert.assertThat(result, allOf(notNullValue(), not(instanceOf(Failed.class))));
      }

      String desiredAddonIdName = ADDON_NAME_TO_UPDATE;
      boolean found = furnace.getAddonRegistry().getAddons().stream()
               .anyMatch((addon) -> (addon.getId().getName().equals(desiredAddonIdName)
                        && addon.getId().getVersion().compareTo(exampleId.getVersion()) > 0));
      Assert.assertTrue("Addon was not updated", found);
   }
}