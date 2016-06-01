/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.controller;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.example.wizards.no_ui.NoUIWizard;
import org.jboss.forge.addon.ui.example.wizards.no_ui.WithInputWizard;
import org.jboss.forge.addon.ui.impl.mock.MockUIContext;
import org.jboss.forge.addon.ui.impl.mock.MockUIRuntime;
import org.jboss.forge.addon.ui.result.CompositeResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
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
public class CommandControllerFromUIExampleTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:ui-example"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi") })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addPackage(MockUIRuntime.class.getPackage())
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-example"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   private CommandControllerFactory controllerFactory;

   @Inject
   private NoUIWizard noUIWizard;

   @Inject
   private WithInputWizard withInputWizard;

   @Test
   public void testWizardNoUI() throws Exception
   {
      WizardCommandController controller = controllerFactory.createWizardController(new MockUIContext(),
               new MockUIRuntime(),
               noUIWizard);
      controller.initialize();
      Assert.assertFalse(controller.canMoveToNextStep());
      Assert.assertFalse(controller.canMoveToPreviousStep());
      Assert.assertThat(controller.getCommand(), instanceOf(WithInputWizard.class));
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, instanceOf(CompositeResult.class));
      List<Result> results = ((CompositeResult) result).getResults();
      Assert.assertEquals(3, results.size());
   }

   @Test
   public void testWizardWithNoUIStep() throws Exception
   {
      WizardCommandController controller = controllerFactory.createWizardController(new MockUIContext(),
               new MockUIRuntime(),
               withInputWizard);
      controller.initialize();
      Assert.assertFalse(controller.canMoveToNextStep());
      Assert.assertFalse(controller.canMoveToPreviousStep());
      Assert.assertThat(controller.getCommand(), instanceOf(WithInputWizard.class));
      Assert.assertTrue(controller.isValid());
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertThat(result, instanceOf(CompositeResult.class));
      List<Result> results = ((CompositeResult) result).getResults();
      Assert.assertEquals(2, results.size());
   }
}
