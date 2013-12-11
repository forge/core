/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.impl.mock.MockUIContextFactory;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for the {@link CommandController} feature
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class CommandControllerTest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(ExampleCommand.class, ExampleNoUICommand.class)
               .addPackage(MockUIContextFactory.class.getPackage())
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private CommandControllerFactory controllerFactory;

   @Inject
   private ExampleCommand exampleCommand;

   @Test
   public void testInjection() throws Exception
   {
      Assert.assertNotNull(controllerFactory);
   }

   @Test
   public void testSingleCommandController() throws Exception
   {
      CommandController controller = controllerFactory.createSingleController(exampleCommand,
               new MockUIContextFactory());
      Assert.assertTrue(controller.isEnabled());
      Assert.assertFalse(controller.isInitialized());
      Assert.assertFalse(controller.getInputs().isEmpty());
   }

}
