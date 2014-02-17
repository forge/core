/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.example.wizards.ChangesInputOneWizard;
import org.jboss.forge.addon.ui.example.wizards.ExampleStepOne;
import org.jboss.forge.addon.ui.example.wizards.ExampleStepTwo;
import org.jboss.forge.addon.ui.example.wizards.ExampleWizard;
import org.jboss.forge.addon.ui.example.wizards.subflow.ExampleFlow;
import org.jboss.forge.addon.ui.example.wizards.subflow.FlowOneOneStep;
import org.jboss.forge.addon.ui.example.wizards.subflow.FlowOneStep;
import org.jboss.forge.addon.ui.example.wizards.subflow.FlowTwoStep;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class WizardCommandControllerTest
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
      try (WizardCommandController controller = testHarness.createWizardController(ExampleWizard.class))
      {
         Assert.assertFalse(controller.isInitialized());
         controller.initialize();
         Assert.assertTrue(controller.isInitialized());

         Assert.assertFalse(controller.isValid());
         controller.setValueFor("firstName", "Forge");
         Assert.assertTrue(controller.isValid());

         Assert.assertFalse(controller.canMoveToPreviousStep());
         Assert.assertTrue(controller.canMoveToNextStep());
         // Going to example Step One
         controller.next();
         Assert.assertThat(controller.getCommand(), is(instanceOf(ExampleStepOne.class)));
         controller.initialize();
         Assert.assertTrue(controller.isInitialized());

         Assert.assertTrue(controller.canMoveToPreviousStep());
         Assert.assertFalse(controller.isValid());
         controller.setValueFor("address", "Foo street");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canMoveToNextStep());

         // Going back one page
         controller.previous();
         Assert.assertThat(controller.getCommand(), is(instanceOf(ExampleWizard.class)));
         controller.setValueFor("goToLastStep", Boolean.TRUE);

         // Should go to ExampleStepTwo
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next();
         controller.initialize();
         Assert.assertTrue(controller.isInitialized());
         Assert.assertThat(controller.getCommand(), is(instanceOf(ExampleStepTwo.class)));

      }
   }

   @Test
   public void testNormalWizardFlow() throws Exception
   {
      try (WizardCommandController controller = testHarness.createWizardController(ExampleFlow.class))
      {
         Assert.assertThat(controller.getCommand(), is(instanceOf(ExampleFlow.class)));
         controller.initialize();
         controller.setValueFor("name", "Forge");
         controller.setValueFor("number", 42);
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowOneStep.class)));
         controller.setValueFor("flowOneInput", "Value");
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowOneOneStep.class)));
         controller.setValueFor("flowOneOneInput", "Value Two");
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowTwoStep.class)));
         controller.setValueFor("flowTwoInput", "Value Three");
         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Assert.assertThat(controller.execute(), is(not(instanceOf(Failed.class))));
      }
   }

   @Test
   public void testStaleStepsWizardFlow() throws Exception
   {
      try (WizardCommandController controller = testHarness.createWizardController(ExampleFlow.class))
      {
         Assert.assertThat(controller.getCommand(), is(instanceOf(ExampleFlow.class)));
         controller.initialize();
         controller.setValueFor("name", "Forge");
         controller.setValueFor("number", 42);
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowOneStep.class)));
         controller.setValueFor("flowOneInput", "Value");
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowOneOneStep.class)));
         controller.setValueFor("flowOneOneInput", "Value Two");
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowTwoStep.class)));
         controller.setValueFor("flowTwoInput", "Value Three");
         Assert.assertTrue(controller.canMoveToPreviousStep());
         controller.previous().previous();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowOneStep.class)));
         controller.setValueFor("flowOneInput", "Changed Value");
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next();
         Assert.assertTrue("FlowOneOneStep shouldn't have been removed", controller.isInitialized());
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next();
         Assert.assertTrue("FlowTwoStep shouldn't have been removed", controller.isInitialized());

         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Assert.assertThat(controller.execute(), is(not(instanceOf(Failed.class))));
      }
   }

   @Test
   public void testSubflowOrder() throws Exception
   {
      try (WizardCommandController controller = testHarness.createWizardController(ExampleFlow.class))
      {
         Assert.assertThat(controller.getCommand(), is(instanceOf(ExampleFlow.class)));
         controller.initialize();
         controller.setValueFor("name", "Forge");
         controller.setValueFor("number", 42);
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowOneStep.class)));
         controller.setValueFor("flowOneInput", "Value");
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowOneOneStep.class)));
         controller.setValueFor("flowOneOneInput", "Value Two");
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowTwoStep.class)));
         controller.setValueFor("flowTwoInput", "Value Three");

         controller.previous().previous();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowOneStep.class)));
         controller.setValueFor("flowOneInput", "Changed Value");
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowOneOneStep.class)));
         controller.setValueFor("flowOneOneInput", "Value Two");
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next().initialize();
         Assert.assertThat(controller.getCommand(), is(instanceOf(FlowTwoStep.class)));
         controller.setValueFor("flowTwoInput", "Value Three");

         Assert.assertFalse(controller.canMoveToNextStep());
         Assert.assertTrue(controller.canExecute());
         Assert.assertThat(controller.execute(), is(not(instanceOf(Failed.class))));
      }
   }

   // FORGE-1372
   @Test
   public void testDynamicInputs() throws Exception
   {
      try (WizardCommandController controller = testHarness.createWizardController(ChangesInputOneWizard.class))
      {
         controller.initialize();
         controller.setValueFor("chooseInputTwo", Boolean.FALSE);
         controller.next().initialize();
         Assert.assertTrue("Should have added one input", controller.getInputs().size() == 1);
         Assert.assertTrue("Input inputOne not added", controller.getInputs().containsKey("inputOne"));
         Assert.assertTrue(controller.canMoveToPreviousStep());
         controller.previous();
         controller.setValueFor("chooseInputTwo", Boolean.TRUE);
         Assert.assertTrue(controller.canMoveToNextStep());
         controller.next().initialize();
         Assert.assertTrue("Should have added one input", controller.getInputs().size() == 1);
         Assert.assertTrue("Input inputTwo not added", controller.getInputs().containsKey("inputTwo"));
      }
   }

}
