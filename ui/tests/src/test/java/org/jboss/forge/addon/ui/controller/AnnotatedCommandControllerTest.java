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
import org.jboss.forge.addon.ui.controller.mock.EnabledHandlerCommand;
import org.jboss.forge.addon.ui.example.commands.ExampleAnnotatedCommand;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AnnotatedCommandControllerTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-example"),
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClass(EnabledHandlerCommand.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-example"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   UITestHarness testHarness;

   @Test
   public void testWizardExecution() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController("Annotation Commands: Number 1"))
      {
         controller.initialize();
         Assert.assertTrue(controller.isInitialized());
         Assert.assertEquals("Annotation Commands: Number 1", controller.getMetadata().getName());

         Assert.assertFalse(controller.isValid());
         controller.setValueFor("name", "Lincoln");
         Assert.assertTrue(controller.isValid());

         Assert.assertTrue(controller.getMetadata().getType().equals(ExampleAnnotatedCommand.class));
      }
   }

   @Test
   public void testWizardExecutionWithIncorrectlyAnnotatedCommand() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController("Annotation Commands: Number 3"))
      {
         controller.initialize();
         Assert.assertTrue(controller.isInitialized());
         Assert.assertEquals("Annotation Commands: Number 3", controller.getMetadata().getName());

         Assert.assertFalse(controller.isValid());
         controller.setValueFor("name", "Lincoln");
         controller.setValueFor("anyData", "SomethingElse");
         Assert.assertTrue(controller.isValid());

         Result result = controller.execute();
         Assert.assertTrue(!(result instanceof Failed));

         Assert.assertTrue(controller.getMetadata().getType().equals(ExampleAnnotatedCommand.class));
      }
   }

   @Test
   public void testCommandMetadata() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController("Annotation Commands: Number 1"))
      {
         UICommandMetadata metadata = controller.getMetadata();
         Assert.assertEquals("Annotation Commands: Number 1", metadata.getName());
         Assert.assertEquals(Categories.create("Root", "Branch"), metadata.getCategory());
      }
      try (CommandController controller = testHarness.createCommandController("Annotation Commands: Number 2"))
      {
         UICommandMetadata metadata = controller.getMetadata();
         Assert.assertEquals("Annotation Commands: Number 2", metadata.getName());
         Assert.assertEquals(Categories.createDefault(), metadata.getCategory());
      }
   }

   @Test
   public void testEnabledHandler() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController("enabled"))
      {
         Assert.assertTrue(controller.isEnabled());
      }
      try (CommandController controller = testHarness.createCommandController("disabled"))
      {
         Assert.assertFalse(controller.isEnabled());
      }
      try (CommandController controller = testHarness.createCommandController("gui"))
      {
         Assert.assertTrue(controller.isEnabled());
      }
      try (CommandController controller = testHarness.createCommandController("nongui"))
      {
         Assert.assertFalse(controller.isEnabled());
      }
   }

   @Test
   public void testBooleanCommand() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController("with-boolean-option"))
      {
         controller.initialize();
         Assert.assertEquals(Boolean.FALSE, controller.getValueFor("value"));
         controller.setValueFor("value", "true");
         Assert.assertTrue(controller.isValid());
         Result result = controller.execute();
         Assert.assertEquals("true", result.getMessage());
      }
   }

}
