/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.connections;

import java.util.Map;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.database.tools.connections.ui.RemoveConnectionProfileCommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RemoveConnectionProfileCommandTest
{

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:simple"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.addon:database-tools"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClass(MockConnectionProfileManagerImpl.class)
               .addAsServiceProvider(Service.class, RemoveConnectionProfileCommandTest.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:simple"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:database-tools"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"));
      return archive;
   }

   private ConnectionProfileManagerProvider provider;
   private ConnectionProfileManager manager;
   private UITestHarness testHarness;

   @Before
   public void setUp()
   {
      provider = SimpleContainer.getServices(getClass().getClassLoader(), ConnectionProfileManagerProvider.class).get();
      manager = new MockConnectionProfileManagerImpl();
      provider.setConnectionProfileManager(manager);
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
   }

   @Test
   public void testRemoveConnectionProfileCommand() throws Exception
   {
      CommandController command = testHarness.createCommandController(RemoveConnectionProfileCommand.class);
      command.initialize();
      command.setValueFor("names", "dummy");
      command.execute();
      Map<String, ConnectionProfile> profiles = manager.loadConnectionProfiles();
      Assert.assertEquals(0, profiles.size());
   }

}