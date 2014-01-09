/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.database.tools.connections;

import java.util.Map;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManager;
import org.jboss.forge.addon.database.tools.connections.RemoveConnectionProfileCommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.ui.test.UITestHarness;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RemoveConnectionProfileCommandTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:database-tools"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:database-tools"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"))
               .addClass(MockConnectionProfileManagerImpl.class);
      return archive;
   }

   @Inject
   private ConnectionProfileManager manager;
   
   @Inject
   private UITestHarness testHarness;

   @Ignore
   @Test
   public void testRemoveConnectionProfileCommand() throws Exception {
	  CommandController command = testHarness.createCommandController(RemoveConnectionProfileCommand.class);
	  command.initialize();
      command.setValueFor("names", "dummy");
      command.execute();
      Map<String, ConnectionProfile> profiles = manager.loadConnectionProfiles();
      Assert.assertEquals(0, profiles.size());
   }
   
}