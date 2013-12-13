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
import org.jboss.forge.addon.ui.impl.mock.MockUIContext;
import org.jboss.forge.addon.ui.impl.mock.MockUIRuntime;
import org.jboss.forge.addon.ui.result.Result;
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
               .addPackage(MockUIRuntime.class.getPackage())
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

   @Inject
   private ExampleNoUICommand exampleNoUICommand;

   @Test
   public void testInjection() throws Exception
   {
      Assert.assertNotNull(controllerFactory);
   }

   @Test
   public void testSingleCommandController() throws Exception
   {
      CommandController controller = controllerFactory.createSingleController(new MockUIContext(), exampleCommand,
               new MockUIRuntime());
      Assert.assertTrue(controller.isEnabled());
      Assert.assertFalse(controller.isInitialized());

      controller.initialize();
      Assert.assertFalse(controller.getInputs().isEmpty());
      Assert.assertTrue(controller.hasInput("firstName"));
      controller.setValueFor("firstName", "Forge");
      Assert.assertEquals("Forge", controller.getValueFor("firstName"));
      Assert.assertTrue(controller.isValid());
      Result result = controller.execute();
      Assert.assertTrue(controller.isValid());
      Assert.assertEquals("Hello, Forge", result.getMessage());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testInitialized() throws Exception
   {
      CommandController controller = controllerFactory.createSingleController(new MockUIContext(), exampleCommand,
               new MockUIRuntime());
      Assert.assertFalse(controller.getInputs().isEmpty());
   }

   @Test
   public void testExampleNoUI() throws Exception
   {
      CommandController controller = controllerFactory.createSingleController(new MockUIContext(), exampleNoUICommand,
               new MockUIRuntime());
      controller.initialize();
      Assert.assertTrue(controller.getInputs().isEmpty());
      Assert.assertTrue(controller.isValid());
      Result result = controller.execute();
      Assert.assertEquals("Executed", result.getMessage());
   }

}
